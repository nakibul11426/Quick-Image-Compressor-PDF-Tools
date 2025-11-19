package com.nakibul.hassan.quickcompress.domain.model

import android.net.Uri

data class ImageItem(
    val uri: Uri,
    val name: String,
    val size: Long,
    val mimeType: String?
)
