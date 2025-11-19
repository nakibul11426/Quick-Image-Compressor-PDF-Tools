package com.nakibul.hassan.quickcompress.presentation.compressor

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nakibul.hassan.quickcompress.presentation.components.ErrorDialog
import com.nakibul.hassan.quickcompress.presentation.components.FileInfoRow
import com.nakibul.hassan.quickcompress.presentation.components.ImageThumbnailGrid
import com.nakibul.hassan.quickcompress.presentation.components.LoadingDialog
import com.nakibul.hassan.quickcompress.presentation.components.PrimaryButton
import com.nakibul.hassan.quickcompress.presentation.components.RoundedCard
import com.nakibul.hassan.quickcompress.presentation.components.SecondaryButton
import com.nakibul.hassan.quickcompress.presentation.components.TopBar

@Composable
fun CompressorPickerScreen(
    viewModel: CompressorViewModel = hiltViewModel(),
    onNavigateToOptions: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val selectedImages by viewModel.selectedImages.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.onImagesSelected(listOf(uri))
        }
    }
    
    LaunchedEffect(uiState) {
        if (uiState is CompressorUiState.ImagesSelected) {
            // Images are ready, stay on this screen
        }
    }
    
    Scaffold(
        topBar = {
            TopBar(
                title = "Select Images",
                onNavigationClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is CompressorUiState.Loading -> {
                    LoadingDialog(message = "Loading image...")
                }
                is CompressorUiState.Error -> {
                    val error = (uiState as CompressorUiState.Error).message
                    ErrorDialog(
                        message = error,
                        onDismiss = { viewModel.reset() }
                    )
                }
                else -> {
                    // Show content
                }
            }
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Choose an image to compress",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                if (selectedImages.isEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "No image selected",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    RoundedCard {
                        Text(
                            text = "1 image selected",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    ImageThumbnailGrid(
                        images = selectedImages,
                        onRemoveImage = { image ->
                            viewModel.removeImage(image)
                        },
                        modifier = Modifier.heightIn(max = 400.dp)
                    )
                    
                    selectedImages.forEach { image ->
                        FileInfoRow(
                            fileName = image.name,
                            fileSize = image.size
                        )
                        HorizontalDivider()
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))

                SecondaryButton(
                    text = if (selectedImages.isEmpty()) "Select Image" else "Change Image",
                    onClick = { imagePickerLauncher.launch("image/*") }
                )

                PrimaryButton(
                    text = "Compress & Save",
                    onClick = onNavigateToOptions,
                    enabled = selectedImages.isNotEmpty()
                )
            }
        }
    }
}
