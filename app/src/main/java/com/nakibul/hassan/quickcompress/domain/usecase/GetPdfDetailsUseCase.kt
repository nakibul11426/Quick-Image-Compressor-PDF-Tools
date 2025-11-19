package com.nakibul.hassan.quickcompress.domain.usecase

import android.net.Uri
import com.nakibul.hassan.quickcompress.domain.model.PdfDocument
import com.nakibul.hassan.quickcompress.domain.repository.PdfRepository
import javax.inject.Inject

class GetPdfDetailsUseCase @Inject constructor(
    private val pdfRepository: PdfRepository
) {
    suspend operator fun invoke(uris: List<Uri>): List<PdfDocument> {
        return uris.map { uri ->
            pdfRepository.getPdfDetails(uri)
        }
    }
}
