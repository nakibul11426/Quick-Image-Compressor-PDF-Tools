package com.nakibul.hassan.quickcompress.domain.usecase

import android.net.Uri
import com.nakibul.hassan.quickcompress.domain.repository.PdfRepository
import javax.inject.Inject

class MergePdfUseCase @Inject constructor(
    private val pdfRepository: PdfRepository
) {
    suspend operator fun invoke(
        pdfUris: List<Uri>,
        outputFileName: String
    ): Uri {
        return pdfRepository.mergePdfs(pdfUris, outputFileName)
    }
}
