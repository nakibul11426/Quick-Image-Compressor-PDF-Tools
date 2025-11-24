package com.nakibul.hassan.quickcompress.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import com.nakibul.hassan.quickcompress.domain.model.PageSize
import com.nakibul.hassan.quickcompress.domain.model.PdfSettings
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

object PdfUtils {
    
    fun createPdfFromImages(
        context: Context,
        imageBitmaps: List<Bitmap>,
        outputFile: File,
        pdfSettings: PdfSettings
    ): Boolean {
        return try {
            Timber.d("createPdfFromImages: Creating PDF with ${imageBitmaps.size} images")
            Timber.d("createPdfFromImages: Output file: ${outputFile.absolutePath}")
            Timber.d("createPdfFromImages: PDF settings: $pdfSettings")
            
            if (imageBitmaps.isEmpty()) {
                Timber.e("createPdfFromImages: No images to create PDF")
                return false
            }
            
            // Calculate uniform page size for all images
            val uniformPageSize = calculateUniformPageSize(imageBitmaps, pdfSettings)
            Timber.d("createPdfFromImages: Using uniform page size: ${uniformPageSize.width}x${uniformPageSize.height}")
            
            val pdfDocument = PdfDocument()
            
            imageBitmaps.forEachIndexed { index, bitmap ->
                Timber.d("createPdfFromImages: Processing page ${index + 1}: ${bitmap.width}x${bitmap.height}")
                
                // Use uniform page size for all pages
                val pageInfo = PdfDocument.PageInfo.Builder(
                    uniformPageSize.width,
                    uniformPageSize.height,
                    index + 1
                ).create()
                
                val page = pdfDocument.startPage(pageInfo)
                drawBitmapOnPage(page.canvas, bitmap, pdfSettings)
                pdfDocument.finishPage(page)
            }
            
            FileOutputStream(outputFile).use { out ->
                pdfDocument.writeTo(out)
            }
            
            pdfDocument.close()
            
            val fileSize = outputFile.length()
            Timber.d("createPdfFromImages: PDF created successfully, size: $fileSize bytes")
            true
        } catch (e: Exception) {
            Timber.e(e, "Error creating PDF from images")
            false
        }
    }
    
    private data class PageDimensions(val width: Int, val height: Int)
    
    private fun calculateUniformPageSize(
        imageBitmaps: List<Bitmap>,
        pdfSettings: PdfSettings
    ): PageDimensions {
        return when (pdfSettings.pageSize) {
            PageSize.FIT_TO_IMAGE -> {
                // Find the maximum dimensions from all images to create uniform pages
                val maxWidth = imageBitmaps.maxOf { it.width }
                val maxHeight = imageBitmaps.maxOf { it.height }
                
                // Use the largest dimensions, maintaining a reasonable aspect ratio
                // This ensures all images fit without distortion
                PageDimensions(maxWidth, maxHeight)
            }
            else -> {
                // Use standard page size for all pages
                PageDimensions(
                    pdfSettings.pageSize.widthPx,
                    pdfSettings.pageSize.heightPx
                )
            }
        }
    }
    
    private fun drawBitmapOnPage(
        canvas: Canvas,
        bitmap: Bitmap,
        pdfSettings: PdfSettings
    ) {
        val margin = pdfSettings.margins.marginPx.toFloat()
        val availableWidth = canvas.width - (2 * margin)
        val availableHeight = canvas.height - (2 * margin)
        
        val scale = minOf(
            availableWidth / bitmap.width,
            availableHeight / bitmap.height
        )
        
        val scaledWidth = bitmap.width * scale
        val scaledHeight = bitmap.height * scale
        
        val left = margin + (availableWidth - scaledWidth) / 2
        val top = margin + (availableHeight - scaledHeight) / 2
        
        val destRect = RectF(
            left,
            top,
            left + scaledWidth,
            top + scaledHeight
        )
        
        canvas.drawBitmap(bitmap, null, destRect, null)
    }
    
