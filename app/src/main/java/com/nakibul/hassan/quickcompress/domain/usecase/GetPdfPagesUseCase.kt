package com.nakibul.hassan.quickcompress.domain.usecase

import android.net.Uri
import com.nakibul.hassan.quickcompress.domain.model.PdfPage
import com.nakibul.hassan.quickcompress.domain.repository.PdfRepository
import javax.inject.Inject

class GetPdfPagesUseCase @Inject constructor(
    private val pdfRepository: PdfRepository
) {
    suspend operator fun invoke(pdfUri: Uri): List<PdfPage> {
        return pdfRepository.getPdfPages(pdfUri)
    }
}
