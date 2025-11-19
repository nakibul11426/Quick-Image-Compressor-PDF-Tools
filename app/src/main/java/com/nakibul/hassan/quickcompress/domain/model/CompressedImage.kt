package com.nakibul.hassan.quickcompress.domain.model

import android.net.Uri

data class CompressedImage(
    val originalUri: Uri,
    val compressedUri: Uri,
    val originalSize: Long,
    val compressedSize: Long,
    val compressionRatio: Float
) {
    val sizeSaved: Long
        get() = originalSize - compressedSize
    
    val percentageSaved: Float
        get() = if (originalSize > 0) {
            ((originalSize - compressedSize).toFloat() / originalSize.toFloat()) * 100
        } else 0f
}
