package com.nakibul.hassan.quickcompress.presentation.pdfsplit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nakibul.hassan.quickcompress.presentation.components.PrimaryButton
import com.nakibul.hassan.quickcompress.presentation.components.RoundedCard
import com.nakibul.hassan.quickcompress.presentation.components.TopBar

@Composable
fun PdfSplitResultScreen(
    viewModel: PdfSplitViewModel,
    onNavigateToHome: () -> Unit
) {
    val selectedPdf by viewModel.selectedPdf.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    
    val splitInfo = remember(uiState) {
        if (uiState is PdfSplitUiState.SplitComplete) {
            val state = uiState as PdfSplitUiState.SplitComplete
            Pair(state.splitPdfUris.size, state.pageCount)
        } else Pair(0, 0)
    }
    
    Scaffold(
        topBar = {
            TopBar(
                title = "PDF Split Complete",
                onNavigationClick = onNavigateToHome
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Success Header Card
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
                            text = "PDF Split Successfully!",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "${splitInfo.second} page${if (splitInfo.second > 1) "s" else ""} extracted into ${splitInfo.first} PDF${if (splitInfo.first > 1) "s" else ""}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            
            // Summary Statistics
            RoundedCard {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Summary",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    HorizontalDivider()

                    SummaryRow(
                        icon = Icons.Default.Description,
                        label = "Source PDF:",
                        value = selectedPdf?.name ?: "Unknown"
                    )
                    
                    SummaryRow(
                        icon = Icons.Default.Description,
                        label = "Pages Extracted:",
                        value = "${splitInfo.second}"
                    )
                    
                    SummaryRow(
                        icon = Icons.Default.Description,
                        label = "Files Created:",
                        value = "${splitInfo.first}"
                    )
                    
                    SummaryRow(
                        icon = Icons.Default.Folder,
                        label = "Location:",
                        value = "Documents/QuickCompress/PDFs/"
                    )
                }
            }
            
            // File Information
            RoundedCard {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "File Details",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    HorizontalDivider()
                    
                    Text(
                        text = "The extracted pages have been saved as separate PDF files to your device's Documents folder under QuickCompress/PDFs/",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = "You can access them from your file manager or any PDF reader application.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            
            // Split Info Card
            RoundedCard {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Split Details",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    HorizontalDivider()
                    
                    Text(
                        text = "Each selected page has been saved as an individual PDF file with sequential numbering.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = "Example: split_part1.pdf, split_part2.pdf, etc.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

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

@Composable
private fun SummaryRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}
