package com.nakibul.hassan.quickcompress.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import com.nakibul.hassan.quickcompress.domain.model.ResizeOption
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object ImageUtils {
    
    fun compressImage(
        context: Context,
        imageUri: Uri,
        quality: Int,
        resizeOption: ResizeOption
    ): Bitmap? {
        return try {
            Timber.d("compressImage: Processing $imageUri with quality=$quality, resizeOption=$resizeOption")
            
            // First pass: Get original dimensions
            var originalWidth = 0
            var originalHeight = 0
            
            context.contentResolver.openInputStream(imageUri)?.use { stream ->
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeStream(stream, null, options)
                originalWidth = options.outWidth
                originalHeight = options.outHeight
            }
            
            if (originalWidth <= 0 || originalHeight <= 0) {
                Timber.e("compressImage: Invalid dimensions")
                return null
            }
            
            Timber.d("compressImage: Original dimensions: ${originalWidth}x${originalHeight}")
            
            // Calculate target dimensions and sample size
            val (targetWidth, targetHeight) = if (resizeOption != ResizeOption.ORIGINAL) {
                Pair(resizeOption.width, resizeOption.height)
            } else {
                Pair(originalWidth, originalHeight)
            }
            
            val sampleSize = calculateInSampleSize(originalWidth, originalHeight, targetWidth, targetHeight)
            Timber.d("compressImage: Using inSampleSize=$sampleSize for target ${targetWidth}x${targetHeight}")
            
            // Second pass: Decode with sample size
            val bitmap = context.contentResolver.openInputStream(imageUri)?.use { stream ->
                val options = BitmapFactory.Options().apply {
                    inSampleSize = sampleSize
                    inPreferredConfig = Bitmap.Config.ARGB_8888
                }
                BitmapFactory.decodeStream(stream, null, options)
            }
            
            if (bitmap == null) {
                Timber.e("compressImage: Failed to decode bitmap")
                return null
            }
            
            Timber.d("compressImage: Decoded bitmap size: ${bitmap.width}x${bitmap.height}")
            
            // Further resize if needed to exact dimensions
            val resizedBitmap = if (resizeOption != ResizeOption.ORIGINAL &&
                (bitmap.width != targetWidth || bitmap.height != targetHeight)) {
                Timber.d("compressImage: Fine-tuning size to ${targetWidth}x${targetHeight}")
                val scaled = resizeBitmap(bitmap, resizeOption)
                if (scaled !== bitmap) {
                    bitmap.recycle()
                }
                scaled
            } else {
                bitmap
            }
            
            Timber.d("compressImage: Final bitmap size: ${resizedBitmap.width}x${resizedBitmap.height}")
            resizedBitmap
        } catch (e: Exception) {
            Timber.e(e, "Error compressing image")
            null
        }
    }
    
    private fun calculateInSampleSize(
        width: Int,
        height: Int,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        var inSampleSize = 1
        
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            
            while (halfHeight / inSampleSize >= reqHeight && 
                   halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        
        return inSampleSize
    }
    
    fun resizeBitmap(bitmap: Bitmap, resizeOption: ResizeOption): Bitmap {
        if (resizeOption == ResizeOption.ORIGINAL) return bitmap
        
        val targetWidth = resizeOption.width
        val targetHeight = resizeOption.height
        
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height
        
        val scale = minOf(
            targetWidth.toFloat() / originalWidth,
            targetHeight.toFloat() / originalHeight
        )
        
        if (scale >= 1.0f) return bitmap
        
        val newWidth = (originalWidth * scale).toInt()
        val newHeight = (originalHeight * scale).toInt()
        
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
    
    fun saveBitmapToFile(
        bitmap: Bitmap,
        file: File,
        quality: Int,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
    ): Boolean {
        return try {
            Timber.d("saveBitmapToFile: Saving ${bitmap.width}x${bitmap.height} bitmap to ${file.absolutePath} with quality=$quality")
            
            FileOutputStream(file).use { out ->
                val success = bitmap.compress(format, quality, out)
                if (!success) {
                    Timber.e("saveBitmapToFile: bitmap.compress returned false")
                    return false
                }
            }
            
            val fileSize = file.length()
            Timber.d("saveBitmapToFile: File saved successfully, size: $fileSize bytes")
            
            if (fileSize == 0L) {
                Timber.e("saveBitmapToFile: File size is 0!")
                return false
            }
            
            true
        } catch (e: Exception) {
            Timber.e(e, "Error saving bitmap")
            false
        }
    }
    
    fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            Timber.d("getBitmapFromUri: Loading $uri")
            
            // First pass: Get dimensions without loading full image
            var width = 0
            var height = 0
            
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeStream(stream, null, options)
                width = options.outWidth
                height = options.outHeight
            }
            
            if (width <= 0 || height <= 0) {
                Timber.e("getBitmapFromUri: Invalid dimensions for $uri")
                return null
            }
            
            Timber.d("getBitmapFromUri: Image dimensions: ${width}x${height}")
            
            // Calculate sample size for memory efficiency
            // Use max 2048 as reasonable size for PDF creation
            val maxDimension = 2048
            val sampleSize = calculateInSampleSize(width, height, maxDimension, maxDimension)
            
            Timber.d("getBitmapFromUri: Using inSampleSize=$sampleSize")
            
            // Second pass: Decode with sample size
            val bitmap = context.contentResolver.openInputStream(uri)?.use { stream ->
                val options = BitmapFactory.Options().apply {
                    inSampleSize = sampleSize
                    inPreferredConfig = Bitmap.Config.ARGB_8888
                }
                BitmapFactory.decodeStream(stream, null, options)
            }
            
            if (bitmap == null) {
                Timber.e("getBitmapFromUri: Failed to decode bitmap from $uri")
                return null
            }
            
            Timber.d("getBitmapFromUri: Successfully loaded bitmap ${bitmap.width}x${bitmap.height}")
            bitmap
        } catch (e: Exception) {
            Timber.e(e, "Error loading bitmap from URI: $uri")
            null
        }
    }
    
    fun correctImageOrientation(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val exif = ExifInterface(inputStream)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED
                )
                
                rotateBitmap(bitmap, orientation)
            } ?: bitmap
        } catch (e: Exception) {
            Timber.e(e, "Error correcting image orientation")
            bitmap
        }
    }
    
    private fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
            else -> return bitmap
        }
        
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}
