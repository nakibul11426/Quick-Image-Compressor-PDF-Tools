package com.nakibul.hassan.quickcompress.presentation.compressor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nakibul.hassan.quickcompress.domain.model.CompressionSettings
import com.nakibul.hassan.quickcompress.presentation.components.ErrorDialog
import com.nakibul.hassan.quickcompress.presentation.components.LoadingDialog
import com.nakibul.hassan.quickcompress.presentation.components.TopBar

@Composable
fun CompressionOptionsScreen(
    viewModel: CompressorViewModel = hiltViewModel(),
    onNavigateToResult: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val settings by viewModel.compressionSettings.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    
    var quality by remember { mutableIntStateOf(settings.quality) }
    var selectedResizeOption by remember { mutableStateOf(settings.resizeOption) }
    var isProcessing by remember { mutableStateOf(false) }
    var showLoadingDialog by remember { mutableStateOf(false) }
    var loadingMessage by remember { mutableStateOf("") }
    
    // Handle state changes with minimum display time for loading dialogs
    LaunchedEffect(uiState) {
        when (uiState) {
            is CompressorUiState.Compressing -> {
                if (isProcessing && !showLoadingDialog) {
                    loadingMessage = "Compressing image..."
                    showLoadingDialog = true
                }
            }
            is CompressorUiState.CompressionComplete -> {
                // Keep showing loading for at least 800ms before transitioning to save
                if (isProcessing) {
                    kotlinx.coroutines.delay(800)
                    loadingMessage = "Saving compressed image..."
                    viewModel.saveImages()
                }
            }
            is CompressorUiState.Saving -> {
                if (isProcessing) {
                    loadingMessage = "Saving compressed image..."
                }
            }
            is CompressorUiState.SaveComplete -> {
                // Keep showing loading for at least 500ms before navigating
                if (isProcessing) {
                    kotlinx.coroutines.delay(500)
                    showLoadingDialog = false
                    onNavigateToResult()
                }
            }
            is CompressorUiState.Error -> {
                showLoadingDialog = false
                isProcessing = false
            }
            else -> {}
        }
    }
    
    Scaffold(
        topBar = {
            TopBar(
                title = "Compression Settings",
                onNavigationClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Main content - always rendered to prevent flickering
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Quality Slider
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Image Quality",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Text(
                            text = "$quality%",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Text(
                            text = "Lower quality = smaller file size",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                        
                        Slider(
                            value = quality.toFloat(),
                            onValueChange = { quality = it.toInt() },
                            valueRange = 10f..100f,
                            steps = 17,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isProcessing
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "10% (Smallest)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "100% (Best)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
                
                // Dimension Selection
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Image Dimensions",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        com.nakibul.hassan.quickcompress.domain.model.ResizeOption.values().forEach { option ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = !isProcessing) { 
                                        selectedResizeOption = option 
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedResizeOption == option,
                                    onClick = { selectedResizeOption = option },
                                    enabled = !isProcessing
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = option.displayName,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (option != com.nakibul.hassan.quickcompress.domain.model.ResizeOption.ORIGINAL) {
                                        Text(
                                            text = "${option.width} × ${option.height} pixels",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Compress Button
                com.nakibul.hassan.quickcompress.presentation.components.PrimaryButton(
                    text = "Compress & Save",
                    onClick = {
                        val newSettings = CompressionSettings(
                            quality = quality,
                            resizeOption = selectedResizeOption
                        )
                        viewModel.updateCompressionSettings(newSettings)
                        isProcessing = true
                        showLoadingDialog = true
                        loadingMessage = "Compressing image..."
                        viewModel.compressImages()
                    },
                    enabled = !isProcessing
                )
            }
            
            // Overlay loading dialog on top of content - prevents flickering
            if (showLoadingDialog) {
                LoadingDialog(message = loadingMessage)
            }
            
            // Show error dialog
            if (uiState is CompressorUiState.Error) {
                val error = (uiState as CompressorUiState.Error).message
                ErrorDialog(
                    message = error,
                    onDismiss = {
                        isProcessing = false
                        showLoadingDialog = false
                        viewModel.reset()
                        onNavigateBack()
                    }
                )
            }
        }
    }
}
