package com.nakibul.hassan.quickcompress.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import kotlin.math.log10
import kotlin.math.pow

object FileUtils {
    
    fun getFileName(context: Context, uri: Uri): String {
        var fileName = "unknown"
        
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1 && cursor.moveToFirst()) {
                fileName = cursor.getString(nameIndex)
            }
        }
        
        return fileName
    }
    
    fun getFileSize(context: Context, uri: Uri): Long {
        var fileSize = 0L
        
        try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (sizeIndex != -1 && cursor.moveToFirst()) {
                    fileSize = cursor.getLong(sizeIndex)
                    Timber.d("getFileSize for $uri: $fileSize bytes")
                } else {
                    Timber.w("getFileSize: SIZE column not found or cursor empty for $uri")
                }
            } ?: Timber.w("getFileSize: Query returned null for $uri")
        } catch (e: Exception) {
            Timber.e(e, "Error getting file size for $uri")
        }
        
        return fileSize
    }
    
    fun getMimeType(context: Context, uri: Uri): String? {
        return context.contentResolver.getType(uri)
    }
    
    fun formatFileSize(size: Long): String {
        if (size <= 0) return "0 B"
        
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
        
        return String.format(
            "%.2f %s",
            size / 1024.0.pow(digitGroups.toDouble()),
            units[digitGroups]
        )
    }
    
    fun copyUriToFile(context: Context, uri: Uri, destinationFile: File): Boolean {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(destinationFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            true
        } catch (e: Exception) {
            Timber.e(e, "Error copying URI to file")
            false
        }
    }
    
    fun createTempFile(context: Context, prefix: String, suffix: String): File {
        val cacheDir = context.cacheDir
        return File.createTempFile(prefix, suffix, cacheDir)
    }
    
    fun getFileExtension(fileName: String): String {
        return fileName.substringAfterLast('.', "")
    }
    
    fun getAppStorageDir(context: Context): File {
        // Use app-specific directory for internal files like PDFs
        val dir = File(context.getExternalFilesDir(null), "QuickCompress")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
    
    fun getCompressedImagesDir(context: Context): File {
        // This is now only used for temporary storage during compression
        // Final images are saved via MediaStore
        val dir = File(context.cacheDir, "compressed_temp")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
    
    fun getPdfsDir(context: Context): File {
        val dir = File(getAppStorageDir(context), "PDFs")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
    
    fun generateUniqueFileName(baseFileName: String): String {
        val timestamp = System.currentTimeMillis()
        val nameWithoutExtension = baseFileName.substringBeforeLast('.')
        val extension = baseFileName.substringAfterLast('.', "")
        
        return if (extension.isNotEmpty()) {
            "${nameWithoutExtension}_$timestamp.$extension"
        } else {
            "${baseFileName}_$timestamp"
        }
    }
}
