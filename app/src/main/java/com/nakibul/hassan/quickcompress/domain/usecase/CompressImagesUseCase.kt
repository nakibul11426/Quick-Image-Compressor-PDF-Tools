package com.nakibul.hassan.quickcompress.domain.usecase

import android.net.Uri
import com.nakibul.hassan.quickcompress.domain.model.CompressedImage
import com.nakibul.hassan.quickcompress.domain.model.CompressionSettings
import com.nakibul.hassan.quickcompress.domain.repository.ImageRepository
import javax.inject.Inject

class CompressImagesUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke(
        imageUris: List<Uri>,
        settings: CompressionSettings
    ): List<CompressedImage> {
        return imageUris.map { uri ->
            imageRepository.compressImage(uri, settings)
        }
    }
}
