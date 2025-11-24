package com.nakibul.hassan.quickcompress.presentation.pdfmerge

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nakibul.hassan.quickcompress.presentation.components.ErrorDialog
import com.nakibul.hassan.quickcompress.presentation.components.LoadingDialog
import com.nakibul.hassan.quickcompress.presentation.components.PrimaryButton
import com.nakibul.hassan.quickcompress.presentation.components.RoundedCard
import com.nakibul.hassan.quickcompress.presentation.components.SecondaryButton
import com.nakibul.hassan.quickcompress.presentation.components.TopBar
import com.nakibul.hassan.quickcompress.utils.FileUtils

@Composable
fun PdfMergeScreen(
    viewModel: PdfMergeViewModel = hiltViewModel(),
    onNavigateToResult: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val selectedPdfs by viewModel.selectedPdfs.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    
    var fileName by remember { mutableStateOf("merged.pdf") }
    
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            viewModel.onPdfsSelected(uris)
        }
    }
    
    LaunchedEffect(uiState) {
        if (uiState is PdfMergeUiState.MergeComplete) {
            onNavigateToResult()
        }
    }
    
    Scaffold(
        topBar = {
            TopBar(
                title = "Merge PDFs",
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
                is PdfMergeUiState.Loading -> {
                    LoadingDialog(message = "Loading PDFs...")
                }
                is PdfMergeUiState.Merging -> {
                    LoadingDialog(message = "Merging PDFs...")
                }
                is PdfMergeUiState.Error -> {
                    val error = (uiState as PdfMergeUiState.Error).message
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
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Select PDFs to Merge",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                if (selectedPdfs.isEmpty()) {
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
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "No PDFs selected",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    RoundedCard {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Output File Name",
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
                        Text(
                            text = "${selectedPdfs.size} PDF(s) selected",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(selectedPdfs, key = { it.uri.toString() }) { pdf ->
                            RoundedCard {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(40.dp)
                                    )
                                    
                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = pdf.name,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = FileUtils.formatFileSize(pdf.size),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "${pdf.pageCount} pages",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                SecondaryButton(
                    text = if (selectedPdfs.isEmpty()) "Select PDFs" else "Add More PDFs",
                    onClick = { pdfPickerLauncher.launch("application/pdf") }
                )

                PrimaryButton(
                    text = "Merge PDFs",
                    onClick = { viewModel.mergePdfs(fileName) },
                    enabled = selectedPdfs.size >= 2 && fileName.isNotBlank()
                )
            }
        }
    }
}