    fun mergePdfs(
        context: Context,
        pdfUris: List<Uri>,
        outputFile: File
    ): Boolean {
        return try {
            val mergedDocument = PdfDocument()
            var currentPageNumber = 0
            
            pdfUris.forEach { uri ->
                val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
                parcelFileDescriptor?.use { pfd ->
                    val renderer = PdfRenderer(pfd)
                    
                    for (i in 0 until renderer.pageCount) {
                        renderer.openPage(i).use { page ->
                            val bitmap = Bitmap.createBitmap(
                                page.width,
                                page.height,
                                Bitmap.Config.ARGB_8888
                            )
                            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                            
                            val pageInfo = PdfDocument.PageInfo.Builder(
                                page.width,
                                page.height,
                                currentPageNumber + 1
                            ).create()
                            
                            val pdfPage = mergedDocument.startPage(pageInfo)
                            pdfPage.canvas.drawBitmap(bitmap, 0f, 0f, null)
                            mergedDocument.finishPage(pdfPage)
                            
                            bitmap.recycle()
                            currentPageNumber++
                        }
                    }
                    
                    renderer.close()
                }
            }
            
            FileOutputStream(outputFile).use { out ->
                mergedDocument.writeTo(out)
            }
            
            mergedDocument.close()
            true
        } catch (e: Exception) {
            Timber.e(e, "Error merging PDFs")
            false
        }
    }
    
    fun splitPdf(
        context: Context,
        pdfUri: Uri,
        pageRanges: List<IntRange>,
        outputFiles: List<File>
    ): Boolean {
        return try {
            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(pdfUri, "r")
            parcelFileDescriptor?.use { pfd ->
                val renderer = PdfRenderer(pfd)
                
                pageRanges.forEachIndexed { index, range ->
                    val splitDocument = PdfDocument()
                    var pageNum = 0
                    
                    range.forEach { pageIndex ->
                        if (pageIndex < renderer.pageCount) {
                            renderer.openPage(pageIndex).use { page ->
                                val bitmap = Bitmap.createBitmap(
                                    page.width,
                                    page.height,
                                    Bitmap.Config.ARGB_8888
                                )
                                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                                
                                val pageInfo = PdfDocument.PageInfo.Builder(
                                    page.width,
                                    page.height,
                                    pageNum + 1
                                ).create()
                                
                                val pdfPage = splitDocument.startPage(pageInfo)
                                pdfPage.canvas.drawBitmap(bitmap, 0f, 0f, null)
                                splitDocument.finishPage(pdfPage)
                                
                                bitmap.recycle()
                                pageNum++
                            }
                        }
                    }
                    
                    FileOutputStream(outputFiles[index]).use { out ->
                        splitDocument.writeTo(out)
                    }
                    splitDocument.close()
                }
                
                renderer.close()
            }
            true
        } catch (e: Exception) {
            Timber.e(e, "Error splitting PDF")
            false
        }
    }
    
    fun getPdfPageCount(context: Context, uri: Uri): Int {
        return try {
            context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
                val renderer = PdfRenderer(pfd)
                val count = renderer.pageCount
                renderer.close()
                count
            } ?: 0
        } catch (e: Exception) {
            Timber.e(e, "Error getting PDF page count")
            0
        }
    }
    
    fun renderPdfPage(context: Context, uri: Uri, pageIndex: Int): Bitmap? {
        return try {
            context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
                val renderer = PdfRenderer(pfd)
                
                if (pageIndex >= renderer.pageCount) {
                    renderer.close()
                    return null
                }
                
                renderer.openPage(pageIndex).use { page ->
                    val bitmap = Bitmap.createBitmap(
                        page.width,
                        page.height,
                        Bitmap.Config.ARGB_8888
                    )
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    renderer.close()
                    bitmap
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error rendering PDF page")
            null
        }
    }
}
