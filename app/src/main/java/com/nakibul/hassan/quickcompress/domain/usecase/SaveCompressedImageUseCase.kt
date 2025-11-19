package com.nakibul.hassan.quickcompress.domain.usecase

import android.net.Uri
import com.nakibul.hassan.quickcompress.domain.repository.ImageRepository
import javax.inject.Inject

class SaveCompressedImageUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke(compressedUri: Uri, displayName: String): Uri {
        return imageRepository.saveCompressedImage(compressedUri, displayName)
    }
}
