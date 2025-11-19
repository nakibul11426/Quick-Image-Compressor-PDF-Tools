package com.nakibul.hassan.quickcompress.presentation.pdfsplit

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nakibul.hassan.quickcompress.domain.model.PdfDocument
import com.nakibul.hassan.quickcompress.domain.model.PdfPage
import com.nakibul.hassan.quickcompress.domain.usecase.GetPdfDetailsUseCase
import com.nakibul.hassan.quickcompress.domain.usecase.GetPdfPagesUseCase
import com.nakibul.hassan.quickcompress.domain.usecase.SplitPdfUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PdfSplitViewModel @Inject constructor(
    private val getPdfDetailsUseCase: GetPdfDetailsUseCase,
    private val getPdfPagesUseCase: GetPdfPagesUseCase,
    private val splitPdfUseCase: SplitPdfUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<PdfSplitUiState>(PdfSplitUiState.Initial)
    val uiState: StateFlow<PdfSplitUiState> = _uiState.asStateFlow()
    
    private val _selectedPdf = MutableStateFlow<PdfDocument?>(null)
    val selectedPdf: StateFlow<PdfDocument?> = _selectedPdf.asStateFlow()
    
    private val _pdfPages = MutableStateFlow<List<PdfPage>>(emptyList())
    val pdfPages: StateFlow<List<PdfPage>> = _pdfPages.asStateFlow()
    
    fun onPdfSelected(uri: Uri) {
        viewModelScope.launch {
            try {
                _uiState.value = PdfSplitUiState.Loading
                val pdfs = getPdfDetailsUseCase(listOf(uri))
                val pdf = pdfs.firstOrNull() ?: throw IllegalStateException("Failed to load PDF")
                
                _selectedPdf.value = pdf
                
                val pages = getPdfPagesUseCase(uri)
                _pdfPages.value = pages
                
                _uiState.value = PdfSplitUiState.PdfLoaded(pdf, pages)
            } catch (e: Exception) {
                Timber.e(e, "Error loading PDF")
                _uiState.value = PdfSplitUiState.Error(e.message ?: "Failed to load PDF")
            }
        }
    }
    
    fun togglePageSelection(pageNumber: Int) {
        val updatedPages = _pdfPages.value.map { page ->
            if (page.pageNumber == pageNumber) {
                page.copy(isSelected = !page.isSelected)
            } else {
                page
            }
        }
        _pdfPages.value = updatedPages
    }
    
    fun splitPdf(outputFilePrefix: String) {
        viewModelScope.launch {
            try {
                _uiState.value = PdfSplitUiState.Splitting
                
                val selectedPages = _pdfPages.value.filter { it.isSelected }
                if (selectedPages.isEmpty()) {
                    _uiState.value = PdfSplitUiState.Error("No pages selected")
                    return@launch
                }
                
                val pageRanges = convertToRanges(selectedPages.map { it.pageNumber - 1 })
                
                val pdfUri = _selectedPdf.value?.uri ?: throw IllegalStateException("No PDF selected")
                val splitUris = splitPdfUseCase(pdfUri, pageRanges, outputFilePrefix)
                
                _uiState.value = PdfSplitUiState.SplitComplete(splitUris)
            } catch (e: Exception) {
                Timber.e(e, "Error splitting PDF")
                _uiState.value = PdfSplitUiState.Error(e.message ?: "Failed to split PDF")
            }
        }
    }
    
    private fun convertToRanges(pageNumbers: List<Int>): List<IntRange> {
        if (pageNumbers.isEmpty()) return emptyList()
        
        val sorted = pageNumbers.sorted()
        val ranges = mutableListOf<IntRange>()
        var rangeStart = sorted.first()
        var rangeEnd = sorted.first()
        
        for (i in 1 until sorted.size) {
            if (sorted[i] == rangeEnd + 1) {
                rangeEnd = sorted[i]
            } else {
                ranges.add(rangeStart..rangeEnd)
                rangeStart = sorted[i]
                rangeEnd = sorted[i]
            }
        }
        ranges.add(rangeStart..rangeEnd)
        
        return ranges
    }
    
    fun reset() {
        _selectedPdf.value = null
        _pdfPages.value = emptyList()
        _uiState.value = PdfSplitUiState.Initial
    }
}

sealed class PdfSplitUiState {
    object Initial : PdfSplitUiState()
    object Loading : PdfSplitUiState()
    data class PdfLoaded(val pdf: PdfDocument, val pages: List<PdfPage>) : PdfSplitUiState()
    object Splitting : PdfSplitUiState()
    data class SplitComplete(val splitPdfUris: List<Uri>) : PdfSplitUiState()
    data class Error(val message: String) : PdfSplitUiState()
}
