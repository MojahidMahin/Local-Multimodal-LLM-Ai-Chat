# Troubleshooting Guide

Solutions to common problems and issues with LocalAiChatApp.

## Table of Contents
- [Installation Issues](#installation-issues)
- [Build Problems](#build-problems)
- [Runtime Errors](#runtime-errors)
- [Performance Issues](#performance-issues)
- [UI/UX Problems](#uiux-problems)
- [Model & Download Issues](#model--download-issues)
- [Database Issues](#database-issues)
- [Development Issues](#development-issues)

## Installation Issues

### APK Installation Failed

**Problem**: Cannot install APK on device

**Symptoms**:
```
Installation failed with message Failed to finalize session
```

**Solutions**:

1. **Enable Unknown Sources**
   ```
   Settings > Security > Unknown Sources > Enable
   ```

2. **Uninstall Previous Version**
   ```bash
   adb uninstall com.localllm.localaichatapp
   adb install app-debug.apk
   ```

3. **Check Storage Space**
   - Ensure at least 500 MB free space
   - Clear cache: Settings > Storage > Clear Cache

4. **Verify APK Integrity**
   ```bash
   # Check APK is not corrupted
   aapt dump badging app-debug.apk
   ```

### Device Not Detected

**Problem**: Android device not showing in Android Studio

**Symptoms**:
- No device in device dropdown
- `adb devices` shows empty list

**Solutions**:

1. **Enable USB Debugging**
   ```
   Settings > About Phone > Tap Build Number 7 times
   Settings > Developer Options > USB Debugging > Enable
   ```

2. **Install USB Drivers (Windows)**
   - Download device-specific USB drivers
   - Install and restart computer

3. **Restart ADB Server**
   ```bash
   adb kill-server
   adb start-server
   adb devices
   ```

4. **Try Different USB Cable/Port**
   - Use original USB cable
   - Try different USB port
   - Avoid USB hubs

5. **Revoke USB Debugging Authorizations**
   ```
   Settings > Developer Options > Revoke USB Debugging Authorizations
   # Reconnect device and approve prompt
   ```

### Permission Denied Errors

**Problem**: Cannot access storage, camera, or microphone

**Symptoms**:
- "Permission denied" errors
- Features not working

**Solutions**:

1. **Grant Permissions Manually**
   ```
   Settings > Apps > LocalAiChatApp > Permissions
   Enable: Storage, Camera, Microphone
   ```

2. **Reinstall App**
   - Uninstall completely
   - Reinstall and grant all permissions when prompted

3. **Check Android Version**
   - Android 11+: Different permission model
   - Grant "All files access" if needed

## Build Problems

### Gradle Sync Failed

**Problem**: Gradle sync fails when opening project

**Symptoms**:
```
Could not resolve all dependencies for configuration ':app:debugCompileClasspath'
```

**Solutions**:

1. **Clean and Rebuild**
   ```bash
   ./gradlew clean build --refresh-dependencies
   ```

2. **Check Internet Connection**
   - Gradle needs internet to download dependencies
   - Check proxy settings if behind firewall

3. **Update Gradle Wrapper**
   ```bash
   ./gradlew wrapper --gradle-version=8.6.1
   ```

4. **Invalidate Caches**
   - File > Invalidate Caches > Invalidate and Restart

5. **Check `local.properties`**
   ```properties
   # Ensure SDK path is correct
   sdk.dir=/path/to/Android/sdk
   ```

6. **Delete Gradle Cache**
   ```bash
   rm -rf ~/.gradle/caches/
   rm -rf .gradle/
   ./gradlew clean build
   ```

### Compilation Errors

**Problem**: Code doesn't compile

**Symptoms**:
```
Unresolved reference: Composable
Cannot access class 'kotlinx.coroutines.flow.Flow'
```

**Solutions**:

1. **Sync Gradle Files**
   - File > Sync Project with Gradle Files

2. **Check JDK Version**
   ```bash
   java -version
   # Should be JDK 21
   ```

   Set in Android Studio:
   ```
   File > Project Structure > SDK Location > Gradle Settings
   Gradle JDK: Use JDK 21
   ```

3. **Update Dependencies**
   - Check `gradle/libs.versions.toml`
   - Ensure versions are compatible

4. **Clean Build**
   ```bash
   ./gradlew clean
   ./gradlew assembleDebug
   ```

### Out of Memory During Build

**Problem**: Build fails with out of memory error

**Symptoms**:
```
OutOfMemoryError: Java heap space
```

**Solutions**:

1. **Increase Gradle Memory**

   Edit `gradle.properties`:
   ```properties
   org.gradle.jvmargs=-Xmx4096m -XX:MaxMetaspaceSize=512m
   ```

2. **Increase Android Studio Memory**

   Help > Edit Custom VM Options:
   ```
   -Xmx4096m
   -XX:ReservedCodeCacheSize=512m
   ```

3. **Enable Build Cache**

   `gradle.properties`:
   ```properties
   org.gradle.caching=true
   android.enableBuildCache=true
   ```

### R8/ProGuard Issues

**Problem**: Release build fails or crashes

**Symptoms**:
- Debug works, release crashes
- ClassNotFoundException in release build

**Solutions**:

1. **Check ProGuard Rules**

   `app/proguard-rules.pro`:
   ```proguard
   # Keep data classes
   -keepclassmembers class com.localllm.localaichatapp.domain.model.** {
       *;
   }

   # Keep Hilt generated classes
   -keep class dagger.hilt.** { *; }
   -keep class javax.inject.** { *; }
   ```

2. **Disable Minification Temporarily**

   `app/build.gradle.kts`:
   ```kotlin
   buildTypes {
       release {
           isMinifyEnabled = false
           isShrinkResources = false
       }
   }
   ```

3. **Check Logs**
   ```bash
   adb logcat | grep -E "ClassNotFoundException|NoSuchMethodException"
   ```

## Runtime Errors

### App Crashes on Launch

**Problem**: App crashes immediately after opening

**Symptoms**:
- App opens then closes
- "App has stopped" message

**Solutions**:

1. **Check Logcat**
   ```bash
   adb logcat | grep AndroidRuntime
   ```

2. **Clear App Data**
   ```
   Settings > Apps > LocalAiChatApp > Storage > Clear Data
   ```

3. **Check Database Migration**
   - If crash after update, database migration may have failed
   - Uninstall and reinstall (will lose data)

4. **Verify Device Compatibility**
   - Check Android version (must be 7.0+)
   - Check architecture (ARM64 required for some models)

5. **Check for Missing Dependencies**
   - Review logcat for "ClassNotFoundException"
   - Ensure all dependencies are included in build

### Crashes When Opening Feature

**Problem**: App crashes when accessing specific feature

**Solutions**:

1. **Check Permissions**
   - Image feature: Storage permission
   - Audio feature: Microphone + Storage
   - Camera: Camera permission

2. **Check Logcat for Stack Trace**
   ```bash
   adb logcat | grep -A 20 "FATAL EXCEPTION"
   ```

3. **Check File Paths**
   - Ensure media files are accessible
   - Check file format is supported

4. **Reinstall App**
   - Clean install may fix corrupted data

### Memory Issues

**Problem**: Out of memory errors during usage

**Symptoms**:
```
OutOfMemoryError: Failed to allocate
```

**Solutions**:

1. **Use Smaller Models**
   - Switch to Gemma-2B instead of Gemma-7B
   - Models > Select smaller model

2. **Close Background Apps**
   - Free up RAM
   - Restart device if necessary

3. **Reduce Max Response Length**
   - Settings > AI Settings > Max Response Length
   - Set lower value (e.g., 512 instead of 2048)

4. **Clear Cache**
   ```
   Settings > Apps > LocalAiChatApp > Storage > Clear Cache
   ```

5. **Device Limitations**
   - Older devices with < 4GB RAM may struggle
   - Consider device upgrade for better experience

## Performance Issues

### Slow AI Responses

**Problem**: AI takes too long to respond

**Solutions**:

1. **Use Faster Model**
   - Switch to smaller model (Gemma-2B)
   - Models > Select faster model

2. **Optimize Device**
   - Close background apps
   - Enable Performance mode
   - Keep device charged (battery saver slows CPU)

3. **Reduce Response Length**
   - Settings > Max Response Length
   - Lower value = faster generation

4. **Clear App Cache**
   ```
   Settings > Apps > LocalAiChatApp > Clear Cache
   ```

5. **Check Device Specs**
   - Older devices will be slower
   - 6GB+ RAM recommended for good performance

### High Battery Drain

**Problem**: App uses too much battery

**Solutions**:

1. **Use Smaller Models**
   - Larger models consume more power
   - Switch to Gemma-2B

2. **Limit Usage Sessions**
   - AI processing is CPU-intensive
   - Take breaks between sessions

3. **Monitor Performance Tab**
   - Check battery metrics
   - Identify heavy operations

4. **Close When Not in Use**
   - Don't leave app running in background
   - Close completely when done

### UI Lag and Stuttering

**Problem**: UI is slow or unresponsive

**Solutions**:

1. **Restart App**
   - Close and reopen
   - Or force stop: Settings > Apps > Force Stop

2. **Clear Cache**
   ```
   Settings > Apps > LocalAiChatApp > Clear Cache
   ```

3. **Reduce Animations**
   ```
   Developer Options > Window/Transition Animation Scale > 0.5x
   ```

4. **Free Up Storage**
   - Ensure at least 1 GB free space
   - Delete unused models

5. **Update Android System**
   - Keep Android OS updated
   - Install security patches

## UI/UX Problems

### UI Elements Not Displaying

**Problem**: Buttons, text, or images missing

**Solutions**:

1. **Restart App**
   - Force close and reopen

2. **Clear App Data**
   ```
   Settings > Apps > LocalAiChatApp > Clear Data
   ```

3. **Check Display Settings**
   - Font size: Settings > Display > Font Size
   - Display size: Settings > Display > Display Size

4. **Update App**
   - Install latest version
   - May contain UI fixes

### Dark Mode Not Working

**Problem**: App doesn't respect system dark mode

**Solutions**:

1. **Check App Settings**
   - Settings > Theme
   - Ensure set to "System" or "Dark"

2. **Check System Settings**
   - Settings > Display > Dark Mode
   - Enable system dark mode

3. **Restart App**
   - Changes may require restart

### Text Cut Off or Overlapping

**Problem**: Text doesn't fit properly

**Solutions**:

1. **Adjust Font Size**
   - Settings > Display > Font Size
   - Try smaller size

2. **Adjust Display Size**
   - Settings > Display > Display Size
   - Try different setting

3. **Report as Bug**
   - May be screen size specific
   - Include device model in report

## Model & Download Issues

### Model Download Fails

**Problem**: Cannot download AI models

**Symptoms**:
- Download gets stuck
- "Download failed" error

**Solutions**:

1. **Check Internet Connection**
   - Stable WiFi recommended
   - Avoid mobile data (large files)

2. **Check Storage Space**
   - Ensure 5+ GB free space
   - Models are large files

3. **Retry Download**
   - Cancel and restart download
   - Try different time (server load)

4. **Clear Download Cache**
   ```
   Settings > Apps > LocalAiChatApp > Clear Cache
   ```

5. **Check Firewall/VPN**
   - Disable VPN temporarily
   - Check firewall settings

### Model Won't Load

**Problem**: Model downloaded but won't initialize

**Solutions**:

1. **Check File Integrity**
   - Re-download model
   - May have corrupted during download

2. **Check Storage Space**
   - Need space for model + runtime
   - Free up additional space

3. **Restart App**
   - Force close and reopen
   - Retry model initialization

4. **Check Logcat**
   ```bash
   adb logcat | grep -i "model\|inference"
   ```

### Cannot Delete Model

**Problem**: Model deletion fails

**Solutions**:

1. **Close Active Sessions**
   - Cannot delete model in use
   - Exit all chats using that model

2. **Restart App**
   - Force close and reopen
   - Try deletion again

3. **Manual Deletion**
   ```bash
   # Via ADB
   adb shell
   cd /data/data/com.localllm.localaichatapp/files/models
   rm -rf model-name/
   ```

## Database Issues

### Database Corruption

**Problem**: App crashes with database errors

**Symptoms**:
```
android.database.sqlite.SQLiteDatabaseCorruptException
```

**Solutions**:

1. **Export Data (if possible)**
   - Settings > Privacy > Export Data
   - Before clearing database

2. **Clear App Data**
   ```
   Settings > Apps > LocalAiChatApp > Storage > Clear Data
   ```
   **Warning**: This deletes all conversations

3. **Reinstall App**
   - Uninstall completely
   - Reinstall fresh version

### Migration Failed

**Problem**: App crashes after update

**Symptoms**:
- Worked before update
- Crashes on launch after update

**Solutions**:

1. **Check Logcat**
   ```bash
   adb logcat | grep -i "migration\|database"
   ```

2. **Clear Database**
   ```
   Settings > Apps > LocalAiChatApp > Clear Data
   ```

3. **Report Issue**
   - Include app version before/after update
   - Include migration error logs

### Missing Conversations

**Problem**: Conversations disappeared

**Solutions**:

1. **Check Search**
   - Use search function
   - May be in different category

2. **Check Database**
   - Database Inspector in Android Studio
   - Verify data exists

3. **No Auto-Backup**
   - App doesn't have cloud backup
   - Lost data cannot be recovered
   - Regularly export important conversations

## Development Issues

### Android Studio Issues

**Problem**: IDE not working properly

**Solutions**:

1. **Invalidate Caches**
   - File > Invalidate Caches > Invalidate and Restart

2. **Update Android Studio**
   - Help > Check for Updates

3. **Reinstall Android Studio**
   - Last resort
   - Backup project first

### Emulator Problems

**Problem**: Emulator won't start or crashes

**Solutions**:

1. **Check Virtualization**
   ```bash
   # Linux
   kvm-ok

   # Windows - Enable Hyper-V or HAXM
   ```

2. **Increase Emulator RAM**
   - AVD Manager > Edit AVD
   - Increase RAM to 4GB+

3. **Cold Boot Emulator**
   - AVD Manager > Down arrow > Cold Boot Now

4. **Create New AVD**
   - Delete corrupted AVD
   - Create fresh emulator

### Git Issues

**Problem**: Cannot push/pull from repository

**Solutions**:

1. **Check Remote**
   ```bash
   git remote -v
   ```

2. **Update Upstream**
   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

3. **Resolve Conflicts**
   ```bash
   git status
   # Manually resolve conflicts
   git add .
   git rebase --continue
   ```

## Getting Further Help

If your issue isn't resolved:

### 1. Check Documentation
- [User Guide](./USER_GUIDE.md)
- [FAQ](./FAQ.md)
- [Installation Guide](./INSTALLATION.md)

### 2. Search Existing Issues
- [GitHub Issues](https://github.com/yourusername/Local-Multimodal-LLM-Ai-Chat/issues)
- Someone may have encountered same problem

### 3. Create New Issue
- Use bug report template
- Include:
  - Device model and Android version
  - App version
  - Steps to reproduce
  - Logcat output
  - Screenshots

### 4. Provide Detailed Information

**Essential Information**:
```bash
# Get device info
adb shell getprop ro.build.version.release  # Android version
adb shell getprop ro.product.model           # Device model

# Get app version
adb shell dumpsys package com.localllm.localaichatapp | grep versionName

# Capture logs
adb logcat -d > logcat.txt
```

---

**Still need help?** Create an issue on [GitHub](https://github.com/yourusername/Local-Multimodal-LLM-Ai-Chat/issues) with detailed information about your problem.
