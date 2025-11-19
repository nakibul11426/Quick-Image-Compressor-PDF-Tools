package com.nakibul.hassan.quickcompress.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nakibul.hassan.quickcompress.presentation.compressor.CompressorPickerScreen
import com.nakibul.hassan.quickcompress.presentation.compressor.CompressionOptionsScreen
import com.nakibul.hassan.quickcompress.presentation.compressor.CompressionResultScreen
import com.nakibul.hassan.quickcompress.presentation.home.HomeScreen
import com.nakibul.hassan.quickcompress.presentation.imagetopdf.ImageToPdfPickerScreen
import com.nakibul.hassan.quickcompress.presentation.imagetopdf.ImageToPdfPreviewScreen
import com.nakibul.hassan.quickcompress.presentation.pdfmerge.PdfMergeScreen
import com.nakibul.hassan.quickcompress.presentation.pdfsplit.PdfSplitScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // Home Screen
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToCompressor = {
                    navController.navigate(Screen.CompressorPicker.route)
                },
                onNavigateToImageToPdf = {
                    navController.navigate(Screen.ImageToPdfPicker.route)
                },
                onNavigateToPdfMerge = {
                    navController.navigate(Screen.PdfMerge.route)
                },
                onNavigateToPdfSplit = {
                    navController.navigate(Screen.PdfSplit.route)
                }
            )
        }
        
        // Image Compressor Flow - Share ViewModel across all compressor screens
        composable(Screen.CompressorPicker.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.CompressorPicker.route)
            }
            CompressorPickerScreen(
                viewModel = hiltViewModel(parentEntry),
                onNavigateToOptions = {
                    navController.navigate(Screen.CompressorOptions.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.CompressorOptions.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.CompressorPicker.route)
            }
            CompressionOptionsScreen(
                viewModel = hiltViewModel(parentEntry),
                onNavigateToResult = {
                    navController.navigate(Screen.CompressorResult.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.CompressorResult.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.CompressorPicker.route)
            }
            CompressionResultScreen(
                viewModel = hiltViewModel(parentEntry),
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Image to PDF Flow - Share ViewModel across both screens
        composable(Screen.ImageToPdfPicker.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.ImageToPdfPicker.route)
            }
            ImageToPdfPickerScreen(
                viewModel = hiltViewModel(parentEntry),
                onNavigateToOrder = {
                    navController.navigate(Screen.ImageToPdfPreview.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.ImageToPdfPreview.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.ImageToPdfPicker.route)
            }
            ImageToPdfPreviewScreen(
                viewModel = hiltViewModel(parentEntry),
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // PDF Merge
        composable(Screen.PdfMerge.route) {
            PdfMergeScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // PDF Split
        composable(Screen.PdfSplit.route) {
            PdfSplitScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
