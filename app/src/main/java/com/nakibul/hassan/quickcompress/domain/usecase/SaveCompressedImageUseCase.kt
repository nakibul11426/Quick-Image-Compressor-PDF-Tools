package com.nakibul.hassan.quickcompress.domain.usecase

import android.net.Uri
import com.nakibul.hassan.quickcompress.domain.repository.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SaveCompressedImageUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke(compressedUri: Uri, displayName: String): Uri = withContext(Dispatchers.IO) {
        return@withContext imageRepository.saveCompressedImage(compressedUri, displayName)
    }
}
