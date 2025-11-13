# Frequently Asked Questions (FAQ)

Common questions and answers about LocalAiChatApp.

## Table of Contents
- [General Questions](#general-questions)
- [Installation & Setup](#installation--setup)
- [Features & Functionality](#features--functionality)
- [Models & Performance](#models--performance)
- [Privacy & Security](#privacy--security)
- [Troubleshooting](#troubleshooting)
- [Development](#development)

## General Questions

### What is LocalAiChatApp?

LocalAiChatApp is an Android application that provides AI-powered chat, image analysis, and audio processing capabilities - all running locally on your device. No internet connection is required after the initial model download.

### Is it really 100% offline?

Yes! Once you've downloaded the AI models, the app works completely offline. All AI processing happens on your device, with no data sent to external servers.

### How much does it cost?

The app is completely free and open-source. There are no subscriptions, in-app purchases, or API fees.

### What makes it different from other AI chat apps?

**Key Differences**:
- **Privacy**: All processing happens on your device
- **Offline**: Works without internet after model download
- **No Costs**: No API fees or subscriptions
- **Multimodal**: Text, image, and audio processing
- **Open Source**: Transparent, auditable code

### What Android versions are supported?

The app supports Android 7.0 (API 24) and higher, up to Android 14 (API 35).

### Can I use it on iOS?

Currently, the app is only available for Android. iOS support is not planned at this time.

### Is this an official Google product?

No, this is an independent open-source project. It uses Google AI Edge SDK but is not developed or endorsed by Google.

## Installation & Setup

### How do I install the app?

See our [Installation Guide](./INSTALLATION.md) for detailed instructions. In brief:
1. Clone the repository
2. Open in Android Studio
3. Build and install on your device

### What are the minimum requirements?

**For Android Device**:
- Android 7.0 (API 24) or higher
- 4 GB RAM minimum (6 GB+ recommended)
- 2 GB free storage (more for larger models)
- ARM64 architecture

**For Development**:
- Android Studio Hedgehog or later
- JDK 21
- 16 GB RAM recommended
- 20 GB free storage

### The build fails with Gradle errors. What should I do?

Try these steps:
```bash
# Clean and rebuild
./gradlew clean build

# Refresh dependencies
./gradlew build --refresh-dependencies

# Invalidate caches in Android Studio
# File > Invalidate Caches > Invalidate and Restart
```

See [Troubleshooting](./TROUBLESHOOTING.md) for more solutions.

### Do I need a Google account?

No, the app doesn't require any accounts or authentication.

## Features & Functionality

### What can I do with this app?

**Main Features**:
- **AI Chat**: Have conversations with AI
- **Image Analysis**: Upload and analyze images
- **Audio Processing**: Transcribe and analyze audio
- **Prompt Lab**: Use pre-built templates for common tasks
- **Model Management**: Download and switch between models
- **Performance Monitoring**: Track AI performance metrics

### How accurate are the AI responses?

Accuracy depends on the model you use:
- **Smaller models** (2B parameters): Fast but less accurate
- **Larger models** (7B parameters): More accurate but slower

The app currently uses mock AI responses for development. Real AI capabilities will be available when integrated with actual AI models.

### Can I customize the AI's behavior?

Yes! You can:
- Adjust temperature (creativity level)
- Set max response length
- Use custom prompt templates
- Switch between different models

### Does it support multiple languages?

The app UI is currently in English. AI model language support depends on which model you use - most support multiple languages.

### Can I use my own AI models?

Currently, only pre-configured models are supported. Custom model support may be added in future versions.

### How do I save important conversations?

You can bookmark conversations by tapping the star icon. Bookmarked conversations appear in a separate list for easy access.

## Models & Performance

### Which AI models are available?

Currently supported models:
- **Gemma-2B**: Lightweight, fast (2 billion parameters)
- **Gemma-7B**: More capable (7 billion parameters)
- **Phi-3-Mini**: Balanced performance

Note: Actual model availability depends on implementation status.

### How large are the models?

Model sizes vary:
- **Gemma-2B**: ~1-2 GB
- **Gemma-7B**: ~4-5 GB
- **Phi-3-Mini**: ~2-3 GB

Ensure you have sufficient storage before downloading.

### How do I download models?

1. Open the app
2. Navigate to **Models** tab
3. Select a model
4. Tap **Download**
5. Wait for download to complete

### Can I delete models I don't need?

Yes! Go to **Models** tab, find the model, and tap the delete button. This frees up storage space.

### Why is the AI response slow?

Several factors affect performance:
- **Model size**: Larger models are slower
- **Device specs**: Older/slower devices take longer
- **Background apps**: Close unnecessary apps
- **Battery mode**: Performance mode improves speed

See [Performance Optimization](./TROUBLESHOOTING.md#performance-issues) for tips.

### How much battery does it use?

Battery usage depends on:
- Model size (larger = more power)
- Usage duration
- Your device

The app includes battery monitoring in the Performance tab. Generally, expect 10-20% battery per hour of active use with larger models.

### Can I run it on a tablet?

Yes! The app works on Android tablets running API 24+.

## Privacy & Security

### Is my data private?

Yes! **All data stays on your device**:
- Conversations are stored locally
- No data sent to external servers
- No analytics or tracking (by default)
- No cloud synchronization

### Do you collect any data?

By default, no data is collected. If you enable crash reporting in settings, anonymized crash logs may be sent to help improve the app.

### Can conversations be recovered if I uninstall?

No, conversations are permanently deleted when you uninstall the app. There's no cloud backup.

### How do I export my data?

1. Go to **Settings**
2. Select **Privacy**
3. Tap **Export Data**
4. Choose export location

This creates a JSON file with all your conversations.

### How do I delete all my data?

1. Go to **Settings**
2. Select **Privacy**
3. Tap **Delete All Data**
4. Confirm deletion

Or simply uninstall the app.

### Is the code open source?

Yes! The entire codebase is open source under the Apache 2.0 license. You can review, audit, and contribute to the code on GitHub.

## Troubleshooting

### The app crashes on startup. What should I do?

1. **Clear app data**: Settings > Apps > LocalAiChatApp > Clear Data
2. **Reinstall the app**
3. **Check device compatibility** (Android 7.0+)
4. **Check logcat** for error messages: `adb logcat | grep LocalAiChatApp`

See [Troubleshooting Guide](./TROUBLESHOOTING.md#app-crashes) for more solutions.

### I'm getting "Out of memory" errors.

Try these solutions:
1. Use smaller models (Gemma-2B instead of Gemma-7B)
2. Close background apps
3. Free up device storage
4. Restart your device
5. Reduce max response length in settings

### The app won't download models.

Check:
1. **Internet connection**: Required for downloading
2. **Storage space**: Ensure enough free space
3. **Permissions**: Grant storage permissions
4. **Network restrictions**: Check firewall/VPN settings

### Images/audio won't upload.

Ensure you've granted:
- **Storage permissions**: Settings > Apps > LocalAiChatApp > Permissions
- **Camera permission** (for taking photos)
- **Microphone permission** (for recording audio)

### The UI looks broken or text is cut off.

Try:
1. **Restart the app**
2. **Clear app cache**: Settings > Apps > LocalAiChatApp > Clear Cache
3. **Update to latest version**
4. **Check device display settings**

### How do I report bugs?

1. **Check existing issues** on GitHub
2. **Create a new issue** if not found
3. **Include**:
   - Device model and Android version
   - App version
   - Steps to reproduce
   - Screenshots (if applicable)
   - Logcat output

See [Issue Guidelines](./CONTRIBUTING.md#issue-guidelines).

## Development

### How can I contribute?

We welcome contributions! See our [Contributing Guide](./CONTRIBUTING.md) for:
- Code contributions
- Bug reports
- Feature requests
- Documentation improvements

### I found a bug. How do I report it?

1. Check [existing issues](https://github.com/yourusername/Local-Multimodal-LLM-Ai-Chat/issues)
2. If not found, [create new issue](https://github.com/yourusername/Local-Multimodal-LLM-Ai-Chat/issues/new)
3. Use the bug report template
4. Include detailed information

### Can I request features?

Yes! [Create a feature request](https://github.com/yourusername/Local-Multimodal-LLM-Ai-Chat/issues/new) using the feature request template.

### How do I set up the development environment?

See our [Development Setup Guide](./DEVELOPMENT_SETUP.md) for complete instructions.

### What's the project structure?

The project follows Clean Architecture:
- **Presentation Layer**: UI, ViewModels, Navigation
- **Domain Layer**: Business logic, Use Cases
- **Data Layer**: Repositories, Database, API

See [Architecture Documentation](./ARCHITECTURE.md) for details.

### Can I use this in my own project?

Yes! The project is licensed under Apache 2.0. You're free to use, modify, and distribute it, as long as you follow the license terms.

### Where can I get help?

1. **Documentation**: Check the [docs](./README.md#-documentation) folder
2. **Issues**: Search [existing issues](https://github.com/yourusername/Local-Multimodal-LLM-Ai-Chat/issues)
3. **Discussions**: Ask in [GitHub Discussions](https://github.com/yourusername/Local-Multimodal-LLM-Ai-Chat/discussions)
4. **Community**: Join our community channels (TBD)

### How often is the app updated?

Update frequency varies based on:
- Community contributions
- Bug fixes needed
- New feature development

Check the [releases page](https://github.com/yourusername/Local-Multimodal-LLM-Ai-Chat/releases) for latest updates.

### Can I donate or sponsor the project?

Currently, we don't accept donations. The best way to support the project is:
- Use the app and provide feedback
- Report bugs
- Contribute code or documentation
- Share the project with others

## Still Have Questions?

If your question isn't answered here:

1. **Search** [existing issues and discussions](https://github.com/yourusername/Local-Multimodal-LLM-Ai-Chat)
2. **Check** other documentation:
   - [User Guide](./USER_GUIDE.md)
   - [Installation Guide](./INSTALLATION.md)
   - [Troubleshooting](./TROUBLESHOOTING.md)
3. **Ask** in [GitHub Discussions](https://github.com/yourusername/Local-Multimodal-LLM-Ai-Chat/discussions)
4. **Create** an issue for bug reports or feature requests

---

**Documentation Last Updated**: November 2025
