# Compression Debugging Guide

## Issue

Compression summary shows 0% for all statistics and compressed images cannot be found.

## Changes Made

### 1. **ImageRepositoryImpl.kt** - Enhanced Logging & Validation

- Added detailed Timber logging throughout `compressImage()` method:

  - Original file size logging
  - Bitmap decoding status
  - Temp file creation confirmation
  - File save success validation
  - Compressed file size logging
  - Compression ratio calculation

- Added validation checks:
  - Verify temp file save success
  - Check source file exists before copying
  - Log file existence in `saveCompressedImage()`

### 2. **CompressorViewModel.kt** - IO Dispatcher & Enhanced Logging

- Added `withContext(Dispatchers.IO)` for all file operations:

  - `onImagesSelected()` - Image loading on IO thread
  - `compressImages()` - Compression on IO thread
  - `saveImages()` - File saving on IO thread

- Added detailed logging:
  - Image count at each step
  - Compression settings used
  - Individual image statistics (original, compressed, saved sizes)
  - Save operation progress

### 3. **FileUtils.kt** - Error Handling

- Enhanced `getFileSize()` with:
  - Try-catch error handling
  - Logging when SIZE column not found
  - Logging when query returns null
  - Debug log of actual file size retrieved

### 4. **ImageUtils.kt** - Bitmap Operation Logging

- Enhanced `compressImage()` with:

  - Bitmap decode status logging
  - Original bitmap dimensions
  - Resize option logging
  - Final bitmap dimensions

- Enhanced `saveBitmapToFile()` with:
  - Input parameters logging (dimensions, quality)
  - Compress operation success check
  - File size verification after save
  - Warning if file size is 0

## How to Debug

### Step 1: Clear App Data

```bash
adb shell pm clear com.naim.quickcompress
```

### Step 2: Run the App

1. Launch the app
2. Select an image from the compressor feature
3. Let it auto-compress

### Step 3: Monitor Logcat

Filter by tag `System.out` or search for "Timber":

```bash
adb logcat -s System.out:D *:E
```

### Expected Log Flow

#### 1. Image Selection

```
CompressorViewModel: Loading X images
FileUtils: getFileSize for content://... : XXXXX bytes
CompressorViewModel: Loaded X images
```

#### 2. Compression Start

```
CompressorViewModel: Starting compression for X images with settings: CompressionSettings(...)
ImageRepositoryImpl: Starting compression for image: content://..., original size: XXXXX bytes
```

#### 3. Bitmap Processing

```
ImageUtils: compressImage: Decoding bitmap with quality=85, resizeOption=...
ImageUtils: compressImage: Original bitmap size: 4000x3000
ImageUtils: compressImage: Resizing to 1920x1080
ImageUtils: compressImage: Final bitmap size: 1920x1440
```

#### 4. File Save

```
ImageUtils: saveBitmapToFile: Saving 1920x1440 bitmap to /data/.../temp_XXX.jpg with quality=85
ImageUtils: saveBitmapToFile: File saved successfully, size: XXXXX bytes
ImageRepositoryImpl: Temp file saved successfully: XXXXX bytes
ImageRepositoryImpl: Compressed size: XXXXX bytes
```

#### 5. Permanent Storage

```
ImageRepositoryImpl: Source file exists: true
FileUtils: copyUriToFile: Copying to /Android/data/.../QuickCompress/Compressed/image_XXX.jpg
CompressorViewModel: Image: Original=XXXXX, Compressed=XXXXX, Saved=XXXXX
```

### Step 4: Check for Errors

Look for these error patterns:

#### ❌ Original Size = 0

```
FileUtils: getFileSize: SIZE column not found or cursor empty
```

**Fix**: URI permission issue or invalid URI

#### ❌ Bitmap Decode Failed

```
ImageUtils: compressImage: Failed to decode bitmap from stream
```

**Fix**: Corrupt image or unsupported format

#### ❌ File Save Failed

```
ImageUtils: saveBitmapToFile: bitmap.compress returned false
```

**Fix**: Disk full or permission issue

#### ❌ File Size = 0

```
ImageUtils: saveBitmapToFile: File size is 0!
```

**Fix**: Bitmap compression failed or format issue

#### ❌ Copy Failed

```
ImageRepositoryImpl: Source file does not exist
```

**Fix**: Temp file was deleted or path incorrect

## Verification Steps

### 1. Check Temp Files

```bash
adb shell ls -lh /data/data/com.naim.quickcompress/cache/
```

### 2. Check Compressed Files

```bash
adb shell ls -lh /sdcard/Android/data/com.naim.quickcompress/files/QuickCompress/Compressed/
```

### 3. Pull a Compressed File

```bash
adb pull /sdcard/Android/data/com.naim.quickcompress/files/QuickCompress/Compressed/image_XXX.jpg
```

### 4. Check File Properties

```bash
adb shell stat /sdcard/Android/data/com.naim.quickcompress/files/QuickCompress/Compressed/image_XXX.jpg
```

## Common Issues & Solutions

### Issue: All sizes show 0

**Cause**: URI ContentResolver failing to read file size  
**Solution**: Check URI permissions in manifest and runtime

### Issue: Bitmap decode returns null

**Cause**: Stream already consumed or closed  
**Solution**: Reopen stream for each read operation

### Issue: File saves but size is 0

**Cause**: Bitmap.compress() failing silently  
**Solution**: Check format compatibility and disk space

### Issue: Files not found after save

**Cause**: Directory not created or wrong path  
**Solution**: Verify `getCompressedImagesDir()` creates directory

## Next Steps

1. **Run the app with new logging**
2. **Capture full logcat output during compression**
3. **Identify which step shows 0 or fails**
4. **Apply targeted fix based on logs**

## Test Case

### Input

- Select 1-2 images from gallery
- Auto-compress with default settings (quality=85, resize=1920x1080)

### Expected Output

```
Original: 3.2 MB
Compressed: 856 KB
Space Saved: 2.34 MB (73%)
```

### Files Location

```
/sdcard/Android/data/com.naim.quickcompress/files/QuickCompress/Compressed/
```

## Contact Points

All logging uses `Timber.d()`, `Timber.w()`, or `Timber.e()`.  
Filter logcat by `Timber` or the specific class name to trace execution flow.
