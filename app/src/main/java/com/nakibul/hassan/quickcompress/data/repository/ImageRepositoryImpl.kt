package com.nakibul.hassan.quickcompress.data.repository

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.nakibul.hassan.quickcompress.domain.model.CompressedImage
import com.nakibul.hassan.quickcompress.domain.model.CompressionSettings
import com.nakibul.hassan.quickcompress.domain.model.ImageItem
import com.nakibul.hassan.quickcompress.domain.repository.ImageRepository
import com.nakibul.hassan.quickcompress.utils.FileUtils
import com.nakibul.hassan.quickcompress.utils.ImageUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
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
            throw IllegalStateException("Compressed file not found")
        }
        
        // Use MediaStore to save to Pictures/QuickCompress folder
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, FileUtils.generateUniqueFileName(displayName))
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // For Android 10+, use relative path
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/QuickCompress")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }
        
        val contentResolver = context.contentResolver
        val imageUri = contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ) ?: throw IllegalStateException("Failed to create MediaStore entry")
        
        Timber.d("MediaStore URI created: $imageUri")
        
        try {
            // Copy file to MediaStore URI
            contentResolver.openOutputStream(imageUri)?.use { outputStream ->
                FileInputStream(sourceFile).use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            
            // Mark as complete (Android 10+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                contentResolver.update(imageUri, contentValues, null, null)
            }
            
            Timber.d("Image saved successfully to MediaStore")
            
            // Clean up temp file
            sourceFile.delete()
            
            return imageUri
        } catch (e: Exception) {
            // If save fails, delete the MediaStore entry
            contentResolver.delete(imageUri, null, null)
            Timber.e(e, "Failed to save image to MediaStore")
            throw e
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
