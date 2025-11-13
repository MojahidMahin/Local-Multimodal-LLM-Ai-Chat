# 🤖 LocalAiChatApp

> **Explore, Experience, and Evaluate the Future of On-Device Generative AI**

A comprehensive Android application featuring Google AI Edge Gallery capabilities with local AI processing, multi-modal interactions, and performance monitoring - all running entirely on your device.

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)](https://kotlinlang.org)
[![Architecture](https://img.shields.io/badge/Architecture-Clean%20MVVM-orange.svg)](#architecture)

## 📱 Screenshots & Demo

*Coming soon - UI implementation in progress*

## ✨ Features

### 🎯 **Core AI Capabilities**
- **💬 AI Chat** - Multi-turn conversational AI with conversation history
- **🖼️ Ask Image** - Upload images and get detailed analysis and insights
- **🎵 Ask Audio** - Process audio files with transcription and analysis
- **✍️ Prompt Lab** - Single-turn AI interactions with customizable templates

### 🚀 **Advanced Features**
- **📱 100% Offline** - All AI processing happens locally on your device
- **🔄 Model Management** - Download, manage, and switch between AI models
- **📊 Performance Insights** - Real-time benchmarking (TTFT, decode speed, latency)
- **📝 Prompt Templates** - 10+ built-in templates across 5 categories
- **🔍 Smart Search** - Find conversations across all your chat history
- **⭐ Bookmarks** - Save important conversations for quick access
- **🎨 Material 3 UI** - Modern, beautiful interface following Material Design

### 🔧 **Technical Excellence**
- **Clean Architecture** - Maintainable, testable, and scalable codebase
- **Room Database** - Comprehensive local storage with migrations
- **Reactive Programming** - Kotlin Flow for responsive data streams
- **Dependency Injection** - Hilt for clean dependency management
- **Error Handling** - Robust error handling with Result wrapper pattern

## 🏗️ Architecture

LocalAiChatApp follows **Clean Architecture** principles with clear separation of concerns:

```
┌─────────────────────────────────────────────┐
│             Presentation Layer              │
│          (UI, ViewModels, Navigation)       │
├─────────────────────────────────────────────┤
│              Domain Layer                   │
│         (Business Logic, Entities)          │
├─────────────────────────────────────────────┤
│               Data Layer                    │
│     (Repository Impl, Database, API)        │
└─────────────────────────────────────────────┘
```

### 📊 **Database Schema**
- **5 comprehensive entities** with proper relationships
- **Foreign key constraints** ensuring data integrity
- **Migration support** for seamless updates
- **Performance optimized** with strategic indices

### 🔄 **Repository Pattern**
- **Reactive data flow** using Kotlin Flow
- **Interface-based design** for easy testing
- **Comprehensive error handling** with Result wrapper
- **Caching strategies** for optimal performance

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog | 2023.1.1 or later
- Android SDK 24 or later
- Kotlin 1.9.0 or later

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/LocalAiChatApp.git
   cd LocalAiChatApp
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory

3. **Build and Run**
   ```bash
   ./gradlew assembleDebug
   ```
   Or use Android Studio's build button

### 🔧 Configuration

The app works out of the box with mock AI implementations. For real AI processing:

1. **AI Edge Integration** - Replace mock implementations with actual AI Edge runtime
2. **Model Downloads** - Configure model source URLs in `ModelApiService`
3. **Permissions** - Camera, microphone, and storage permissions are handled automatically

## 📚 Documentation

Comprehensive documentation is available in the [`docs`](./docs) directory:

### 📖 For Users

| Document | Description |
|----------|-------------|
| **[🚀 Installation Guide](./docs/INSTALLATION.md)** | Step-by-step installation and setup instructions |
| **[📱 User Guide](./docs/USER_GUIDE.md)** | Complete guide to using all app features |
| **[❓ FAQ](./docs/FAQ.md)** | Frequently asked questions and answers |
| **[🔧 Troubleshooting](./docs/TROUBLESHOOTING.md)** | Solutions to common problems and issues |

### 👨‍💻 For Developers

| Document | Description |
|----------|-------------|
| **[🛠️ Development Setup](./docs/DEVELOPMENT_SETUP.md)** | Setting up your development environment |
| **[🤝 Contributing Guide](./docs/CONTRIBUTING.md)** | How to contribute to the project |
| **[✅ Testing Guide](./docs/TESTING.md)** | Writing and running tests |
| **[📝 Code Style Guide](./docs/CODE_STYLE.md)** | Coding standards and conventions |

### 📐 Technical Documentation

| Document | Description |
|----------|-------------|
| **[🏗️ Architecture](./docs/ARCHITECTURE.md)** | System architecture and design patterns |
| **[💾 Database Schema](./docs/DATABASE_SCHEMA.md)** | Database structure and relationships |
| **[🔌 API Reference](./docs/API_REFERENCE.md)** | Repository interfaces and usage examples |
| **[📋 Implementation Guide](./docs/IMPLEMENTATION_DOCS.md)** | Complete technical implementation details |
| **[✅ Phase 1 Completion](./docs/PHASE_1_COMPLETION_SUMMARY.md)** | Current implementation status |

### 🎯 Quick Links

- **New to the project?** Start with the [User Guide](./docs/USER_GUIDE.md)
- **Want to contribute?** Check out the [Contributing Guide](./docs/CONTRIBUTING.md)
- **Setting up dev environment?** See [Development Setup](./docs/DEVELOPMENT_SETUP.md)
- **Having issues?** Visit [Troubleshooting](./docs/TROUBLESHOOTING.md) or [FAQ](./docs/FAQ.md)

## 🛠️ Technology Stack

### **Core Framework**
- **Kotlin** - Modern Android development language
- **Jetpack Compose** - Declarative UI toolkit
- **Material 3** - Latest Material Design system

### **Architecture & DI**
- **MVVM Pattern** - Model-View-ViewModel architecture
- **Clean Architecture** - Separation of concerns
- **Hilt** - Dependency injection framework
- **Kotlin Coroutines** - Asynchronous programming

### **Data & Storage**
- **Room Database** - Local data persistence
- **Kotlin Flow** - Reactive data streams
- **DataStore** - Settings and preferences
- **Type Converters** - Complex data handling

### **Networking & Files**
- **Retrofit** - HTTP client for API calls
- **OkHttp** - Network layer with interceptors
- **Coil** - Image loading and caching
- **WorkManager** - Background processing

### **Media & Permissions**
- **CameraX** - Camera integration
- **Media3** - Audio/video processing
- **Accompanist** - Permission handling

## 📖 Usage Examples

### Basic Chat
```kotlin
// Create a chat session
val session = chatRepository.createSession(
    modelId = "gemma-2b",
    title = "General Discussion",
    taskType = TaskType.CHAT
)

// Send user message and get AI response
aiInferenceRepository.generateResponse(
    modelId = "gemma-2b",
    input = "Explain quantum computing",
    taskType = TaskType.CHAT
).collect { response ->
    // Handle streaming response
    updateUI(response.content, response.isComplete)
}
```

### Image Analysis
```kotlin
// Analyze an image
aiInferenceRepository.analyzeImage(
    modelId = "gemma-7b",
    imageUri = selectedImageUri,
    query = "Describe what you see in detail"
).collect { response ->
    displayAnalysisResult(response)
}
```

### Template-Based Generation
```kotlin
// Use a prompt template
val template = promptTemplateRepository.getTemplateById("summarize-article")
aiInferenceRepository.generateWithTemplate(
    modelId = "phi-3-mini",
    template = template.template,
    parameters = mapOf("text" to articleContent)
).collect { response ->
    displaySummary(response)
}
```

## 🧪 Testing

The app includes comprehensive testing coverage:

```bash
# Run unit tests
./gradlew testDebugUnitTest

# Run instrumented tests
./gradlew connectedDebugAndroidTest

# Generate coverage report
./gradlew jacocoTestReport
```

## 📈 Performance

### **Benchmarking Features**
- **Time to First Token (TTFT)** - Latency measurement
- **Decode Speed** - Tokens per second calculation
- **Memory Usage** - RAM consumption monitoring
- **CPU Usage** - Processor utilization tracking
- **Battery Impact** - Power consumption analysis

### **Optimization**
- **Local Processing** - No network dependency after model download
- **Efficient Database** - Optimized queries with proper indexing
- **Memory Management** - Proper lifecycle handling and cleanup
- **Background Processing** - Non-blocking UI with coroutines

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

### **Development Workflow**
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### **Code Standards**
- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) principles
- Write comprehensive tests
- Document public APIs

## 🐛 Issues & Support

- **Bug Reports** - [Create an issue](https://github.com/yourusername/LocalAiChatApp/issues/new?template=bug_report.md)
- **Feature Requests** - [Request a feature](https://github.com/yourusername/LocalAiChatApp/issues/new?template=feature_request.md)
- **Questions** - [Ask in Discussions](https://github.com/yourusername/LocalAiChatApp/discussions)

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

```
Copyright 2024 LocalAiChatApp

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## 🙏 Acknowledgments

- **Google AI Edge** - For providing the foundation for on-device AI
- **Android Jetpack** - For the comprehensive development toolkit
- **Material Design** - For the beautiful design system
- **Open Source Community** - For the amazing libraries and tools

---

<div align="center">

**Built with ❤️ for the future of on-device AI**

[⭐ Star this repo](https://github.com/yourusername/LocalAiChatApp) • [🐛 Report Bug](https://github.com/yourusername/LocalAiChatApp/issues) • [💡 Request Feature](https://github.com/yourusername/LocalAiChatApp/issues)

</div>