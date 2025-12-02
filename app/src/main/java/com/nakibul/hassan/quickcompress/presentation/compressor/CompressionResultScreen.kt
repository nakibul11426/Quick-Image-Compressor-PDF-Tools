package com.nakibul.hassan.quickcompress.presentation.compressor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nakibul.hassan.quickcompress.utils.FileUtils
import com.nakibul.hassan.quickcompress.presentation.components.ErrorDialog
import com.nakibul.hassan.quickcompress.presentation.components.PrimaryButton
import com.nakibul.hassan.quickcompress.presentation.components.RoundedCard
import com.nakibul.hassan.quickcompress.presentation.components.TopBar

@Composable
fun CompressionResultScreen(
    viewModel: CompressorViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit
) {
    val compressedImages by viewModel.compressedImages.collectAsState()
    val selectedImages by viewModel.selectedImages.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    
    // Debug logging
    LaunchedEffect(Unit) {
        timber.log.Timber.d("CompressionResultScreen launched")
        timber.log.Timber.d("UIState: $uiState")
        timber.log.Timber.d("CompressedImages size: ${compressedImages.size}")
        timber.log.Timber.d("SelectedImages size: ${selectedImages.size}")
    }
    
    LaunchedEffect(compressedImages, uiState) {
        timber.log.Timber.d("CompressionResultScreen: compressedImages.size = ${compressedImages.size}, state = $uiState")
        compressedImages.forEachIndexed { index, img ->
            timber.log.Timber.d("Image $index: original=${img.originalSize}, compressed=${img.compressedSize}")
        }
    }
    
    Scaffold(
        topBar = {
            TopBar(
                title = "Compression Results",
                onNavigationClick = onNavigateToHome
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
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
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                        
                        Column {
                            Text(
                                text = "Image Saved Successfully!",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "1 image compressed and saved",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Location: Documents/QuickCompress/CompressedImages/",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
                
                // Summary Stats
                RoundedCard {
                    val totalOriginalSize = compressedImages.sumOf { it.originalSize }
                    val totalCompressedSize = compressedImages.sumOf { it.compressedSize }
                    val totalSaved = totalOriginalSize - totalCompressedSize
                    val averageCompression = if (totalOriginalSize > 0) {
                        ((totalSaved.toFloat() / totalOriginalSize.toFloat()) * 100).toInt()
                    } else 0

                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Summary",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        HorizontalDivider()

                        SummaryRow("Original Size:", FileUtils.formatFileSize(totalOriginalSize))
                        SummaryRow(
                            "Compressed Size:",
                            FileUtils.formatFileSize(totalCompressedSize)
                        )
                        SummaryRow("Space Saved:", FileUtils.formatFileSize(totalSaved))
                        SummaryRow("File Size Reduced:", "$averageCompression%")
                        
                        Text(
                            text = "Note: Compression result varies by image content and selected quality/dimensions",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
                
                // Individual Results
                Text(
                    text = "Individual Results",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                compressedImages.forEachIndexed { index, result ->
                    RoundedCard {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = selectedImages.getOrNull(index)?.name
                                    ?: "Image ${index + 1}",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1
                            )

                            SummaryRow(
                                "Original:",
                                FileUtils.formatFileSize(result.originalSize)
                            )
                            SummaryRow(
                                "Compressed:",
                                FileUtils.formatFileSize(result.compressedSize)
                            )
                            SummaryRow(
                                "Saved:",
                                "${result.percentageSaved.toInt()}% (${
                                    FileUtils.formatFileSize(
                                        result.sizeSaved
                                    )
                                })"
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                val context = LocalContext.current
                Button(
                    onClick = {
                        FileUtils.openDirectory(context, FileUtils.getCompressedImagesDir(context))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FolderOpen,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Open Saved Folder",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))

                PrimaryButton(
                    text = "Done",
                    onClick = {
                        viewModel.reset()
                        onNavigateToHome()
                    }
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
