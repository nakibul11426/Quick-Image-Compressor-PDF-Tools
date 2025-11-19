package com.nakibul.hassan.quickcompress.presentation.imagetopdf

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nakibul.hassan.quickcompress.domain.model.ImageItem
import com.nakibul.hassan.quickcompress.domain.model.PdfSettings
import com.nakibul.hassan.quickcompress.domain.usecase.CreatePdfFromImagesUseCase
import com.nakibul.hassan.quickcompress.domain.usecase.GetImageDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ImageToPdfViewModel @Inject constructor(
    private val getImageDetailsUseCase: GetImageDetailsUseCase,
    private val createPdfFromImagesUseCase: CreatePdfFromImagesUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ImageToPdfUiState>(ImageToPdfUiState.Initial)
    val uiState: StateFlow<ImageToPdfUiState> = _uiState.asStateFlow()
    
    private val _selectedImages = MutableStateFlow<List<ImageItem>>(emptyList())
    val selectedImages: StateFlow<List<ImageItem>> = _selectedImages.asStateFlow()
    
    private val _pdfSettings = MutableStateFlow(PdfSettings())
    val pdfSettings: StateFlow<PdfSettings> = _pdfSettings.asStateFlow()
    
    fun onImagesSelected(uris: List<Uri>) {
        viewModelScope.launch {
            try {
                _uiState.value = ImageToPdfUiState.Loading
                val images = getImageDetailsUseCase(uris)
                _selectedImages.value = images
                _uiState.value = ImageToPdfUiState.ImagesSelected(images)
            } catch (e: Exception) {
                Timber.e(e, "Error loading images")
                _uiState.value = ImageToPdfUiState.Error(e.message ?: "Failed to load images")
            }
        }
    }
    
    fun addMoreImages(uris: List<Uri>) {
        viewModelScope.launch {
            try {
                _uiState.value = ImageToPdfUiState.Loading
                val newImages = getImageDetailsUseCase(uris)
                val currentImages = _selectedImages.value.toMutableList()
                currentImages.addAll(newImages)
                _selectedImages.value = currentImages
                _uiState.value = ImageToPdfUiState.ImagesSelected(currentImages)
            } catch (e: Exception) {
                Timber.e(e, "Error loading images")
                _uiState.value = ImageToPdfUiState.Error(e.message ?: "Failed to load images")
            }
        }
    }
    
    fun reorderImages(fromIndex: Int, toIndex: Int) {
        val currentList = _selectedImages.value.toMutableList()
        val item = currentList.removeAt(fromIndex)
        currentList.add(toIndex, item)
        _selectedImages.value = currentList
    }
    
    fun removeImage(image: ImageItem) {
        val currentImages = _selectedImages.value.toMutableList()
        currentImages.remove(image)
        _selectedImages.value = currentImages
        
        if (currentImages.isEmpty()) {
            _uiState.value = ImageToPdfUiState.Initial
        }
    }
    
    fun updatePdfSettings(settings: PdfSettings) {
        _pdfSettings.value = settings
    }
    
    fun createPdf(fileName: String) {
        viewModelScope.launch {
            try {
                _uiState.value = ImageToPdfUiState.Creating
                
                val startTime = System.currentTimeMillis()
                
                val imageUris = _selectedImages.value.map { it.uri }
                Timber.d("createPdf: Creating PDF from ${imageUris.size} images")
                Timber.d("createPdf: Output filename: $fileName")
                
                val pdfUri = createPdfFromImagesUseCase(
                    imageUris,
                    _pdfSettings.value,
                    fileName
                )
                
                Timber.d("createPdf: PDF created at: $pdfUri")
                
                // Ensure minimum display time of 1 second for loading indicator
                val elapsedTime = System.currentTimeMillis() - startTime
                val remainingTime = 1000 - elapsedTime
                if (remainingTime > 0) {
                    kotlinx.coroutines.delay(remainingTime)
                }
                
                _uiState.value = ImageToPdfUiState.PdfCreated(pdfUri)
            } catch (e: Exception) {
                Timber.e(e, "Error creating PDF")
                _uiState.value = ImageToPdfUiState.Error(e.message ?: "Failed to create PDF")
            }
        }
    }
    
    fun reset() {
        _selectedImages.value = emptyList()
        _pdfSettings.value = PdfSettings()
        _uiState.value = ImageToPdfUiState.Initial
    }
}

sealed class ImageToPdfUiState {
    object Initial : ImageToPdfUiState()
    object Loading : ImageToPdfUiState()
    data class ImagesSelected(val images: List<ImageItem>) : ImageToPdfUiState()
    object Creating : ImageToPdfUiState()
    data class PdfCreated(val pdfUri: Uri) : ImageToPdfUiState()
    data class Error(val message: String) : ImageToPdfUiState()
}
