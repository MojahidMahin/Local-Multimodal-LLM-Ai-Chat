# Installation Guide

This guide will help you install and set up LocalAiChatApp on your development machine and Android device.

## Table of Contents
- [System Requirements](#system-requirements)
- [Development Environment Setup](#development-environment-setup)
- [Building the App](#building-the-app)
- [Installing on Device](#installing-on-device)
- [Troubleshooting](#troubleshooting)

## System Requirements

### For Development

#### Minimum Requirements
- **Operating System**: Windows 10/11, macOS 10.14+, or Linux (Ubuntu 18.04+)
- **RAM**: 8 GB minimum (16 GB recommended)
- **Disk Space**: 10 GB free space minimum
- **Java**: JDK 21 or later
- **Android Studio**: Hedgehog | 2023.1.1 or later

#### Android Device Requirements
- **Android Version**: Android 7.0 (API 24) or higher
- **RAM**: 4 GB minimum (6 GB+ recommended for better AI performance)
- **Storage**: 2 GB free space minimum (depends on AI models downloaded)
- **Architecture**: ARM64 (armeabi-v7a, arm64-v8a)

### Supported Platforms
- Android 7.0 Nougat (API 24) - Android 14 (API 35)
- Optimized for Android 12+ with Material You theming

## Development Environment Setup

### Step 1: Install Android Studio

1. **Download Android Studio**
   - Visit [Android Studio Download Page](https://developer.android.com/studio)
   - Download the latest stable version (Hedgehog 2023.1.1 or later)

2. **Install Android Studio**

   **Windows:**
   ```bash
   # Run the downloaded .exe installer
   # Follow the installation wizard
   ```

   **macOS:**
   ```bash
   # Open the downloaded .dmg file
   # Drag Android Studio to Applications folder
   ```

   **Linux (Ubuntu/Debian):**
   ```bash
   sudo snap install android-studio --classic
   # Or download tar.gz and extract to /opt/
   ```

3. **Launch Android Studio**
   - Open Android Studio
   - Complete the setup wizard
   - Download the recommended SDK components

### Step 2: Install Required SDK Components

1. Open Android Studio
2. Go to **Tools > SDK Manager**
3. Install the following:
   - **SDK Platforms**: Android 14.0 (API 35)
   - **SDK Tools**:
     - Android SDK Build-Tools 35.0.0
     - Android SDK Platform-Tools
     - Android Emulator
     - Google Play services
     - Intel/AMD x86 Emulator Accelerator (HAXM)

### Step 3: Install Java Development Kit (JDK)

The project requires **JDK 21**.

**Check current Java version:**
```bash
java -version
```

**Install JDK 21:**

**Windows/macOS/Linux:**
```bash
# Download from Oracle or use package manager
# Ubuntu/Debian:
sudo apt-get update
sudo apt-get install openjdk-21-jdk

# macOS (using Homebrew):
brew install openjdk@21

# Windows (using Chocolatey):
choco install openjdk21
```

### Step 4: Configure Git

```bash
# Install Git
# Ubuntu/Debian:
sudo apt-get install git

# macOS (using Homebrew):
brew install git

# Windows: Download from https://git-scm.com/download/win

# Configure Git
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"
```

## Building the App

### Step 1: Clone the Repository

```bash
# Clone the repository
git clone https://github.com/yourusername/Local-Multimodal-LLM-Ai-Chat.git

# Navigate to project directory
cd Local-Multimodal-LLM-Ai-Chat
```

### Step 2: Open Project in Android Studio

1. Launch Android Studio
2. Select **File > Open**
3. Navigate to the cloned repository folder
4. Click **OK**
5. Wait for Gradle sync to complete

### Step 3: Sync Gradle

Android Studio will automatically sync Gradle. If not:

1. Click **File > Sync Project with Gradle Files**
2. Wait for the sync to complete
3. Resolve any dependency issues if prompted

### Step 4: Build the Project

**Using Android Studio:**
1. Click **Build > Make Project** (or press `Ctrl+F9` / `Cmd+F9`)
2. Wait for the build to complete
3. Check the **Build** window for any errors

**Using Command Line:**

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK (unsigned)
./gradlew assembleRelease

# Clean and build
./gradlew clean assembleDebug
```

The built APK will be located at:
```
app/build/outputs/apk/debug/app-debug.apk
```

## Installing on Device

### Method 1: Using Android Studio (Recommended)

1. **Enable Developer Options on Android Device**
   - Go to **Settings > About Phone**
   - Tap **Build Number** 7 times
   - Return to **Settings > System > Developer Options**
   - Enable **USB Debugging**

2. **Connect Device**
   - Connect your Android device via USB
   - Approve USB debugging prompt on device
   - Verify device appears in Android Studio device list

3. **Run the App**
   - Click the **Run** button (green triangle) in Android Studio
   - Select your device from the deployment target dialog
   - Wait for installation and launch

### Method 2: Using ADB (Android Debug Bridge)

```bash
# Ensure device is connected and USB debugging is enabled
adb devices

# Install the APK
adb install app/build/outputs/apk/debug/app-debug.apk

# Or install and replace existing version
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Launch the app
adb shell am start -n com.localllm.localaichatapp/.MainActivity
```

### Method 3: Direct APK Installation

1. Transfer the APK file to your Android device
2. Enable **Install from Unknown Sources** in device settings
3. Use a file manager to locate the APK
4. Tap the APK file to install
5. Follow the installation prompts

### Method 4: Using Android Emulator

1. **Create an Emulator**
   - In Android Studio, go to **Tools > Device Manager**
   - Click **Create Device**
   - Select a device definition (e.g., Pixel 6)
   - Select system image (API 35 recommended)
   - Click **Finish**

2. **Run on Emulator**
   - Start the emulator
   - Click **Run** in Android Studio
   - Select the emulator as deployment target

## Post-Installation Setup

### First Launch

1. **Grant Permissions**
   - The app will request storage permissions
   - Grant **Read Media Images**, **Read Media Audio**, and **Read Media Video** permissions
   - These are required for image and audio analysis features

2. **Download AI Models (Optional)**
   - Navigate to **Models** tab
   - Select a model to download (e.g., Gemma-2B)
   - Wait for download to complete
   - Note: The app works with mock AI implementation by default

3. **Explore Features**
   - Try the **AI Chat** feature for conversations
   - Use **Ask Image** to analyze images
   - Explore **Prompt Lab** for template-based interactions

## Verification

### Verify Installation

```bash
# Check if app is installed
adb shell pm list packages | grep com.localllm.localaichatapp

# Check app info
adb shell dumpsys package com.localllm.localaichatapp | grep version

# View app logs
adb logcat | grep LocalAiChatApp
```

### Test Basic Functionality

1. Open the app
2. Navigate to **AI Chat**
3. Send a test message
4. Verify response is received (mock implementation)
5. Check all navigation items work

## Troubleshooting

### Gradle Sync Fails

**Problem**: Gradle sync fails with dependency errors

**Solutions**:
```bash
# Clear Gradle cache
./gradlew clean --refresh-dependencies

# Or manually delete cache
rm -rf ~/.gradle/caches/

# Invalidate caches in Android Studio
# File > Invalidate Caches > Invalidate and Restart
```

### Build Errors

**Problem**: Compilation errors during build

**Solutions**:
1. Check JDK version: `java -version` (should be 21)
2. Ensure Android SDK is properly installed
3. Update Gradle: `./gradlew wrapper --gradle-version=8.6.1`
4. Clean and rebuild: `./gradlew clean build`

### Device Not Detected

**Problem**: Android device not showing in Android Studio

**Solutions**:
1. Enable **USB Debugging** on device
2. Install device-specific USB drivers (Windows)
3. Try different USB cable or port
4. Run `adb kill-server && adb start-server`
5. Check `adb devices` output

### Installation Failed

**Problem**: APK installation fails

**Solutions**:
```bash
# Uninstall existing version
adb uninstall com.localllm.localaichatapp

# Reinstall
adb install -r app-debug.apk

# Check logcat for errors
adb logcat | grep PackageManager
```

### App Crashes on Launch

**Problem**: App crashes immediately after launch

**Solutions**:
1. Check logcat for crash details: `adb logcat | grep AndroidRuntime`
2. Verify device meets minimum requirements (API 24+)
3. Clear app data: Settings > Apps > LocalAiChatApp > Clear Data
4. Reinstall the app

### Insufficient Storage

**Problem**: Not enough space for AI models

**Solutions**:
1. Free up device storage (at least 2 GB)
2. Use smaller models (Gemma-2B instead of Gemma-7B)
3. Delete unused models from app settings

## Additional Resources

- [Development Setup Guide](./DEVELOPMENT_SETUP.md)
- [User Guide](./USER_GUIDE.md)
- [Troubleshooting Guide](./TROUBLESHOOTING.md)
- [FAQ](./FAQ.md)
- [Android Studio Documentation](https://developer.android.com/studio)
- [Gradle Documentation](https://gradle.org/guides/)

## Getting Help

If you encounter issues not covered here:

1. Check the [Troubleshooting Guide](./TROUBLESHOOTING.md)
2. Search [existing issues](https://github.com/yourusername/Local-Multimodal-LLM-Ai-Chat/issues)
3. Create a [new issue](https://github.com/yourusername/Local-Multimodal-LLM-Ai-Chat/issues/new) with:
   - Your environment details (OS, Android Studio version, device model)
   - Error messages and logs
   - Steps to reproduce the problem

---

**Next Steps**: Once installed, check out the [User Guide](./USER_GUIDE.md) to learn how to use the app.
