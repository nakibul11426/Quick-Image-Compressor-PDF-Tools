package com.nakibul.hassan.quickcompress.data.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.nakibul.hassan.quickcompress.domain.repository.PdfRepository
import com.nakibul.hassan.quickcompress.utils.FileUtils
import com.nakibul.hassan.quickcompress.utils.ImageUtils
import com.nakibul.hassan.quickcompress.utils.PdfUtils
import com.nakibul.hassan.quickcompress.domain.model.PdfDocument
import com.nakibul.hassan.quickcompress.domain.model.PdfPage
import com.nakibul.hassan.quickcompress.domain.model.PdfSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class PdfRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PdfRepository {
    
    override suspend fun createPdfFromImages(
        imageUris: List<Uri>,
        pdfSettings: PdfSettings,
        outputFileName: String
    ): Uri {
        val bitmaps = imageUris.mapNotNull { uri ->
            ImageUtils.getBitmapFromUri(context, uri)
        }
        
        if (bitmaps.isEmpty()) {
            throw IllegalStateException("No valid images to create PDF")
        }
        
        val outputDir = FileUtils.getPdfsDir(context)
        val fileName = FileUtils.generateUniqueFileName(outputFileName)
        val outputFile = File(outputDir, fileName)
        
        val success = PdfUtils.createPdfFromImages(context, bitmaps, outputFile, pdfSettings)
        
        bitmaps.forEach { it.recycle() }
        
        if (!success) {
            throw IllegalStateException("Failed to create PDF")
        }
        
        return Uri.fromFile(outputFile)
    }
    
    override suspend fun mergePdfs(
        pdfUris: List<Uri>,
        outputFileName: String
    ): Uri {
        val outputDir = FileUtils.getPdfsDir(context)
        val fileName = FileUtils.generateUniqueFileName(outputFileName)
        val outputFile = File(outputDir, fileName)
        
        val success = PdfUtils.mergePdfs(context, pdfUris, outputFile)
        
        if (!success) {
            throw IllegalStateException("Failed to merge PDFs")
        }
        
        return Uri.fromFile(outputFile)
    }
    
    override suspend fun splitPdf(
        pdfUri: Uri,
        pageRanges: List<IntRange>,
        outputFilePrefix: String
    ): List<Uri> {
        val outputDir = FileUtils.getPdfsDir(context)
        
        val outputFiles = pageRanges.mapIndexed { index, _ ->
            val fileName = "${outputFilePrefix}_part${index + 1}_${System.currentTimeMillis()}.pdf"
            File(outputDir, fileName)
        }
        
        val success = PdfUtils.splitPdf(context, pdfUri, pageRanges, outputFiles)
        
        if (!success) {
            throw IllegalStateException("Failed to split PDF")
        }
        
        return outputFiles.map { Uri.fromFile(it) }
    }
    
    override suspend fun getPdfDetails(uri: Uri): PdfDocument {
        val name = FileUtils.getFileName(context, uri)
        val size = FileUtils.getFileSize(context, uri)
        val pageCount = PdfUtils.getPdfPageCount(context, uri)
        
        return PdfDocument(
            uri = uri,
            name = name,
            size = size,
            pageCount = pageCount
        )
    }
    
    override suspend fun getPdfPages(uri: Uri): List<PdfPage> {
        val pageCount = PdfUtils.getPdfPageCount(context, uri)
        
        return (0 until pageCount).map { pageIndex ->
            val thumbnail = PdfUtils.renderPdfPage(context, uri, pageIndex)
            PdfPage(
                pageNumber = pageIndex + 1,
                thumbnail = thumbnail,
                isSelected = false
            )
        }
    }
    
    override fun sharePdf(uri: Uri) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "application/pdf"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        val chooser = Intent.createChooser(shareIntent, "Share PDF")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}
