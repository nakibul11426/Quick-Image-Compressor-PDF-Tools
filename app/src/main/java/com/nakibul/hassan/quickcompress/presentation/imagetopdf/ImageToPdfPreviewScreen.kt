package com.nakibul.hassan.quickcompress.presentation.imagetopdf

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nakibul.hassan.quickcompress.domain.model.MarginOption
import com.nakibul.hassan.quickcompress.domain.model.PageSize
import com.nakibul.hassan.quickcompress.domain.model.PdfSettings
import com.nakibul.hassan.quickcompress.presentation.components.ErrorDialog
import com.nakibul.hassan.quickcompress.presentation.components.LoadingDialog
import com.nakibul.hassan.quickcompress.presentation.components.PrimaryButton
import com.nakibul.hassan.quickcompress.presentation.components.RoundedCard
import com.nakibul.hassan.quickcompress.presentation.components.TopBar

@Composable
fun ImageToPdfPreviewScreen(
    viewModel: ImageToPdfViewModel,
    onNavigateToResult: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val pdfSettings by viewModel.pdfSettings.collectAsState()
    val selectedImages by viewModel.selectedImages.collectAsState()
    
    var fileName by remember { mutableStateOf("document.pdf") }
    var selectedPageSize by remember { mutableStateOf(pdfSettings.pageSize) }
    var selectedMargin by remember { mutableStateOf(pdfSettings.margins) }
    
    LaunchedEffect(uiState) {
        if (uiState is ImageToPdfUiState.PdfCreated) {
            onNavigateToResult()
        }
    }
    
    Scaffold(
        topBar = {
            TopBar(
                title = "Create PDF",
                onNavigationClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Always show content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Configure PDF Settings",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                RoundedCard {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "PDF Name",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        OutlinedTextField(
                            value = fileName,
                            onValueChange = { fileName = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            label = { Text("File name") },
                            suffix = { Text(".pdf") }
                        )
                    }
                }

                RoundedCard {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Page Size",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        PageSize.entries.forEach { size ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedPageSize == size,
                                    onClick = { selectedPageSize = size }
                                )
                                Text(
                                    text = size.displayName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                RoundedCard {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Margins",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        MarginOption.entries.forEach { margin ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedMargin == margin,
                                    onClick = { selectedMargin = margin }
                                )
                                Text(
                                    text = margin.displayName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                RoundedCard {
                    Text(
                        text = "${selectedImages.size} image(s) will be included",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))

                PrimaryButton(
                    text = "Create PDF",
                    onClick = {
                        val settings = PdfSettings(
                            pageSize = selectedPageSize,
                            margins = selectedMargin
                        )
                        viewModel.updatePdfSettings(settings)
                        viewModel.createPdf(fileName)
                    },
                    enabled = fileName.isNotBlank()
                )
            }
            
            // Show dialogs as overlays
            if (uiState is ImageToPdfUiState.Creating) {
                LoadingDialog(message = "Creating PDF...")
            }
            
            if (uiState is ImageToPdfUiState.Error) {
                val error = (uiState as ImageToPdfUiState.Error).message
                ErrorDialog(
                    message = error,
                    onDismiss = { viewModel.reset() }
                )
            }

        }
    }
}
