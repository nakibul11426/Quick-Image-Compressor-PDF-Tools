package com.nakibul.hassan.quickcompress.domain.usecase

import android.net.Uri
import com.nakibul.hassan.quickcompress.domain.repository.PdfRepository
import javax.inject.Inject

class SplitPdfUseCase @Inject constructor(
    private val pdfRepository: PdfRepository
) {
    suspend operator fun invoke(
        pdfUri: Uri,
        pageRanges: List<IntRange>,
        outputFilePrefix: String
    ): List<Uri> {
        return pdfRepository.splitPdf(pdfUri, pageRanges, outputFilePrefix)
    }
}
