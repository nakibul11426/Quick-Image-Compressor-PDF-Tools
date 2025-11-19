package com.nakibul.hassan.quickcompress.domain.usecase

import android.net.Uri
import com.nakibul.hassan.quickcompress.domain.model.PdfSettings
import com.nakibul.hassan.quickcompress.domain.repository.PdfRepository
import javax.inject.Inject

class CreatePdfFromImagesUseCase @Inject constructor(
    private val pdfRepository: PdfRepository
) {
    suspend operator fun invoke(
        imageUris: List<Uri>,
        pdfSettings: PdfSettings,
        outputFileName: String
    ): Uri {
        return pdfRepository.createPdfFromImages(imageUris, pdfSettings, outputFileName)
    }
}
