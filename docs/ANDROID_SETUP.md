# Android App Setup Guide

Complete guide for installing, setting up, and running LocalAI Chat on Android devices.

## Table of Contents
- [System Requirements](#system-requirements)
- [Android Version Compatibility](#android-version-compatibility)
- [Quick Start](#quick-start)
- [Installation Methods](#installation-methods)
- [First Launch Setup](#first-launch-setup)
- [Building from Source](#building-from-source)
- [Device Compatibility](#device-compatibility)
- [Permissions](#permissions)
- [Configuration](#configuration)
- [Storage Requirements](#storage-requirements)
- [Performance Optimization](#performance-optimization)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)

## System Requirements

### Minimum Requirements

**Android Version:**
- **Minimum**: Android 7.0 Nougat (API Level 24)
- **Target**: Android 14 (API Level 35)
- **Recommended**: Android 12+ for best experience

**Hardware:**
- **CPU**: ARMv7 or ARM64 (arm64-v8a recommended)
- **RAM**: 4 GB minimum (6 GB+ recommended)
- **Storage**: 2 GB free space minimum
  - Base app: ~100 MB
  - AI models: 1-5 GB depending on model
  - User data: 100 MB - 1 GB
- **Display**: Any resolution (optimized for 1080p+)

### Recommended Specifications

For optimal performance:
- **Android Version**: Android 12 or higher
- **RAM**: 8 GB or more
- **Storage**: 10 GB+ free space
- **Processor**: Snapdragon 888+ or equivalent
- **GPU**: Adreno 660+ or equivalent (for AI acceleration)

### Supported Architectures

✅ **arm64-v8a** (64-bit ARM) - Recommended
✅ **armeabi-v7a** (32-bit ARM) - Supported
❌ **x86** - Not supported (most Android devices use ARM)
❌ **x86_64** - Not supported

## Android Version Compatibility

### Android 7.0 Nougat (API 24) - Minimum
**Release**: August 2016
**Support Status**: ✅ Fully supported

**Features Available:**
- All core features
- Basic AI chat
- Image and audio processing
- Model management
- Automation system

**Limitations:**
- Notification channels use legacy API
- Some Material 3 components fallback to Material 2
- Performance may be slower on older devices

**Devices:**
- Samsung Galaxy S7/S7 Edge
- Google Pixel/Pixel XL
- OnePlus 3/3T
- And newer

### Android 8.0-8.1 Oreo (API 26-27)
**Release**: August 2017
**Support Status**: ✅ Fully supported

**Improvements:**
- Better notification management
- Background execution limits
- Enhanced battery optimization
- Picture-in-Picture support

**Devices:**
- Samsung Galaxy S8/S8+/Note 8
- Google Pixel 2/2 XL
- OnePlus 5/5T

### Android 9.0 Pie (API 28)
**Release**: August 2018
**Support Status**: ✅ Fully supported

**Improvements:**
- Adaptive battery and brightness
- Better gesture navigation
- Neural Networks API support (faster AI)
- Enhanced notifications

**Devices:**
- Samsung Galaxy S9/S9+/Note 9
- Google Pixel 3/3 XL
- OnePlus 6/6T

### Android 10 (API 29)
**Release**: September 2019
**Support Status**: ✅ Fully supported

**Improvements:**
- Dark theme support
- Scoped storage (better privacy)
- Gesture navigation by default
- Enhanced location controls

**Devices:**
- Samsung Galaxy S10 series/Note 10
- Google Pixel 4/4 XL
- OnePlus 7/7 Pro/7T

### Android 11 (API 30)
**Release**: September 2020
**Support Status**: ✅ Fully supported

**Improvements:**
- Conversation notifications
- One-time permissions
- Better media controls
- Neural Networks API 1.3

**Devices:**
- Samsung Galaxy S20 series/Note 20
- Google Pixel 5/4a
- OnePlus 8/8 Pro

### Android 12 (API 31)
**Release**: October 2021
**Support Status**: ✅ Fully supported + Recommended

**Improvements:**
- Material You design
- Dynamic color theming
- Privacy dashboard
- Improved performance
- Better NNAPI support

**Devices:**
- Samsung Galaxy S21 series
- Google Pixel 6/6 Pro
- OnePlus 9/9 Pro

### Android 13 (API 33)
**Release**: August 2022
**Support Status**: ✅ Fully supported + Recommended

**Improvements:**
- Per-app language preferences
- Themed app icons
- Better media permissions
- Enhanced privacy controls

**Devices:**
- Samsung Galaxy S22 series/S23 series
- Google Pixel 7/7 Pro
- OnePlus 10/11

### Android 14 (API 35) - Target
**Release**: October 2023
**Support Status**: ✅ Fully supported + Target version

**Improvements:**
- Enhanced performance
- Better battery optimization
- Improved security
- Advanced AI capabilities

**Devices:**
- Samsung Galaxy S24 series
- Google Pixel 8/8 Pro
- Latest flagship devices

## Quick Start

### For End Users (Pre-built APK)

**Option 1: Google Play Store** (When available)
1. Open Google Play Store
2. Search "LocalAI Chat"
3. Tap "Install"
4. Open and grant permissions
5. Start using!

**Option 2: Direct APK Installation**
```
1. Download APK from GitHub Releases
2. Settings → Security → Enable "Install from unknown sources"
3. Open downloaded APK file
4. Tap "Install"
5. Grant requested permissions
6. Launch app
```

### For Developers

See [Building from Source](#building-from-source) section below.

## Installation Methods

### Method 1: Google Play Store (Recommended)

**When available:**
1. Open Play Store app
2. Search for "LocalAI Chat"
3. Tap "Install" button
4. Wait for download and installation
5. Tap "Open" or find app icon

**Advantages:**
- ✅ Automatic updates
- ✅ Verified and signed
- ✅ Easy installation
- ✅ No special permissions needed

### Method 2: Direct APK Download

**Steps:**

1. **Enable Unknown Sources**
   ```
   Android 7-11:
   Settings → Security → Unknown Sources → Enable

   Android 12+:
   Settings → Apps → Special app access → Install unknown apps
   → Choose browser → Allow from this source
   ```

2. **Download APK**
   - Visit GitHub Releases page
   - Download latest `app-release.apk`
   - Or build from source (see below)

3. **Install APK**
   - Open Downloads folder
   - Tap on APK file
   - Tap "Install"
   - Wait for installation
   - Tap "Open"

4. **Verify Installation**
   ```
   Settings → Apps → LocalAI Chat
   Check version and permissions
   ```

**Advantages:**
- ✅ No Play Store required
- ✅ Works on any Android device
- ✅ Full control over versions

**Disadvantages:**
- ⚠️ Manual updates
- ⚠️ Need to enable unknown sources
- ⚠️ Security responsibility on user

### Method 3: ADB Installation (Advanced)

**Prerequisites:**
- ADB installed on computer
- USB debugging enabled on device
- USB cable

**Steps:**

1. **Enable Developer Options**
   ```
   Settings → About Phone → Tap "Build Number" 7 times
   ```

2. **Enable USB Debugging**
   ```
   Settings → Developer Options → USB Debugging → Enable
   ```

3. **Connect Device**
   ```bash
   # Connect via USB cable
   # Accept USB debugging prompt on device

   # Verify connection
   adb devices
   ```

4. **Install APK**
   ```bash
   # Install
   adb install app-release.apk

   # Or install with replacement
   adb install -r app-release.apk

   # Install to specific device
   adb -s <device_serial> install app-release.apk
   ```

5. **Launch App**
   ```bash
   adb shell am start -n com.localllm.localaichatapp/.MainActivity
   ```

**Verification:**
```bash
# Check if installed
adb shell pm list packages | grep localllm

# Get app info
adb shell dumpsys package com.localllm.localaichatapp

# View logs
adb logcat | grep LocalAiChatApp
```

### Method 4: F-Droid (Future)

When available on F-Droid:
1. Install F-Droid app
2. Add repository if needed
3. Search "LocalAI Chat"
4. Install

## First Launch Setup

### Step 1: Grant Permissions

On first launch, the app will request permissions:

**Required Permissions:**
- ✅ **Storage** - Read media files (images, audio)
  - Tap "Allow" or "While using the app"

**Optional Permissions:**
- **Camera** - Take photos for image analysis
  - Recommended: Allow
  - Can deny and enable later
- **Microphone** - Record audio for analysis
  - Recommended: Allow
  - Can deny and enable later
- **Notifications** - Automation alerts
  - Recommended: Allow
  - Can disable in settings

**Permission Management:**
```
Settings → Apps → LocalAI Chat → Permissions
```

### Step 2: Welcome Screen

1. **Choose Language** (if supported)
   - Select your preferred language
   - Default: System language

2. **Select Theme**
   - Light mode
   - Dark mode
   - System default (recommended)

3. **Review Privacy Policy**
   - No data collection
   - Everything stored locally
   - No internet connection required

### Step 3: Download AI Model (Optional)

**Option A: Use Mock Model** (for testing)
- Skip download
- Uses simulated AI responses
- No storage required
- Good for trying features

**Option B: Download Real Model**

1. Tap "Download Model"
2. Choose model:
   - **Gemma-2B** (~1.5 GB) - Fast, recommended
   - **Gemma-7B** (~5 GB) - More capable
   - **Phi-3-Mini** (~2 GB) - Balanced

3. Ensure stable WiFi connection
4. Wait for download (5-30 minutes)
5. Model will auto-initialize

**Storage Check:**
```
Settings → Storage
Verify available space before download
```

### Step 4: Explore Features

**Home Screen Tour:**
1. **AI Chat** - Try a conversation
2. **Ask Image** - Upload a test image
3. **Ask Audio** - Record a test audio
4. **Prompt Lab** - Try a template

**Settings Configuration:**
```
Settings → Preferences
- Set default model
- Configure temperature
- Enable/disable features
- Set up automation (optional)
```

## Building from Source

### Prerequisites

**Required Software:**

1. **Android Studio** (Latest stable version)
   - Download: https://developer.android.com/studio
   - Version: Hedgehog (2023.1.1) or later

2. **JDK 21**
   ```bash
   # Check version
   java -version
   # Should show: openjdk version "21.x.x"

   # Install if needed
   # Ubuntu/Debian:
   sudo apt install openjdk-21-jdk

   # macOS:
   brew install openjdk@21

   # Windows:
   # Download from Oracle or AdoptOpenJDK
   ```

3. **Git**
   ```bash
   git --version
   # Should show: git version 2.x.x
   ```

4. **Android SDK**
   - API Level 24 (Android 7.0) - Minimum
   - API Level 35 (Android 14) - Target
   - Build Tools 35.0.0
   - Platform Tools
   - SDK Tools

### Clone Repository

```bash
# Clone from GitHub
git clone https://github.com/yourusername/Local-Multimodal-LLM-Ai-Chat.git

# Navigate to project
cd Local-Multimodal-LLM-Ai-Chat

# Checkout desired branch
git checkout main
# or
git checkout develop
```

### Open in Android Studio

1. **Launch Android Studio**

2. **Open Project**
   - File → Open
   - Navigate to cloned directory
   - Select root folder
   - Click OK

3. **Wait for Gradle Sync**
   - Android Studio will sync automatically
   - May take 5-10 minutes on first run
   - Download dependencies

4. **Resolve Dependencies** (if needed)
   - Tools → SDK Manager
   - Install missing SDK components
   - Accept licenses

### Build Configurations

**Build Variants:**

```
Build → Select Build Variant
```

Available variants:
- **debug** - Development build with debugging
- **release** - Production build (requires signing)

### Build Debug APK

**Method 1: Android Studio**
```
1. Build → Build Bundle(s) / APK(s) → Build APK(s)
2. Wait for build to complete
3. Click "locate" in notification
4. APK location: app/build/outputs/apk/debug/
```

**Method 2: Command Line**
```bash
# Navigate to project root
cd Local-Multimodal-LLM-Ai-Chat

# Build debug APK
./gradlew assembleDebug

# Output: app/build/outputs/apk/debug/app-debug.apk
```

**Build Time:**
- First build: 5-15 minutes
- Incremental builds: 1-3 minutes

### Build Release APK

**1. Generate Keystore** (first time only)

```bash
keytool -genkey -v -keystore localai-release.keystore \
  -alias localai -keyalg RSA -keysize 2048 -validity 10000

# Follow prompts:
# - Enter keystore password
# - Enter key password
# - Enter your name, organization, etc.
```

**2. Configure Signing**

Create `keystore.properties` in project root:
```properties
storeFile=/path/to/localai-release.keystore
storePassword=your_keystore_password
keyAlias=localai
keyPassword=your_key_password
```

Add to `.gitignore`:
```
keystore.properties
*.keystore
```

**3. Update `app/build.gradle.kts`**

Already configured in project:
```kotlin
android {
    signingConfigs {
        create("release") {
            // Loads from keystore.properties
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            proguardFiles(...)
        }
    }
}
```

**4. Build Release APK**

```bash
# Command line
./gradlew assembleRelease

# Output: app/build/outputs/apk/release/app-release.apk

# Or in Android Studio
Build → Generate Signed Bundle / APK → APK
→ Select keystore → Enter passwords → Build
```

### Build AAB (Android App Bundle)

For Google Play Store submission:

```bash
# Build bundle
./gradlew bundleRelease

# Output: app/build/outputs/bundle/release/app-release.aab

# Or in Android Studio
Build → Generate Signed Bundle / APK → Android App Bundle
→ Select keystore → Enter passwords → Build
```

### Install Built APK

**Method 1: Android Studio**
```
1. Connect device via USB
2. Enable USB debugging on device
3. Run → Run 'app' (Shift+F10)
4. Select device from list
```

**Method 2: ADB**
```bash
# Ensure device connected
adb devices

# Install debug build
adb install app/build/outputs/apk/debug/app-debug.apk

# Install release build
adb install app/build/outputs/apk/release/app-release.apk

# Reinstall (replace existing)
adb install -r app-debug.apk
```

**Method 3: Transfer and Install**
```
1. Copy APK to device storage
2. Open file manager on device
3. Navigate to copied APK
4. Tap to install
```

## Device Compatibility

### Tested Devices

**Samsung:**
- ✅ Galaxy S24 series (Android 14)
- ✅ Galaxy S23 series (Android 13/14)
- ✅ Galaxy S22 series (Android 12/13)
- ✅ Galaxy S21 series (Android 11/12/13)
- ✅ Galaxy S20 series (Android 10/11/12)
- ✅ Galaxy Note 20 series (Android 10/11/12)
- ✅ Galaxy A series (varies)

**Google Pixel:**
- ✅ Pixel 8/8 Pro (Android 14)
- ✅ Pixel 7/7 Pro (Android 13/14)
- ✅ Pixel 6/6 Pro (Android 12/13/14)
- ✅ Pixel 5/4a (Android 11/12/13)
- ✅ Pixel 4/4 XL (Android 10/11/12/13)
- ✅ Pixel 3/3 XL (Android 9/10/11/12)

**OnePlus:**
- ✅ OnePlus 11/10/9 series (Android 13/14)
- ✅ OnePlus 8/7 series (Android 11/12/13)
- ✅ OnePlus 6/5 series (Android 10/11)

**Xiaomi:**
- ✅ Xiaomi 13/12 series (MIUI 14/15)
- ✅ Redmi Note series (MIUI 13+)
- ✅ Poco F series (MIUI 13+)

**Other Brands:**
- ✅ Realme (Android 11+)
- ✅ Oppo (ColorOS 12+)
- ✅ Vivo (OriginOS/FuntouchOS 12+)
- ✅ Motorola (Android 11+)
- ✅ Nokia (Android One, 11+)

### Performance by Device Tier

**Flagship (Best Performance)**
- Snapdragon 888+ or equivalent
- 8GB+ RAM
- Android 12+
- Expected: Instant responses, smooth UI

**Mid-Range (Good Performance)**
- Snapdragon 778G or equivalent
- 6GB RAM
- Android 11+
- Expected: Fast responses, occasional lag with large models

**Budget (Acceptable Performance)**
- Snapdragon 695 or equivalent
- 4GB RAM
- Android 10+
- Expected: Slower responses, use smaller models

**Entry-Level (Limited)**
- Snapdragon 400 series
- 3-4GB RAM
- Android 9+
- Expected: Very slow, use mock mode or smallest models only

### Form Factors

**Phones:**
- ✅ Standard smartphones (all sizes)
- ✅ Compact phones (Android 12+ recommended)
- ✅ Large phones (phablets)

**Tablets:**
- ✅ 7-8 inch tablets
- ✅ 10+ inch tablets
- ✅ Foldable displays (responsive UI adapts)

**Foldables:**
- ✅ Samsung Galaxy Z Fold series
- ✅ Samsung Galaxy Z Flip series
- ✅ Other foldables (responsive layout)

**ChromeOS:**
- ⚠️ Experimental support
- Works on Chromebooks with Android app support
- May have layout quirks

## Permissions

### Permission Details

#### 1. Read Media Images (Android 13+)
**Permission:** `READ_MEDIA_IMAGES`
**Purpose:** Access images for "Ask Image" feature
**When Requested:** First use of image feature
**Can Deny:** Yes, but image feature won't work
**Runtime:** Dynamic (requested when needed)

**What it allows:**
- Select images from gallery
- Analyze uploaded images with AI
- View image thumbnails

**What it doesn't allow:**
- Access to camera (separate permission)
- Modification of images
- Access to other file types

#### 2. Read Media Audio (Android 13+)
**Permission:** `READ_MEDIA_AUDIO`
**Purpose:** Access audio files for "Ask Audio" feature
**When Requested:** First use of audio feature
**Can Deny:** Yes, but audio feature won't work

**What it allows:**
- Select audio files from storage
- Process audio with AI
- Play audio files

#### 3. Read Media Video (Android 13+)
**Permission:** `READ_MEDIA_VIDEO`
**Purpose:** Future video feature support
**When Requested:** When video feature added
**Can Deny:** Yes

#### 4. Read External Storage (Android 12 and below)
**Permission:** `READ_EXTERNAL_STORAGE`
**Purpose:** Access media files on older Android
**When Requested:** First use of media features
**Can Deny:** Yes, but media features won't work

**Replacement:** On Android 13+, replaced by granular media permissions above

#### 5. Camera (Optional)
**Permission:** `CAMERA`
**Purpose:** Take photos for image analysis
**When Requested:** When camera button tapped
**Can Deny:** Yes, can still upload from gallery

**What it allows:**
- Take photos within app
- Use camera for image analysis

**Privacy:**
- Only when app is in foreground
- Indicator shown when camera active
- No background access

#### 6. Record Audio (Optional)
**Permission:** `RECORD_AUDIO`
**Purpose:** Record audio for analysis
**When Requested:** When record button tapped
**Can Deny:** Yes, can still upload audio files

**What it allows:**
- Record audio within app
- Voice input for chat (future)

**Privacy:**
- Only when app is in foreground
- Indicator shown when mic active
- No background access

#### 7. Post Notifications (Android 13+)
**Permission:** `POST_NOTIFICATIONS`
**Purpose:** Show automation workflow notifications
**When Requested:** On first launch (Android 13+)
**Can Deny:** Yes, automation will work without notifications

**What it allows:**
- Workflow completion alerts
- Model download notifications
- Important app updates

#### 8. Internet (Auto-granted)
**Permission:** `INTERNET`
**Purpose:** Download AI models only
**When Requested:** Auto-granted at install
**Can Deny:** No (but app works offline)

**What it's used for:**
- Download AI models from repository
- Check for app updates (if enabled)

**What it's NOT used for:**
- ❌ Sending user data
- ❌ Analytics
- ❌ Ads
- ❌ AI processing (all local)

### Permission Best Practices

**For Users:**

1. **Grant Media Permissions**
   - Required for core features
   - Safe - scoped to media only

2. **Grant Camera/Mic** (Recommended)
   - Convenient for quick captures
   - Can always deny and upload files instead

3. **Grant Notifications** (Recommended)
   - Useful for automation alerts
   - Can customize notification settings

**Managing Permissions:**
```
Settings → Apps → LocalAI Chat → Permissions

Options:
- Allow
- Allow only while using app (recommended)
- Ask every time
- Don't allow
```

**Revoking Permissions:**
```
Settings → Apps → LocalAI Chat → Permissions
→ Select permission → Don't allow
```

App will prompt again when feature is used.

## Configuration

### App Settings

**Access Settings:**
```
Home → ⚙️ Icon (top right)
or
Navigation drawer → Settings
```

### General Settings

**Theme:**
```
Settings → Appearance → Theme
Options:
- Light
- Dark
- System default (recommended)
```

**Language:**
```
Settings → General → Language
- System default
- English
- (Other languages if added)
```

**Notifications:**
```
Settings → Notifications
- Enable/disable notifications
- Notification sound
- Vibration
```

### AI Settings

**Default Model:**
```
Settings → AI → Default Model
Select:
- Gemma-2B (fast)
- Gemma-7B (capable)
- Phi-3-Mini (balanced)
```

**Response Length:**
```
Settings → AI → Max Response Length
Range: 128 - 4096 tokens
Default: 2048
Recommendation:
- 512 for quick responses
- 2048 for detailed responses
- 4096 for very detailed content
```

**Temperature:**
```
Settings → AI → Temperature
Range: 0.0 - 2.0
Default: 0.7
Higher = more creative, less predictable
Lower = more focused, more predictable
```

**Streaming:**
```
Settings → AI → Enable Streaming
- On: See response as it's generated (recommended)
- Off: Wait for complete response
```

### Storage Settings

**Cache Management:**
```
Settings → Storage → Cache
- View cache size
- Clear cache (keeps models and data)
- Auto-clear old cache
```

**Data Management:**
```
Settings → Storage → Data
- View database size
- Export conversations
- Import conversations
- Delete old sessions
```

**Model Storage:**
```
Settings → Storage → Models
- View downloaded models
- Delete models
- Model cache location
```

### Privacy Settings

**Data Collection:**
```
Settings → Privacy → Analytics
- Disable usage analytics (recommended)
- Disable crash reports (affects debugging)
```

**Data Export:**
```
Settings → Privacy → Export Data
- Export all data as JSON
- Export specific sessions
- Share or save backup
```

**Data Deletion:**
```
Settings → Privacy → Delete All Data
- Clear all conversations
- Keep models and settings
or
- Full reset (everything deleted)
```

### Advanced Settings

**Developer Options:**
```
Settings → Advanced → Developer Options
(Tap version number 7 times to unlock)

Options:
- Show debug logs
- Enable experimental features
- Mock AI mode
- Performance monitoring
```

**Model Configuration:**
```
Settings → Advanced → Model Config
- Model download source
- Model cache directory
- Auto-update models
```

**Automation:**
```
Settings → Advanced → Automation
- Enable/disable automation system
- View workflow executions
- Auto-backup schedule
```

## Storage Requirements

### App Size Breakdown

**Base App:**
```
App binary: ~50-80 MB
  - Code: ~30 MB
  - Resources: ~20 MB
  - Libraries: ~30 MB
```

**AI Models:**
```
Gemma-2B:    ~1.5 GB
Gemma-7B:    ~5.0 GB
Phi-3-Mini:  ~2.0 GB
```

**User Data:**
```
Database:    ~10-500 MB (depends on usage)
  - Conversations
  - Messages
  - Benchmarks
  - Workflows

Cache:       ~50-200 MB
  - Temporary files
  - Image cache
  - Audio cache
```

**Total Storage Examples:**

**Light User:**
```
Base app:        80 MB
Gemma-2B model:  1.5 GB
User data:       50 MB
Cache:           30 MB
----------------------------
Total:           ~1.7 GB
```

**Heavy User:**
```
Base app:        80 MB
Gemma-7B model:  5.0 GB
User data:       500 MB
Cache:           200 MB
----------------------------
Total:           ~5.8 GB
```

**Multiple Models:**
```
Base app:        80 MB
Gemma-2B:        1.5 GB
Gemma-7B:        5.0 GB
Phi-3-Mini:      2.0 GB
User data:       300 MB
Cache:           150 MB
----------------------------
Total:           ~9.0 GB
```

### Storage Management

**Check Available Space:**
```
Settings → Storage
or
Android Settings → Storage → This device
```

**Free Up Space:**

1. **Delete Unused Models**
   ```
   App → Models → Select model → Delete
   Frees: 1.5-5 GB per model
   ```

2. **Clear Cache**
   ```
   Settings → Storage → Clear Cache
   Frees: 50-200 MB
   Effect: None on functionality
   ```

3. **Delete Old Conversations**
   ```
   Settings → Storage → Delete Old Sessions
   Select: Older than 30/60/90 days
   Frees: Varies (10-200 MB)
   ```

4. **Export and Delete**
   ```
   Settings → Export Data
   Settings → Delete All Data
   Frees: All user data
   Can reimport later
   ```

**Recommendations:**

- Keep at least 2 GB free space for smooth operation
- Use smaller models on devices with limited storage
- Regularly delete old conversations you don't need
- Export important conversations before major cleanups

## Performance Optimization

### For Best Performance

**1. Choose Right Model:**
```
Device RAM → Recommended Model
4 GB      → Gemma-2B (or mock mode)
6 GB      → Gemma-2B or Phi-3-Mini
8 GB+     → Any model, including Gemma-7B
```

**2. Close Background Apps:**
```
Settings → Apps → Running apps → Close unused apps
or
Recent apps button → Swipe away unused apps
```

**3. Enable Performance Mode:**
```
Android Settings → Battery → Performance mode
(Increases CPU speed, uses more battery)
```

**4. Keep Device Charged:**
```
Battery saver mode slows CPU
For best performance: Keep >50% battery
or connect to charger
```

**5. Free Up Storage:**
```
Low storage slows device
Keep at least 2 GB free
```

**6. Restart Device:**
```
Occasional restart clears memory
Especially after heavy use
```

**7. Update Android:**
```
Latest Android versions have performance improvements
Settings → System → System update
```

**8. Optimize App Settings:**
```
LocalAI Chat → Settings → AI
- Reduce max response length (512-1024)
- Disable streaming if slow
- Use lower temperature (0.3-0.5)
```

### Performance Metrics

**Expected Performance** (Gemma-2B on mid-range device):

```
Time to First Token: 1-3 seconds
Tokens per second:   10-30 tokens/sec
Memory usage:        1.5-2.5 GB
CPU usage:           60-90%
Battery per hour:    15-25%
```

**Benchmarking:**
```
App → Performance tab
Run benchmark to test your device
Compare with expected values
```

## Troubleshooting

### Common Issues

#### App Won't Install

**Error:** "App not installed"

**Solutions:**

1. **Check Android version**
   ```
   Settings → About phone → Android version
   Must be 7.0 or higher
   ```

2. **Check storage space**
   ```
   Settings → Storage
   Need at least 500 MB free
   ```

3. **Enable unknown sources**
   ```
   Settings → Security → Unknown sources (Android 7-11)
   Settings → Apps → Install unknown apps (Android 12+)
   ```

4. **Check APK integrity**
   ```
   Re-download APK
   Verify file size matches
   ```

5. **Uninstall old version**
   ```
   Settings → Apps → LocalAI Chat → Uninstall
   Then reinstall
   ```

#### App Crashes on Launch

**Solutions:**

1. **Check logcat**
   ```bash
   adb logcat | grep LocalAiChatApp
   Look for crash reason
   ```

2. **Clear app data**
   ```
   Settings → Apps → LocalAI Chat → Storage → Clear data
   Warning: Deletes all conversations
   ```

3. **Reinstall app**
   ```
   Uninstall completely
   Restart device
   Reinstall
   ```

4. **Check compatibility**
   ```
   Verify device meets minimum requirements
   ```

#### Model Download Fails

**Solutions:**

1. **Check internet connection**
   ```
   Open browser, test connection
   Use WiFi, not mobile data
   ```

2. **Check storage space**
   ```
   Need 2x model size free
   (e.g., 3 GB free for 1.5 GB model)
   ```

3. **Restart download**
   ```
   Models → Cancel → Restart download
   ```

4. **Change download source** (if available)
   ```
   Settings → Advanced → Model source
   ```

5. **Download manually** (advanced)
   ```
   Download model separately
   Place in app's model directory
   ```

#### Slow Performance

**Solutions:**

1. **Use smaller model**
   ```
   Switch from Gemma-7B to Gemma-2B
   ```

2. **Close background apps**
   ```
   Free up RAM
   ```

3. **Enable performance mode**
   ```
   Settings → Battery → Performance
   ```

4. **Lower settings**
   ```
   Reduce max response length
   Lower temperature
   ```

5. **Clear cache**
   ```
   Settings → Storage → Clear cache
   ```

#### Out of Memory Errors

**Solutions:**

1. **Switch to smaller model**
   ```
   Models → Select Gemma-2B
   ```

2. **Close other apps**
   ```
   Free up RAM
   ```

3. **Restart device**
   ```
   Clears memory
   ```

4. **Reduce response length**
   ```
   Settings → AI → Max length → 512
   ```

#### Permissions Not Working

**Solutions:**

1. **Check permission granted**
   ```
   Settings → Apps → LocalAI Chat → Permissions
   Ensure needed permissions are "Allowed"
   ```

2. **Restart app**
   ```
   Close app completely
   Reopen
   ```

3. **Reinstall** (last resort)
   ```
   Backup data first
   Uninstall and reinstall
   ```

### Getting Help

**Before asking for help:**

1. Check this guide's FAQ section
2. Review error messages carefully
3. Note your device model and Android version
4. Check app version (Settings → About)

**How to report issues:**

1. **Gather information**
   ```
   Device: [Model name]
   Android version: [Version]
   App version: [Version]
   Error message: [Exact message]
   Steps to reproduce: [Detailed steps]
   ```

2. **Collect logs** (if possible)
   ```bash
   adb logcat -d > logcat.txt
   ```

3. **Create issue**
   - GitHub Issues: [Repository URL]
   - Include all information above
   - Attach logs if available
   - Add screenshots if helpful

## FAQ

### Installation FAQs

**Q: What's the minimum Android version?**
A: Android 7.0 (API 24). Released August 2016.

**Q: Will it work on Android 6 or older?**
A: No. Minimum is Android 7.0 due to library requirements.

**Q: How much storage do I need?**
A: Minimum 2 GB free. Recommended 5-10 GB for multiple models.

**Q: Can I install on SD card?**
A: App installs to internal storage. Models can be moved to SD card (future feature).

**Q: Do I need Google Play Services?**
A: No. App works without Google Play Services.

**Q: Works on rooted devices?**
A: Yes, fully compatible with rooted devices.

**Q: Works on custom ROMs?**
A: Yes, as long as Android version is 7.0+.

### Performance FAQs

**Q: Why is it slow on my device?**
A: Depends on device specs. Use smaller models on older devices.

**Q: How much RAM do I need?**
A: Minimum 4 GB. Recommended 6-8 GB for best performance.

**Q: Can I use multiple models?**
A: Yes, but only one active at a time. Switch in settings.

**Q: Why does it use so much battery?**
A: AI processing is CPU-intensive. Normal to use 15-25% per hour.

**Q: Can I use while charging?**
A: Yes, recommended for extended use.

### Feature FAQs

**Q: Does it work offline?**
A: Yes, completely offline after models are downloaded.

**Q: Can I use my own AI models?**
A: Not currently. Feature planned for future.

**Q: Can conversations sync across devices?**
A: Not automatically. Use export/import feature.

**Q: Is there a cloud backup?**
A: No. Everything is local. Use export for backups.

**Q: Can I share conversations?**
A: Yes. Long-press conversation → Share → Choose app.

### Privacy FAQs

**Q: Is data collected?**
A: No. Everything stays on your device.

**Q: Does it need internet?**
A: Only for downloading models. After that, fully offline.

**Q: Can others see my conversations?**
A: No. All data is private and stored locally.

**Q: Is it open source?**
A: Yes. Full source code available on GitHub.

**Q: How is data secured?**
A: Android app sandbox. Optional: Device encryption.

---

## Quick Reference Card

### Minimum Requirements
- Android 7.0+
- 4 GB RAM
- 2 GB storage
- ARM64 processor

### Installation Steps
1. Enable unknown sources
2. Download APK
3. Install
4. Grant permissions
5. Download model (optional)

### Essential Settings
- Settings → AI → Default Model
- Settings → AI → Temperature: 0.7
- Settings → Appearance → Theme: System

### Troubleshooting
- Won't install → Check Android version & storage
- Crashes → Clear data & reinstall
- Slow → Use smaller model
- Out of memory → Close apps, use Gemma-2B

### Support
- Docs: `/docs` folder
- Issues: GitHub Issues
- Logs: `adb logcat`

---

**Last Updated:** November 2024
**App Version:** 1.0.0
**Min Android:** 7.0 (API 24)
**Target Android:** 14 (API 35)
