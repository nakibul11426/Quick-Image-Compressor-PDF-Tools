package com.nakibul.hassan.quickcompress.domain.model

data class CompressionSettings(
    val quality: Int = 80,
    val resizeOption: ResizeOption = ResizeOption.ORIGINAL
)

enum class ResizeOption(val displayName: String, val width: Int, val height: Int) {
    ORIGINAL("Original", -1, -1),
    HD_1080("1920x1080", 1920, 1080),
    HD_720("1280x720", 1280, 720),
    SD("720x480", 720, 480)
}
