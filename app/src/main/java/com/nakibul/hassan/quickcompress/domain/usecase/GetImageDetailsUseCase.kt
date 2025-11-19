package com.nakibul.hassan.quickcompress.domain.usecase

import android.net.Uri
import com.nakibul.hassan.quickcompress.domain.model.ImageItem
import com.nakibul.hassan.quickcompress.domain.repository.ImageRepository
import javax.inject.Inject

class GetImageDetailsUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke(uris: List<Uri>): List<ImageItem> {
        return uris.map { uri ->
            imageRepository.getImageDetails(uri)
        }
    }
}
