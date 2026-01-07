# Build Instructions for Community Fund Manager

This document provides detailed instructions for building the Community Fund Manager Android application.

## Prerequisites

### Required Software
1. **Android Studio** (Arctic Fox or later)
   - Download from: https://developer.android.com/studio
   
2. **Java Development Kit (JDK) 8 or higher**
   - Verify installation: `java -version`
   - Download from: https://adoptopenjdk.net/

3. **Android SDK**
   - Installed automatically with Android Studio
   - Minimum SDK: API 24 (Android 7.0)
   - Target SDK: API 34 (Android 14)

### Firebase Account
- Create a free account at: https://console.firebase.google.com/

## Step-by-Step Build Process

### 1. Clone the Repository

```bash
git clone https://github.com/asifmahmud71/community-fund-manager.git
cd community-fund-manager
```

### 2. Firebase Setup (REQUIRED)

The app will not build without proper Firebase configuration.

#### A. Create Firebase Project
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project"
3. Enter project name: "Community Fund Manager"
4. Follow the setup wizard

#### B. Add Android App
1. In Firebase Console, click "Add app" → Android
2. Enter package name: `com.fundmanager`
3. Register app
4. Download `google-services.json`

#### C. Replace Configuration File
```bash
# Replace the placeholder file with your downloaded file
cp ~/Downloads/google-services.json app/google-services.json
```

#### D. Enable Firebase Services
1. **Authentication**:
   - Navigate to Authentication → Sign-in method
   - Enable "Email/Password"

2. **Realtime Database**:
   - Navigate to Realtime Database → Create Database
   - Start in test mode (or copy rules from FIREBASE_SECURITY_RULES.md)

3. **Cloud Messaging**:
   - Navigate to Cloud Messaging
   - Note your Server Key (optional for basic functionality)

For detailed Firebase setup, see [FIREBASE_SETUP.md](FIREBASE_SETUP.md)

### 3. Build Using Android Studio

#### Open Project
1. Launch Android Studio
2. File → Open
3. Navigate to the cloned repository
4. Select the root directory
5. Click "OK"

#### Sync Gradle
1. Android Studio will automatically prompt to sync Gradle
2. If not, click "Sync Now" in the banner
3. Wait for sync to complete (this may take a few minutes on first run)

#### Build the App
1. Build → Make Project (or press Ctrl+F9 / Cmd+F9)
2. Wait for build to complete
3. Check "Build" window for any errors

#### Run the App
1. Connect an Android device (USB debugging enabled) or start an emulator
2. Select device from the dropdown in the toolbar
3. Click "Run" button (green play icon) or press Shift+F10
4. App will install and launch on the device/emulator

### 4. Build Using Command Line

#### Gradle Wrapper (Recommended)

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK (requires signing configuration)
./gradlew assembleRelease

# Install debug APK on connected device
./gradlew installDebug

# Run all checks
./gradlew check

# Clean build
./gradlew clean
```

#### Direct Gradle (if gradle is installed globally)

```bash
# Build debug APK
gradle assembleDebug

# Build and install
gradle installDebug
```

### 5. Build Output Locations

After successful build, APK files will be located at:

```
app/build/outputs/apk/debug/app-debug.apk
app/build/outputs/apk/release/app-release-unsigned.apk
```

## Common Build Issues and Solutions

### Issue 1: "google-services.json not found"
**Solution**: Ensure you've downloaded and placed the `google-services.json` file in the `app/` directory.

### Issue 2: Gradle sync failed
**Solution**: 
```bash
# Clean gradle cache
./gradlew clean
rm -rf .gradle/
rm -rf build/
rm -rf app/build/

# Re-sync
./gradlew build
```

### Issue 3: SDK location not found
**Solution**: Create or update `local.properties`:
```properties
sdk.dir=/path/to/your/Android/Sdk
```

### Issue 4: Dependency resolution errors
**Solution**:
1. Check internet connection
2. Clear Gradle cache:
   ```bash
   ./gradlew clean --refresh-dependencies
   ```
3. In Android Studio: File → Invalidate Caches / Restart

### Issue 5: Build tools version mismatch
**Solution**: Update `build.gradle` to match your installed build tools version or install the required version via SDK Manager.

## Building for Production

### Generate Signed APK

1. **Create Keystore** (first time only):
   ```bash
   keytool -genkey -v -keystore my-release-key.keystore -alias my-key-alias -keyalg RSA -keysize 2048 -validity 10000
   ```

2. **Configure Signing** in `app/build.gradle`:
   ```gradle
   android {
       signingConfigs {
           release {
               storeFile file("path/to/my-release-key.keystore")
               storePassword "your-keystore-password"
               keyAlias "my-key-alias"
               keyPassword "your-key-password"
           }
       }
       buildTypes {
           release {
               signingConfig signingConfigs.release
               minifyEnabled true
               proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
           }
       }
   }
   ```

3. **Build Signed APK**:
   ```bash
   ./gradlew assembleRelease
   ```
   
   Or via Android Studio:
   - Build → Generate Signed Bundle / APK
   - Select APK
   - Choose keystore and enter passwords
   - Select "release" build variant
   - Click "Finish"

### Generate Android App Bundle (AAB) for Play Store

```bash
./gradlew bundleRelease
```

Output: `app/build/outputs/bundle/release/app-release.aab`

## Verification

### Verify APK Installation
```bash
# List installed packages
adb shell pm list packages | grep fundmanager

# Get app info
adb shell dumpsys package com.fundmanager
```

### Run Tests
```bash
# Run unit tests
./gradlew test

# Run instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest
```

## Build Configuration Details

### Gradle Version
- Gradle: 8.2
- Android Gradle Plugin: 8.2.0

### SDK Versions
- minSdkVersion: 24
- targetSdkVersion: 34
- compileSdkVersion: 34

### Dependencies
See `app/build.gradle` for full list of dependencies including:
- AndroidX libraries
- Firebase SDK
- Material Design Components
- MPAndroidChart

## Additional Resources

- [Android Developer Guide](https://developer.android.com/guide)
- [Firebase Documentation](https://firebase.google.com/docs)
- [Gradle Build Tool](https://gradle.org/)

## Support

For build issues:
1. Check this guide thoroughly
2. Review error messages in Android Studio's "Build" window
3. Search for error messages online
4. Open an issue on GitHub with:
   - Error message
   - Build environment details
   - Steps to reproduce

## Notes

- First build may take 5-10 minutes as dependencies are downloaded
- Subsequent builds are much faster due to caching
- Always use the latest stable version of Android Studio
- Keep Firebase SDK up to date for security patches
