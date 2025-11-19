package com.nakibul.hassan.quickcompress.domain.repository

import android.net.Uri
import com.nakibul.hassan.quickcompress.domain.model.CompressedImage
import com.nakibul.hassan.quickcompress.domain.model.CompressionSettings
import com.nakibul.hassan.quickcompress.domain.model.ImageItem

interface ImageRepository {
    suspend fun getImageDetails(uri: Uri): ImageItem
    suspend fun compressImage(
        imageUri: Uri,
        settings: CompressionSettings
    ): CompressedImage
    suspend fun saveCompressedImage(compressedUri: Uri, displayName: String): Uri
    fun shareImages(uris: List<Uri>)
}
