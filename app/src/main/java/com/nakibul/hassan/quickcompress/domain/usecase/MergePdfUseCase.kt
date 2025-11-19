package com.nakibul.hassan.quickcompress.domain.usecase

import android.net.Uri
import com.nakibul.hassan.quickcompress.domain.repository.PdfRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MergePdfUseCase @Inject constructor(
    private val pdfRepository: PdfRepository
) {
    suspend operator fun invoke(
        pdfUris: List<Uri>,
        outputFileName: String
    ): Uri = withContext(Dispatchers.IO) {
        return@withContext pdfRepository.mergePdfs(pdfUris, outputFileName)
    }
}
