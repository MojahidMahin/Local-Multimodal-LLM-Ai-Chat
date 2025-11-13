# Development Setup Guide

This guide will help you set up your development environment for contributing to LocalAiChatApp.

## Table of Contents
- [Prerequisites](#prerequisites)
- [Development Environment](#development-environment)
- [Project Setup](#project-setup)
- [Build Configuration](#build-configuration)
- [Running the App](#running-the-app)
- [Debugging](#debugging)
- [Development Tools](#development-tools)
- [Common Development Tasks](#common-development-tasks)

## Prerequisites

### Required Software

1. **Java Development Kit (JDK) 21**
   ```bash
   # Verify installation
   java -version
   # Should show: openjdk version "21.x.x"
   ```

2. **Android Studio Hedgehog | 2023.1.1 or later**
   - Download from [developer.android.com/studio](https://developer.android.com/studio)
   - Includes Android SDK and tools

3. **Git**
   ```bash
   git --version
   # Should show: git version 2.x.x
   ```

4. **Gradle 8.6.1 or later** (included with project)

### Recommended Software

- **Android Emulator** with system images for API 24, 28, 31, 35
- **Git GUI** (optional): GitKraken, SourceTree, or GitHub Desktop
- **Code Editor** (optional): VS Code for viewing markdown docs

### System Requirements

- **RAM**: 16 GB minimum (32 GB recommended for emulator + IDE)
- **Storage**: 20 GB free space for Android SDK, project, and models
- **CPU**: Modern multi-core processor (Intel i5/Ryzen 5 or better)
- **OS**: Windows 10+, macOS 10.14+, or Linux (Ubuntu 18.04+)

## Development Environment

### Step 1: Install and Configure Android Studio

1. **Download and Install**
   ```bash
   # Linux (Ubuntu/Debian)
   sudo snap install android-studio --classic

   # macOS (Homebrew)
   brew install --cask android-studio

   # Windows: Download installer from website
   ```

2. **Initial Configuration**
   - Launch Android Studio
   - Complete setup wizard
   - Choose "Standard" installation
   - Select UI theme

3. **Install SDK Components**
   - Open **Tools > SDK Manager**
   - **SDK Platforms** tab:
     - ✓ Android 14.0 (API 35)
     - ✓ Android 12.0 (API 31)
     - ✓ Android 9.0 (API 28)
     - ✓ Android 7.0 (API 24)

   - **SDK Tools** tab:
     - ✓ Android SDK Build-Tools 35.0.0
     - ✓ Android SDK Platform-Tools
     - ✓ Android Emulator
     - ✓ Google Play services
     - ✓ Intel/AMD x86 Emulator Accelerator (HAXM)
     - ✓ Android SDK Command-line Tools

### Step 2: Configure IDE Settings

1. **Increase Memory Allocation**
   - **Help > Edit Custom VM Options**
   - Add or modify:
     ```
     -Xmx4096m
     -XX:ReservedCodeCacheSize=512m
     ```

2. **Enable Auto-Import**
   - **File > Settings > Editor > General > Auto Import**
   - ✓ Add unambiguous imports on the fly
   - ✓ Optimize imports on the fly

3. **Configure Code Style**
   - **File > Settings > Editor > Code Style > Kotlin**
   - Scheme: **Kotlin Style Guide**
   - Import from: `config/codestyle.xml` (if available)

4. **Enable Useful Plugins**
   - **File > Settings > Plugins**
   - Recommended:
     - Kotlin
     - Android
     - Compose
     - Git
     - Markdown

### Step 3: Setup Android Emulators

1. **Create Emulator for Development**
   - **Tools > Device Manager > Create Device**
   - Select: **Pixel 6** (or similar)
   - System Image: **API 35** (Android 14)
   - Configure:
     - RAM: 4 GB
     - Storage: 8 GB
     - Graphics: Hardware

2. **Create Emulator for Testing**
   - Additional emulator with **API 24** (minimum supported)
   - Test compatibility

3. **Emulator Settings**
   ```bash
   # Increase emulator RAM and heap
   # Edit ~/.android/avd/[emulator_name].avd/config.ini
   hw.ramSize=4096
   vm.heapSize=512
   ```

## Project Setup

### Step 1: Fork and Clone Repository

```bash
# Fork the repository on GitHub first, then:

# Clone your fork
git clone https://github.com/YOUR_USERNAME/Local-Multimodal-LLM-Ai-Chat.git
cd Local-Multimodal-LLM-Ai-Chat

# Add upstream remote
git remote add upstream https://github.com/ORIGINAL_OWNER/Local-Multimodal-LLM-Ai-Chat.git

# Verify remotes
git remote -v
# Should show:
# origin    https://github.com/YOUR_USERNAME/Local-Multimodal-LLM-Ai-Chat.git (fetch)
# origin    https://github.com/YOUR_USERNAME/Local-Multimodal-LLM-Ai-Chat.git (push)
# upstream  https://github.com/ORIGINAL_OWNER/Local-Multimodal-LLM-Ai-Chat.git (fetch)
# upstream  https://github.com/ORIGINAL_OWNER/Local-Multimodal-LLM-Ai-Chat.git (push)
```

### Step 2: Open Project in Android Studio

1. Launch Android Studio
2. **File > Open**
3. Navigate to cloned repository
4. Click **OK**
5. Wait for Gradle sync (may take 5-10 minutes first time)

### Step 3: Verify Project Structure

```
Local-Multimodal-LLM-Ai-Chat/
├── app/                          # Main application module
│   ├── build.gradle.kts         # App-level build config
│   └── src/
│       ├── main/                # Production code
│       ├── test/                # Unit tests
│       └── androidTest/         # Instrumented tests
├── gradle/
│   ├── libs.versions.toml       # Dependency version catalog
│   └── wrapper/
├── build.gradle.kts             # Root build config
├── settings.gradle.kts
├── gradle.properties
└── docs/                        # Documentation
```

### Step 4: Sync Dependencies

```bash
# From command line
./gradlew clean build

# Or in Android Studio
# File > Sync Project with Gradle Files
```

## Build Configuration

### Build Variants

The project has two build types:

1. **Debug**
   - Debuggable
   - Logs enabled
   - No code shrinking
   - Fast build times

2. **Release**
   - Optimized
   - ProGuard/R8 enabled
   - Code shrinking and obfuscation
   - Slower build, smaller APK

### Build Configuration Files

**`build.gradle.kts` (root)**:
```kotlin
// Root build file - plugin versions and repositories
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
}
```

**`app/build.gradle.kts`**:
```kotlin
android {
    namespace = "com.localllm.localaichatapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.localllm.localaichatapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
}
```

**`gradle/libs.versions.toml`**:
```toml
# Centralized dependency management
[versions]
kotlin = "2.0.20"
androidGradlePlugin = "8.6.1"
composeBom = "2024.09.03"
hilt = "2.52"
room = "2.6.1"
```

### Local Configuration

Create `local.properties` (git-ignored):
```properties
# Android SDK location
sdk.dir=/Users/yourname/Library/Android/sdk

# Optional: Signing configs for release builds
RELEASE_STORE_FILE=/path/to/keystore.jks
RELEASE_STORE_PASSWORD=your_password
RELEASE_KEY_ALIAS=your_alias
RELEASE_KEY_PASSWORD=your_key_password
```

## Running the App

### Using Android Studio

1. **Select Build Variant**
   - **Build > Select Build Variant**
   - Choose: `debug` or `release`

2. **Select Device/Emulator**
   - Click device dropdown in toolbar
   - Select physical device or emulator

3. **Run**
   - Click **Run** button (green triangle)
   - Or press `Shift+F10` (Windows/Linux) / `Ctrl+R` (macOS)

### Using Gradle

```bash
# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Build and install
./gradlew installDebug

# Run on specific device
./gradlew installDebug -Pandroid.serial=DEVICE_SERIAL

# Build release APK
./gradlew assembleRelease
```

### Using ADB

```bash
# List connected devices
adb devices

# Install APK
adb install app/build/outputs/apk/debug/app-debug.apk

# Uninstall and reinstall
adb uninstall com.localllm.localaichatapp
adb install app/build/outputs/apk/debug/app-debug.apk

# Launch app
adb shell am start -n com.localllm.localaichatapp/.MainActivity
```

## Debugging

### Logcat

**In Android Studio**:
- **View > Tool Windows > Logcat**
- Filter by package: `com.localllm.localaichatapp`
- Use log levels: Verbose, Debug, Info, Warn, Error

**Command Line**:
```bash
# View all logs
adb logcat

# Filter by package
adb logcat | grep LocalAiChatApp

# Filter by tag
adb logcat -s "ChatViewModel"

# Clear logs
adb logcat -c
```

### Debugging in Code

```kotlin
import android.util.Log

// Use consistent tags
private val TAG = "ChatViewModel"

// Log levels
Log.v(TAG, "Verbose message")  // Detailed
Log.d(TAG, "Debug message")    // Development
Log.i(TAG, "Info message")     // General info
Log.w(TAG, "Warning message")  // Warnings
Log.e(TAG, "Error message", exception)  // Errors
```

### Breakpoint Debugging

1. **Set Breakpoint**
   - Click left gutter next to line number
   - Or press `Ctrl+F8` (Windows/Linux) / `Cmd+F8` (macOS)

2. **Debug App**
   - Click **Debug** button (bug icon)
   - Or press `Shift+F9` (Windows/Linux) / `Ctrl+D` (macOS)

3. **Debug Controls**
   - **F8**: Step over
   - **F7**: Step into
   - **Shift+F8**: Step out
   - **F9**: Resume program

### Layout Inspector

1. **Open Layout Inspector**
   - **Tools > Layout Inspector**
   - Select running device/emulator

2. **Inspect UI**
   - View hierarchy
   - Check component properties
   - Measure dimensions

### Network Debugging

```bash
# Monitor network traffic
adb shell tcpdump -i any -s 0 -w - | wireshark -k -i -

# Or use Charles Proxy / Proxyman
```

## Development Tools

### Useful Gradle Commands

```bash
# Clean build
./gradlew clean

# Build project
./gradlew build

# Run tests
./gradlew test

# Run lint checks
./gradlew lint

# Check dependencies
./gradlew dependencies

# Refresh dependencies
./gradlew build --refresh-dependencies

# See all tasks
./gradlew tasks
```

### Code Quality Tools

**Lint**:
```bash
# Run Android Lint
./gradlew lint

# View report
open app/build/reports/lint-results-debug.html
```

**Detekt** (if configured):
```bash
# Run Detekt for code analysis
./gradlew detekt
```

### Database Inspection

1. **Device File Explorer**
   - **View > Tool Windows > Device File Explorer**
   - Navigate to: `/data/data/com.localllm.localaichatapp/databases/`
   - Download `chat_database`

2. **Database Inspector**
   - **View > Tool Windows > App Inspection**
   - Select **Database Inspector**
   - View tables, run queries

### Performance Profiling

1. **CPU Profiler**
   - **View > Tool Windows > Profiler**
   - Select CPU

2. **Memory Profiler**
   - Track memory allocations
   - Detect memory leaks

3. **Network Profiler**
   - Monitor network requests

## Common Development Tasks

### Creating New Feature

```bash
# Create feature branch
git checkout -b feature/my-new-feature

# Make changes...

# Stage changes
git add .

# Commit
git commit -m "feat: add new feature"

# Push to your fork
git push origin feature/my-new-feature
```

### Adding Dependencies

1. **Edit `gradle/libs.versions.toml`**:
   ```toml
   [versions]
   newLib = "1.0.0"

   [libraries]
   newLib = { module = "com.example:library", version.ref = "newLib" }
   ```

2. **Update `app/build.gradle.kts`**:
   ```kotlin
   dependencies {
       implementation(libs.newLib)
   }
   ```

3. **Sync Gradle**

### Database Migrations

When modifying database schema:

1. **Update Entity**:
   ```kotlin
   @Entity(tableName = "table_name")
   data class MyEntity(
       // Add new field
       val newField: String = ""
   )
   ```

2. **Increment Version**:
   ```kotlin
   @Database(
       entities = [...],
       version = 4,  // Increment
       exportSchema = true
   )
   ```

3. **Create Migration**:
   ```kotlin
   val MIGRATION_3_4 = object : Migration(3, 4) {
       override fun migrate(database: SupportSQLiteDatabase) {
           database.execSQL("ALTER TABLE table_name ADD COLUMN newField TEXT NOT NULL DEFAULT ''")
       }
   }
   ```

### Running Tests

```bash
# Unit tests
./gradlew test

# Specific test class
./gradlew test --tests ChatRepositoryTest

# Integration tests
./gradlew connectedAndroidTest

# With coverage
./gradlew jacocoTestReport
```

### Code Formatting

```bash
# Format Kotlin code (if ktlint configured)
./gradlew ktlintFormat

# Or use Android Studio
# Code > Reformat Code (Ctrl+Alt+L)
```

### Updating from Upstream

```bash
# Fetch latest changes
git fetch upstream

# Merge into your branch
git checkout main
git merge upstream/main

# Or rebase your feature branch
git checkout feature/my-feature
git rebase upstream/main
```

## Troubleshooting

### Gradle Sync Issues

```bash
# Invalidate caches and restart
# File > Invalidate Caches > Invalidate and Restart

# Or command line
./gradlew clean --refresh-dependencies
rm -rf ~/.gradle/caches/
```

### Build Failures

```bash
# Clean and rebuild
./gradlew clean build

# Check JDK version
echo $JAVA_HOME
java -version
```

### Emulator Issues

```bash
# Reset emulator
adb -s emulator-5554 emu kill
# Restart emulator

# Clear emulator data
# Cold Boot from AVD Manager
```

## Next Steps

- Read [Contributing Guide](./CONTRIBUTING.md)
- Check [Code Style Guide](./CODE_STYLE.md)
- Review [Testing Guide](./TESTING.md)
- Explore [Architecture Documentation](./ARCHITECTURE.md)

---

**Need Help?** Check [Troubleshooting](./TROUBLESHOOTING.md) or create an issue on GitHub.
