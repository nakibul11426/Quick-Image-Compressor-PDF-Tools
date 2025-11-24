package com.nakibul.hassan.quickcompress.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nakibul.hassan.quickcompress.presentation.components.ToolCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCompressor: () -> Unit,
    onNavigateToImageToPdf: () -> Unit,
    onNavigateToPdfMerge: () -> Unit,
    onNavigateToPdfSplit: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quick Image Compressor & PDF Tools") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Quick Tools",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Choose a tool to get started with image and PDF processing",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Ad placeholder
            BannerAdView()
            
            // Image Compressor Tool
            ToolCard(
                title = "Image Compressor",
                description = "Compress single or multiple images to reduce file size",
                icon = {
                    Icon(
                        imageVector = Icons.Default.Compress,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                },
                onClick = onNavigateToCompressor
            )
            
            // Image to PDF Tool
            ToolCard(
                title = "Image to PDF",
                description = "Convert multiple images into a single PDF document",
                icon = {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                },
                onClick = onNavigateToImageToPdf
            )
            
            // PDF Merge Tool
            ToolCard(
                title = "Merge PDFs",
                description = "Combine multiple PDF files into one document",
                icon = {
                    Icon(
                        imageVector = Icons.Default.MergeType,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                },
                onClick = onNavigateToPdfMerge
            )
            
            // PDF Split Tool
            ToolCard(
                title = "Split PDF",
                description = "Extract specific pages from a PDF document",
                icon = {
                    Icon(
                        imageVector = Icons.Default.ContentCut,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                },
                onClick = onNavigateToPdfSplit
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun BannerAdView() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Ad Placeholder",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
