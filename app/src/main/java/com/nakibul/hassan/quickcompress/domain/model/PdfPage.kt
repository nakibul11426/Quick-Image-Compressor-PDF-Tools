package com.nakibul.hassan.quickcompress.domain.model

import android.graphics.Bitmap

data class PdfPage(
    val pageNumber: Int,
    val thumbnail: Bitmap?,
    val isSelected: Boolean = false
)
