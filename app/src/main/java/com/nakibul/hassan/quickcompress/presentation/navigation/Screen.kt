package com.nakibul.hassan.quickcompress.presentation.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    
    object CompressorPicker : Screen("compressor/picker")
    object CompressorOptions : Screen("compressor/options")
    object CompressorResult : Screen("compressor/result")
    
    object ImageToPdfPicker : Screen("imagetopdf/picker")
    object ImageToPdfPreview : Screen("imagetopdf/preview")
    object ImageToPdfResult : Screen("imagetopdf/result")
    
    object PdfMerge : Screen("pdfmerge")
    object PdfMergeResult : Screen("pdfmerge/result")
    
    object PdfSplit : Screen("pdfsplit")
    object PdfSplitResult : Screen("pdfsplit/result")
}
