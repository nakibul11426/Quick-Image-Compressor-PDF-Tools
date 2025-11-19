package com.nakibul.hassan.quickcompress.presentation.pdfmerge

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nakibul.hassan.quickcompress.domain.model.PdfDocument
import com.nakibul.hassan.quickcompress.domain.usecase.GetPdfDetailsUseCase
import com.nakibul.hassan.quickcompress.domain.usecase.MergePdfUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PdfMergeViewModel @Inject constructor(
    private val getPdfDetailsUseCase: GetPdfDetailsUseCase,
    private val mergePdfUseCase: MergePdfUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<PdfMergeUiState>(PdfMergeUiState.Initial)
    val uiState: StateFlow<PdfMergeUiState> = _uiState.asStateFlow()
    
    private val _selectedPdfs = MutableStateFlow<List<PdfDocument>>(emptyList())
    val selectedPdfs: StateFlow<List<PdfDocument>> = _selectedPdfs.asStateFlow()
    
    fun onPdfsSelected(uris: List<Uri>) {
        viewModelScope.launch {
            try {
                _uiState.value = PdfMergeUiState.Loading
                val pdfs = getPdfDetailsUseCase(uris)
                _selectedPdfs.value = pdfs
                _uiState.value = PdfMergeUiState.PdfsSelected(pdfs)
            } catch (e: Exception) {
                Timber.e(e, "Error loading PDFs")
                _uiState.value = PdfMergeUiState.Error(e.message ?: "Failed to load PDFs")
            }
        }
    }
    
    fun reorderPdfs(fromIndex: Int, toIndex: Int) {
        val currentList = _selectedPdfs.value.toMutableList()
        val item = currentList.removeAt(fromIndex)
        currentList.add(toIndex, item)
        _selectedPdfs.value = currentList
    }
    
    fun removePdf(pdf: PdfDocument) {
        val currentPdfs = _selectedPdfs.value.toMutableList()
        currentPdfs.remove(pdf)
        _selectedPdfs.value = currentPdfs
        
        if (currentPdfs.isEmpty()) {
            _uiState.value = PdfMergeUiState.Initial
        }
    }
    
    fun mergePdfs(outputFileName: String) {
        viewModelScope.launch {
            try {
                _uiState.value = PdfMergeUiState.Merging
                
                val pdfUris = _selectedPdfs.value.map { it.uri }
                val mergedPdfUri = mergePdfUseCase(pdfUris, outputFileName)
                
                _uiState.value = PdfMergeUiState.MergeComplete(mergedPdfUri)
            } catch (e: Exception) {
                Timber.e(e, "Error merging PDFs")
                _uiState.value = PdfMergeUiState.Error(e.message ?: "Failed to merge PDFs")
            }
        }
    }
    
    fun reset() {
        _selectedPdfs.value = emptyList()
        _uiState.value = PdfMergeUiState.Initial
    }
}

sealed class PdfMergeUiState {
    object Initial : PdfMergeUiState()
    object Loading : PdfMergeUiState()
    data class PdfsSelected(val pdfs: List<PdfDocument>) : PdfMergeUiState()
    object Merging : PdfMergeUiState()
    data class MergeComplete(val mergedPdfUri: Uri) : PdfMergeUiState()
    data class Error(val message: String) : PdfMergeUiState()
}
