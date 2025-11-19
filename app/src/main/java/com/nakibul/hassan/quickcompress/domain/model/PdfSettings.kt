package com.nakibul.hassan.quickcompress.domain.model

data class PdfSettings(
    val pageSize: PageSize = PageSize.A4,
    val margins: MarginOption = MarginOption.NONE
)

enum class PageSize(val displayName: String, val widthPx: Int, val heightPx: Int) {
    A4("A4", 595, 842),
    LETTER("Letter", 612, 792),
    FIT_TO_IMAGE("Fit to Image", -1, -1)
}

enum class MarginOption(val displayName: String, val marginPx: Int) {
    NONE("None", 0),
    SMALL("Small", 40)
}
