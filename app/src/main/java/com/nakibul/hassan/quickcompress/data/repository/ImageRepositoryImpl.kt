package com.nakibul.hassan.quickcompress.data.repository

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import com.nakibul.hassan.quickcompress.domain.model.CompressedImage
import com.nakibul.hassan.quickcompress.domain.model.CompressionSettings
import com.nakibul.hassan.quickcompress.domain.model.ImageItem
import com.nakibul.hassan.quickcompress.domain.repository.ImageRepository
import com.nakibul.hassan.quickcompress.utils.FileUtils
import com.nakibul.hassan.quickcompress.utils.ImageUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ImageRepository {
    
    override suspend fun getImageDetails(uri: Uri): ImageItem {
        val name = FileUtils.getFileName(context, uri)
        val size = FileUtils.getFileSize(context, uri)
        val mimeType = FileUtils.getMimeType(context, uri)
        
        return ImageItem(
            uri = uri,
            name = name,
            size = size,
            mimeType = mimeType
        )
    }
    
    override suspend fun compressImage(
        imageUri: Uri,
        settings: CompressionSettings
    ): CompressedImage {
        val originalSize = FileUtils.getFileSize(context, imageUri)
        
        Timber.d("Starting compression for image: $imageUri, original size: $originalSize bytes, quality: ${settings.quality}")
        
        val compressedBitmap = ImageUtils.compressImage(
            context, 
            imageUri, 
            settings.quality, 
            settings.resizeOption
        ) ?: throw IllegalStateException("Failed to compress image")
        
        Timber.d("Bitmap loaded: ${compressedBitmap.width}x${compressedBitmap.height}")
        
        // Create temp file for compressed image
        val tempFile = FileUtils.createTempFile(context, "compressed_", ".jpg")
        
        // Save bitmap with compression quality
        val saveSuccess = ImageUtils.saveBitmapToFile(
            compressedBitmap, 
            tempFile, 
            settings.quality,
            Bitmap.CompressFormat.JPEG
        )
        
        if (!saveSuccess) {
            throw IllegalStateException("Failed to save compressed image")
        }
        
        // Recycle bitmap to free memory
        compressedBitmap.recycle()
        
        val compressedSize = tempFile.length()
        Timber.d("Compressed size: $compressedSize bytes (quality: ${settings.quality}%)")
        
        val compressionRatio = if (originalSize > 0) {
            compressedSize.toFloat() / originalSize.toFloat()
        } else 1f
        
        val compressedUri = Uri.fromFile(tempFile)
        
        Timber.d("Compression complete - Original: $originalSize, Compressed: $compressedSize, Ratio: $compressionRatio")
        
        return CompressedImage(
            originalUri = imageUri,
            compressedUri = compressedUri,
            originalSize = originalSize,
            compressedSize = compressedSize,
            compressionRatio = compressionRatio
        )
    }
    
    override suspend fun saveCompressedImage(compressedUri: Uri, displayName: String): Uri {
        Timber.d("Saving compressed image: $displayName")
        
        val sourceFile = File(compressedUri.path!!)
        if (!sourceFile.exists()) {
            Timber.e("Source file does not exist: ${sourceFile.absolutePath}")
            throw IllegalStateException("Compressed file not found: ${sourceFile.absolutePath}")
        }
        
        // Get output directory
        val outputDir = FileUtils.getCompressedImagesDir(context)
        
        // Validate directory exists and is writable
        if (!outputDir.exists()) {
            Timber.e("Output directory does not exist: ${outputDir.absolutePath}")
            throw IllegalStateException("Failed to create output directory: ${outputDir.absolutePath}")
        }
        
        if (!outputDir.canWrite()) {
            Timber.e("Output directory is not writable: ${outputDir.absolutePath}")
            throw IllegalStateException("Cannot write to directory: ${outputDir.absolutePath}")
        }
        
        val fileName = FileUtils.generateUniqueFileName(displayName)
        val outputFile = File(outputDir, fileName)
        
        Timber.d("Saving to: ${outputFile.absolutePath}")
        
        try {
            // Copy file to destination
            sourceFile.copyTo(outputFile, overwrite = true)
            
            if (!outputFile.exists() || outputFile.length() == 0L) {
                throw IllegalStateException("File copy failed or resulted in empty file")
            }
            
            Timber.d("Image saved successfully. Size: ${outputFile.length()} bytes")
            
            // Clean up temp file
            sourceFile.delete()
            
            return Uri.fromFile(outputFile)
        } catch (e: Exception) {
            Timber.e(e, "Failed to save image: ${e.message}")
            throw IllegalStateException("Failed to save compressed image: ${e.message}", e)
        }
    }
    
    override fun shareImages(uris: List<Uri>) {
        val shareIntent = Intent().apply {
            if (uris.size == 1) {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, uris.first())
                type = "image/*"
            } else {
                action = Intent.ACTION_SEND_MULTIPLE
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
                type = "image/*"
            }
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        val chooser = Intent.createChooser(shareIntent, "Share Images")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}
