package com.nakibul.hassan.quickcompress

object AppConfig {
    const val APP_NAME = "Quick Image Compressor & PDF Tools"
    const val VERSION_NAME = "1.0"
    const val VERSION_CODE = 1
    
    // File storage paths
    const val COMPRESSED_IMAGES_FOLDER = "Compressed"
    const val PDFS_FOLDER = "PDFs"
    
    // Compression settings
    const val DEFAULT_QUALITY = 80
    const val MIN_QUALITY = 10
    const val MAX_QUALITY = 100
    
    // PDF settings
    const val A4_WIDTH_PX = 595
    const val A4_HEIGHT_PX = 842
    const val LETTER_WIDTH_PX = 612
    const val LETTER_HEIGHT_PX = 792
    
    // File naming
    const val IMAGE_EXTENSION = ".jpg"
    const val PDF_EXTENSION = ".pdf"
}
