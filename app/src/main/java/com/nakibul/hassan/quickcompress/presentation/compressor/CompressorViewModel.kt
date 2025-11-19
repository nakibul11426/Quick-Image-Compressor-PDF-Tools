package com.nakibul.hassan.quickcompress.presentation.compressor

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nakibul.hassan.quickcompress.domain.model.CompressedImage
import com.nakibul.hassan.quickcompress.domain.model.CompressionSettings
import com.nakibul.hassan.quickcompress.domain.model.ImageItem
import com.nakibul.hassan.quickcompress.domain.usecase.CompressImagesUseCase
import com.nakibul.hassan.quickcompress.domain.usecase.GetImageDetailsUseCase
import com.nakibul.hassan.quickcompress.domain.usecase.SaveCompressedImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CompressorViewModel @Inject constructor(
    private val getImageDetailsUseCase: GetImageDetailsUseCase,
    private val compressImagesUseCase: CompressImagesUseCase,
    private val saveCompressedImageUseCase: SaveCompressedImageUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<CompressorUiState>(CompressorUiState.Initial)
    val uiState: StateFlow<CompressorUiState> = _uiState.asStateFlow()
    
    private val _selectedImages = MutableStateFlow<List<ImageItem>>(emptyList())
    val selectedImages: StateFlow<List<ImageItem>> = _selectedImages.asStateFlow()
    
    private val _compressionSettings = MutableStateFlow(CompressionSettings())
    val compressionSettings: StateFlow<CompressionSettings> = _compressionSettings.asStateFlow()
    
    private val _compressedImages = MutableStateFlow<List<CompressedImage>>(emptyList())
    val compressedImages: StateFlow<List<CompressedImage>> = _compressedImages.asStateFlow()
    
    fun onImagesSelected(uris: List<Uri>) {
        viewModelScope.launch {
            try {
                _uiState.value = CompressorUiState.Loading
                Timber.d("Loading ${uris.size} images")
                
                val images = withContext(Dispatchers.IO) {
                    getImageDetailsUseCase(uris)
                }
                
                Timber.d("Loaded ${images.size} images")
                _selectedImages.value = images
                _uiState.value = CompressorUiState.ImagesSelected(images)
            } catch (e: Exception) {
                Timber.e(e, "Error loading images")
                _uiState.value = CompressorUiState.Error(e.message ?: "Failed to load images")
            }
        }
    }
    
    fun removeImage(image: ImageItem) {
        val currentImages = _selectedImages.value.toMutableList()
        currentImages.remove(image)
        _selectedImages.value = currentImages
        
        if (currentImages.isEmpty()) {
            _uiState.value = CompressorUiState.Initial
        }
    }
    
    fun updateCompressionSettings(settings: CompressionSettings) {
        _compressionSettings.value = settings
    }
    
    fun compressImages() {
        viewModelScope.launch {
            try {
                _uiState.value = CompressorUiState.Compressing
                
                val imageUris = _selectedImages.value.map { it.uri }
                Timber.d("Starting compression for ${imageUris.size} images with settings: ${_compressionSettings.value}")
                
                val compressed = withContext(Dispatchers.IO) {
                    compressImagesUseCase(imageUris, _compressionSettings.value)
                }
                
                Timber.d("Compression complete: ${compressed.size} images")
                compressed.forEach { img ->
                    Timber.d("Image: Original=${img.originalSize}, Compressed=${img.compressedSize}, Saved=${img.sizeSaved}")
                }
                
                _compressedImages.value = compressed
                _uiState.value = CompressorUiState.CompressionComplete(compressed)
            } catch (e: Exception) {
                Timber.e(e, "Error compressing images")
                _uiState.value = CompressorUiState.Error(e.message ?: "Failed to compress images")
            }
        }
    }
    
    fun saveImages() {
        viewModelScope.launch {
            try {
                _uiState.value = CompressorUiState.Saving
                
                Timber.d("Saving ${_compressedImages.value.size} compressed images")
                
                val savedUris = withContext(Dispatchers.IO) {
                    _compressedImages.value.mapIndexed { index, compressed ->
                        val originalName = _selectedImages.value.getOrNull(index)?.name ?: "image_$index.jpg"
                        Timber.d("Saving image $index: $originalName")
                        saveCompressedImageUseCase(compressed.compressedUri, originalName)
                    }
                }
                
                Timber.d("All images saved successfully. Count: ${savedUris.size}")
                _uiState.value = CompressorUiState.SaveComplete(savedUris)
            } catch (e: Exception) {
                Timber.e(e, "Error saving images")
                _uiState.value = CompressorUiState.Error(e.message ?: "Failed to save images")
            }
        }
    }
    
    fun reset() {
        _selectedImages.value = emptyList()
        _compressedImages.value = emptyList()
        _compressionSettings.value = CompressionSettings()
        _uiState.value = CompressorUiState.Initial
    }
}

sealed class CompressorUiState {
    object Initial : CompressorUiState()
    object Loading : CompressorUiState()
    data class ImagesSelected(val images: List<ImageItem>) : CompressorUiState()
    object Compressing : CompressorUiState()
    data class CompressionComplete(val results: List<CompressedImage>) : CompressorUiState()
    object Saving : CompressorUiState()
    data class SaveComplete(val savedUris: List<Uri>) : CompressorUiState()
    data class Error(val message: String) : CompressorUiState()
}
