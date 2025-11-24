package com.nakibul.hassan.quickcompress.presentation.pdfsplit

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nakibul.hassan.quickcompress.domain.model.PdfPage
import com.nakibul.hassan.quickcompress.presentation.components.ErrorDialog
import com.nakibul.hassan.quickcompress.presentation.components.LoadingDialog
import com.nakibul.hassan.quickcompress.presentation.components.PrimaryButton
import com.nakibul.hassan.quickcompress.presentation.components.RoundedCard
import com.nakibul.hassan.quickcompress.presentation.components.SuccessDialog
import com.nakibul.hassan.quickcompress.presentation.components.TopBar

@Composable
fun PdfSplitScreen(
    viewModel: PdfSplitViewModel = hiltViewModel(),
    onNavigateToResult: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val selectedPdf by viewModel.selectedPdf.collectAsState()
    val pdfPages by viewModel.pdfPages.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    
    var fileName by remember { mutableStateOf("split") }
    
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onPdfSelected(it) }
    }
    
    LaunchedEffect(uiState) {
        if (uiState is PdfSplitUiState.SplitComplete) {
            onNavigateToResult()
        }
    }
    
    Scaffold(
        topBar = {
            TopBar(
                title = "Split PDF",
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
                is PdfSplitUiState.Loading -> {
                    LoadingDialog(message = "Loading PDF...")
                }
                is PdfSplitUiState.Splitting -> {
                    LoadingDialog(message = "Splitting PDF...")
                }
                is PdfSplitUiState.Error -> {
                    val error = (uiState as PdfSplitUiState.Error).message
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
                    text = "Select PDF to Split",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                if (selectedPdf == null) {
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
                                    text = "No PDF selected",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))

                    PrimaryButton(
                        text = "Select PDF",
                        onClick = { pdfPickerLauncher.launch("application/pdf") }
                    )
                } else {
                    RoundedCard {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Output File Prefix",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            OutlinedTextField(
                                value = fileName,
                                onValueChange = { fileName = it },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                label = { Text("File prefix") },
                                supportingText = { Text("Files will be named: ${fileName}_part1.pdf, ${fileName}_part2.pdf, etc.") }
                            )
                        }
                    }

                    RoundedCard {
                        val selectedCount = pdfPages.count { it.isSelected }
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = selectedPdf!!.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Total pages: ${pdfPages.size} | Selected: $selectedCount",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    
                    Text(
                        text = "Select pages to extract",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(pdfPages, key = { it.pageNumber }) { page ->
                            PdfPageThumbnail(
                                page = page,
                                onPageClick = { viewModel.togglePageSelection(page.pageNumber) }
                            )
                        }
                    }

                    PrimaryButton(
                        text = "Split PDF",
                        onClick = { viewModel.splitPdf(fileName) },
                        enabled = pdfPages.any { it.isSelected } && fileName.isNotBlank()
                    )
                }
            }
        }
    }
}

@Composable
private fun PdfPageThumbnail(
    page: PdfPage,
    onPageClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(0.7f)
            .background(
                color = if (page.isSelected)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            )
            .border(
                width = if (page.isSelected) 2.dp else 1.dp,
                color = if (page.isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.medium
            )
            .clickable(onClick = onPageClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (page.thumbnail != null) {
                Image(
                    bitmap = page.thumbnail.asImageBitmap(),
                    contentDescription = "Page ${page.pageNumber}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Page ${page.pageNumber}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (page.isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (page.isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Selected",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
