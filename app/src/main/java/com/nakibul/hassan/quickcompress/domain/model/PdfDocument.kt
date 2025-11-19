package com.nakibul.hassan.quickcompress.domain.model

import android.net.Uri

data class PdfDocument(
    val uri: Uri,
    val name: String,
    val size: Long,
    val pageCount: Int
)
