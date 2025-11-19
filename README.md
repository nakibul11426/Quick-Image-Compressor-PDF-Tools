# Quick Image Compressor & PDF Tools

A complete Android application built with Kotlin, Jetpack Compose, and MVVM architecture for image compression and PDF manipulation.

## Features

### 🖼️ Image Compression

- Compress single or multiple images
- Adjustable quality settings (10-100%)
- Multiple resize options (Original, 1920x1080, 1280x720, 720x480)
- Before/after comparison with compression statistics
- Save and share compressed images

### 📄 Image to PDF

- Convert multiple images into a single PDF
- Reorder images before conversion
- Customizable page size (A4, Letter, Fit-to-image)
- Margin options (None, Small)
- Save and share generated PDFs

### 🔗 PDF Merge

- Combine multiple PDF files into one
- View page count and file size before merging
- Reorder PDFs before merging
- Save and share merged PDFs

### ✂️ PDF Split

- Extract specific pages from a PDF
- Visual page selection with thumbnails
- Split into multiple files based on selection
- Save and share split PDFs

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: MVVM + Clean Architecture
- **Dependency Injection**: Hilt
- **Image Loading**: Coil
- **Navigation**: Jetpack Navigation Compose
- **Concurrency**: Kotlin Coroutines + Flow
- **Logging**: Timber

## Architecture

The app follows Clean Architecture principles with three main layers:

### Domain Layer

- **Models**: Business entities (`ImageItem`, `CompressedImage`, `PdfDocument`, etc.)
- **Repository Interfaces**: Abstract contracts for data operations
- **Use Cases**: Single-responsibility business logic components

### Data Layer

- **Repository Implementations**: Concrete implementations of domain repositories
- **Utilities**: Helper classes for image/PDF processing

### Presentation Layer

- **ViewModels**: State management and business logic orchestration
- **Compose Screens**: UI components
- **Navigation**: Navigation graph and screen routing
- **Theme**: Material 3 theming and reusable components

## Project Structure

```
com.naim.quickcompress/
├── data/
│   └── repository/
│       ├── ImageRepositoryImpl.kt
│       └── PdfRepositoryImpl.kt
├── domain/
│   ├── model/
│   │   ├── CompressedImage.kt
│   │   ├── CompressionSettings.kt
│   │   ├── ImageItem.kt
│   │   ├── PdfDocument.kt
│   │   ├── PdfPage.kt
│   │   └── PdfSettings.kt
│   ├── repository/
│   │   ├── ImageRepository.kt
│   │   └── PdfRepository.kt
│   └── usecase/
│       ├── CompressImagesUseCase.kt
│       ├── CreatePdfFromImagesUseCase.kt
│       ├── GetImageDetailsUseCase.kt
│       ├── GetPdfDetailsUseCase.kt
│       ├── GetPdfPagesUseCase.kt
│       ├── MergePdfUseCase.kt
│       ├── SaveCompressedImageUseCase.kt
│       └── SplitPdfUseCase.kt
├── presentation/
│   ├── components/
│   │   ├── Buttons.kt
│   │   ├── Cards.kt
│   │   ├── Dialogs.kt
│   │   ├── FileInfo.kt
│   │   ├── ImageThumbnail.kt
│   │   └── TopBar.kt
│   ├── compressor/
│   │   ├── CompressorViewModel.kt
│   │   ├── CompressorPickerScreen.kt
│   │   ├── CompressionOptionsScreen.kt
│   │   └── CompressionResultScreen.kt
│   ├── imagetopdf/
│   │   ├── ImageToPdfViewModel.kt
│   │   ├── ImageToPdfPickerScreen.kt
│   │   └── ImageToPdfPreviewScreen.kt
│   ├── pdfmerge/
│   │   ├── PdfMergeViewModel.kt
│   │   └── PdfMergeScreen.kt
│   ├── pdfsplit/
│   │   ├── PdfSplitViewModel.kt
│   │   └── PdfSplitScreen.kt
│   ├── home/
│   │   └── HomeScreen.kt
│   ├── navigation/
│   │   ├── AppNavigation.kt
│   │   └── Screen.kt
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
├── utils/
│   ├── FileUtils.kt
│   ├── ImageUtils.kt
│   └── PdfUtils.kt
├── di/
│   ├── AppModule.kt
│   ├── RepositoryModule.kt
│   └── UseCaseModule.kt
├── MainActivity.kt
└── QuickCompressApplication.kt
```

## Key Implementation Details

### Image Compression

- Uses Android's `BitmapFactory` and `Bitmap.compress()` API
- Supports EXIF data for proper image orientation
- Implements efficient memory management for large images
- Uses coroutines for background processing

### PDF Operations

- Leverages Android's `PdfDocument` API for PDF creation
- Uses `PdfRenderer` for reading and displaying PDF pages
- Implements bitmap caching for thumbnail generation
- Handles page-by-page rendering for large PDFs

### File Management

- Uses Storage Access Framework (SAF) for file picking
- Saves files to app-specific external storage
- Implements FileProvider for secure file sharing
- Generates unique filenames to prevent conflicts

### State Management

- ViewModels expose `StateFlow` for reactive UI updates
- Sealed classes for type-safe state representation
- Proper lifecycle awareness with Compose integration
- Error handling with user-friendly messages

## Build & Run

### Requirements

- Android Studio Hedgehog or later
- JDK 11 or higher
- Android SDK 29 or higher
- Gradle 8.0 or higher

### Setup

1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Run the app on an emulator or physical device (API 29+)

### Gradle Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Run tests
./gradlew test

# Clean build
./gradlew clean build
```

## Permissions

The app requires the following permissions:

- `READ_MEDIA_IMAGES` (Android 13+)
- `READ_EXTERNAL_STORAGE` (Android 12 and below)
- `WRITE_EXTERNAL_STORAGE` (Android 9 and below)

All file operations use SAF, making the app compatible with scoped storage requirements.

## Future Enhancements

- [ ] PDF password protection
- [ ] PDF page rotation and deletion
- [ ] Batch processing with progress tracking
- [ ] Cloud storage integration
- [ ] PDF annotation tools
- [ ] Image watermarking
- [ ] OCR support for PDFs
- [ ] Dark mode theme improvements
- [ ] Widget for quick access
- [ ] In-app premium features

## License

This project is for demonstration purposes. Modify as needed for your use case.

## Credits

Built with:

- Jetpack Compose
- Material Design 3
- Coil Image Loading Library
- Timber Logging Library
- Hilt Dependency Injection

---

**Version**: 1.0  
**Min SDK**: 29 (Android 10)  
**Target SDK**: 36 (Android 14+)
