package com.nakibul.hassan.quickcompress.presentation.imagetopdf

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
import com.nakibul.hassan.quickcompress.presentation.components.ImageThumbnailGrid
import com.nakibul.hassan.quickcompress.presentation.components.LoadingDialog
import com.nakibul.hassan.quickcompress.presentation.components.PrimaryButton
import com.nakibul.hassan.quickcompress.presentation.components.RoundedCard
import com.nakibul.hassan.quickcompress.presentation.components.SecondaryButton
import com.nakibul.hassan.quickcompress.presentation.components.TopBar

@Composable
fun ImageToPdfPickerScreen(
    viewModel: ImageToPdfViewModel,
    onNavigateToOrder: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val selectedImages by viewModel.selectedImages.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            if (selectedImages.isEmpty()) {
                viewModel.onImagesSelected(uris)
            } else {
                viewModel.addMoreImages(uris)
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopBar(
                title = "Select Images for PDF",
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
                is ImageToPdfUiState.Loading -> {
                    LoadingDialog(message = "Loading images...")
                }
                is ImageToPdfUiState.Error -> {
                    val error = (uiState as ImageToPdfUiState.Error).message
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
                    text = "Choose images to convert to PDF",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Text(
                    text = "Select one or multiple images. All selected images will be combined into a single PDF file.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                                    text = "No images selected",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    RoundedCard {
                        Text(
                            text = "${selectedImages.size} image(s) selected",
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
                }
                
                Spacer(modifier = Modifier.weight(1f))

                SecondaryButton(
                    text = if (selectedImages.isEmpty()) "Select Images" else "Add More Images",
                    onClick = { imagePickerLauncher.launch("image/*") }
                )

                PrimaryButton(
                    text = "Next",
                    onClick = onNavigateToOrder,
                    enabled = selectedImages.isNotEmpty()
                )
            }
        }
    }
}
