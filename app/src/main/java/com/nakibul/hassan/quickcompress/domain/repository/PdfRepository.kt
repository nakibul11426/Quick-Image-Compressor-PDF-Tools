package com.nakibul.hassan.quickcompress.domain.repository

import android.net.Uri
import com.nakibul.hassan.quickcompress.domain.model.PdfDocument
import com.nakibul.hassan.quickcompress.domain.model.PdfPage
import com.nakibul.hassan.quickcompress.domain.model.PdfSettings

interface PdfRepository {
    suspend fun createPdfFromImages(
        imageUris: List<Uri>,
        pdfSettings: PdfSettings,
        outputFileName: String
    ): Uri
    
    suspend fun mergePdfs(
        pdfUris: List<Uri>,
        outputFileName: String
    ): Uri
    
    suspend fun splitPdf(
        pdfUri: Uri,
        pageRanges: List<IntRange>,
        outputFilePrefix: String
    ): List<Uri>
    
    suspend fun getPdfDetails(uri: Uri): PdfDocument
    
    suspend fun getPdfPages(uri: Uri): List<PdfPage>
    
    fun sharePdf(uri: Uri)
}
