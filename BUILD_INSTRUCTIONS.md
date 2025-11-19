# 🚀 Build Instructions

## Prerequisites

Before building the project, ensure you have:

1. **Android Studio Hedgehog (2023.1.1) or later**
2. **JDK 11 or higher** (JDK 17 recommended)
3. **Android SDK 29 or higher**
4. **Gradle 8.0+** (automatically installed by Android Studio)

---

## Step-by-Step Build Process

### 1. Open Project in Android Studio

```bash
# Navigate to project directory
cd "C:\Users\BJIT\Desktop\QuickImageCompressor&PDFTools"

# Open with Android Studio
# File → Open → Select the project folder
```

### 2. Sync Gradle Files

- Android Studio will automatically detect the project
- Wait for "Gradle sync" to complete
- If prompted, click **"Sync Now"**
- This will download all dependencies (may take 5-10 minutes first time)

### 3. Resolve Any SDK Issues

If Android Studio shows SDK-related warnings:

- Go to **Tools → SDK Manager**
- Install **Android SDK Platform 36**
- Install **Android SDK Build-Tools 34.0.0** or higher
- Click **Apply** and **OK**

### 4. Build the Project

#### Option A: Using Android Studio UI

1. Click **Build → Make Project** (Ctrl+F9 / Cmd+F9)
2. Wait for build to complete
3. Check the **Build** tab at the bottom for any errors

#### Option B: Using Gradle Command

```bash
# Windows (PowerShell)
.\gradlew.bat assembleDebug

# Mac/Linux
./gradlew assembleDebug
```

### 5. Run the App

#### Option A: On Emulator

1. Click **Tools → Device Manager**
2. Create a new **Virtual Device** (if not exists)
   - Recommended: Pixel 6 with Android 13 (API 33) or higher
3. Click **Run** (Shift+F10 / Ctrl+R)
4. Select your emulator

#### Option B: On Physical Device

1. Enable **Developer Options** on your Android device
2. Enable **USB Debugging**
3. Connect device via USB
4. Click **Run** and select your device

---

## Common Issues & Solutions

### Issue 1: "Failed to resolve: com.google.dagger:hilt-android"

**Solution**:

- Ensure you have an internet connection
- Click **File → Invalidate Caches → Invalidate and Restart**
- Sync Gradle again

### Issue 2: "Installed Build Tools revision X is corrupted"

**Solution**:

- Go to **SDK Manager → SDK Tools**
- Uninstall and reinstall **Android SDK Build-Tools**

### Issue 3: "Manifest merger failed"

**Solution**:

- The AndroidManifest.xml is already configured correctly
- If issues persist, click **Build → Clean Project**
- Then **Build → Rebuild Project**

### Issue 4: KSP errors

**Solution**:

- Ensure KSP plugin version matches Kotlin version in `libs.versions.toml`
- Current setup: Kotlin 2.0.21, KSP 2.0.21-1.0.28

### Issue 5: "Cannot find symbol BuildConfig"

**Solution**:

- BuildConfig is used for DEBUG flag in QuickCompressApplication
- It's automatically generated during build
- If errors occur, use `AppConfig.kt` constants instead

---

## Gradle Tasks Reference

```bash
# Clean build
.\gradlew.bat clean

# Build debug APK
.\gradlew.bat assembleDebug

# Build release APK (requires signing config)
.\gradlew.bat assembleRelease

# Run unit tests
.\gradlew.bat test

# Run all checks (lint + tests)
.\gradlew.bat check

# Install debug APK on connected device
.\gradlew.bat installDebug

# View all available tasks
.\gradlew.bat tasks
```

---

## Output Locations

After successful build:

- **Debug APK**: `app/build/outputs/apk/debug/app-debug.apk`
- **Release APK**: `app/build/outputs/apk/release/app-release.apk`
- **AAB (for Play Store)**: `app/build/outputs/bundle/release/app-release.aab`

---

## Testing the App

### Manual Testing Checklist

#### Image Compression

1. ✅ Open app → Tap "Image Compressor"
2. ✅ Select 2-3 images
3. ✅ Adjust quality slider
4. ✅ Try different resize options
5. ✅ Compress images
6. ✅ Verify statistics are displayed
7. ✅ Save images
8. ✅ Check files in device storage

#### Image to PDF

1. ✅ Tap "Image to PDF"
2. ✅ Select 3-5 images
3. ✅ Choose page size (A4)
4. ✅ Create PDF
5. ✅ Open PDF with viewer to verify

#### PDF Merge

1. ✅ Tap "Merge PDFs"
2. ✅ Select 2+ PDF files
3. ✅ Merge PDFs
4. ✅ Verify merged PDF opens correctly

#### PDF Split

1. ✅ Tap "Split PDF"
2. ✅ Select a multi-page PDF
3. ✅ Select specific pages
4. ✅ Split PDF
5. ✅ Verify split files

---

## Performance Tips

### For Faster Builds

1. Enable **Gradle Build Cache**:

   - Add to `gradle.properties`:
     ```properties
     org.gradle.caching=true
     org.gradle.parallel=true
     ```

2. Increase **JVM Heap Size**:

   ```properties
   org.gradle.jvmargs=-Xmx4096m -XX:+HeapDumpOnOutOfMemoryError
   ```

3. Use **Configuration Cache** (Experimental):
   ```properties
   org.gradle.configuration-cache=true
   ```

---

## Release Build Setup

To create a release build for Play Store:

1. **Create Keystore**:

   ```bash
   keytool -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-key-alias
   ```

2. **Add to `app/build.gradle.kts`**:

   ```kotlin
   android {
       signingConfigs {
           create("release") {
               storeFile = file("path/to/my-release-key.jks")
               storePassword = "your-keystore-password"
               keyAlias = "my-key-alias"
               keyPassword = "your-key-password"
           }
       }
       buildTypes {
           release {
               signingConfig = signingConfigs.getByName("release")
               // ... existing config
           }
       }
   }
   ```

3. **Build Release**:
   ```bash
   .\gradlew.bat bundleRelease
   ```

---

## Next Steps After Build

1. ✅ Test all features thoroughly
2. ✅ Run on multiple devices/screen sizes
3. ✅ Test on different Android versions (10, 11, 12, 13, 14)
4. ✅ Add app icon (currently using default launcher icon)
5. ✅ Integrate AdMob (placeholders are ready)
6. ✅ Set up Firebase Analytics (optional)
7. ✅ Add crash reporting (Firebase Crashlytics)
8. ✅ Prepare Play Store listing

---

## Support

If you encounter any issues:

1. Check the **Build** output tab in Android Studio
2. Review **Logcat** for runtime errors
3. Ensure all dependencies are properly synced
4. Try **File → Invalidate Caches → Invalidate and Restart**

---

**Build Time Estimate**: 3-5 minutes (first build), 30-60 seconds (incremental builds)

**APK Size**: ~10-15 MB (debug), ~8-10 MB (release with ProGuard/R8)

---

✅ **Project is ready to build and run!**
