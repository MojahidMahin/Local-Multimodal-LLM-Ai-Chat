# Multi-Platform Architecture Guide

## Overview

LocalAiChatApp is now a cross-platform application supporting both **Android** and **Web** platforms, sharing core business logic while providing native experiences on each platform.

## Architecture Strategy

```
┌─────────────────────────────────────────────────────────┐
│                  SHARED MODULE                          │
│    (Kotlin Multiplatform - Business Logic)              │
├─────────────────────────────────────────────────────────┤
│  • Domain Models                                        │
│  • Business Logic                                       │
│  • Repository Interfaces                                │
│  • Use Cases                                            │
│  • Automation System Core                               │
└─────────────────────────────────────────────────────────┘
           ↓                           ↓
┌──────────────────────┐    ┌──────────────────────┐
│   ANDROID PLATFORM   │    │    WEB PLATFORM      │
├──────────────────────┤    ├──────────────────────┤
│ • Jetpack Compose    │    │ • React + TypeScript │
│ • Room Database      │    │ • IndexedDB          │
│ • Hilt DI            │    │ • React Context      │
│ • Android SDK        │    │ • Web APIs           │
│ • Material 3         │    │ • Material UI        │
│ • Native AI (NNAPI)  │    │ • WebAssembly AI     │
└──────────────────────┘    └──────────────────────┘
```

## Technology Stack

### Shared Layer (Kotlin Multiplatform)
- **Language**: Kotlin 2.0.20
- **Serialization**: kotlinx.serialization
- **Coroutines**: kotlinx.coroutines
- **DateTime**: kotlinx-datetime
- **HTTP**: Ktor client (for model downloads)

### Android Platform
- **UI**: Jetpack Compose
- **Database**: Room
- **DI**: Hilt
- **AI**: TensorFlow Lite / MediaPipe
- **Build**: Gradle

### Web Platform
- **Framework**: React 18 + TypeScript
- **UI Library**: Material-UI (MUI)
- **State Management**: Redux Toolkit + RTK Query
- **Database**: Dexie.js (IndexedDB wrapper)
- **AI**: TensorFlow.js / ONNX Runtime Web
- **Build**: Vite
- **PWA**: Workbox

## Project Structure

```
LocalAiChatApp/
├── android/                    # Android-specific code
│   ├── app/
│   │   └── src/main/java/
│   │       └── com/localllm/localaichatapp/
│   │           ├── presentation/    # Compose UI
│   │           ├── data/           # Android data impl
│   │           └── di/             # Hilt modules
│   └── build.gradle.kts
│
├── web/                        # Web application
│   ├── public/                # Static assets
│   ├── src/
│   │   ├── components/        # React components
│   │   ├── features/          # Feature modules
│   │   ├── services/          # API & storage services
│   │   ├── store/             # Redux store
│   │   ├── hooks/             # Custom React hooks
│   │   ├── utils/             # Utilities
│   │   └── App.tsx            # Root component
│   ├── package.json
│   ├── vite.config.ts
│   └── tsconfig.json
│
├── shared/                     # Kotlin Multiplatform shared code
│   ├── src/
│   │   ├── commonMain/        # Platform-independent code
│   │   │   └── kotlin/
│   │   │       ├── domain/
│   │   │       │   ├── model/
│   │   │       │   ├── repository/
│   │   │       │   └── usecase/
│   │   │       └── util/
│   │   ├── androidMain/       # Android-specific implementations
│   │   └── jsMain/            # JavaScript/Web implementations
│   └── build.gradle.kts
│
├── docs/                       # Documentation
│   ├── MULTI_PLATFORM.md      # This file
│   ├── WEB_SETUP.md           # Web setup guide
│   └── ANDROID_SETUP.md       # Android setup guide
│
└── build.gradle.kts           # Root build file
```

## Feature Parity Matrix

| Feature | Android | Web | Notes |
|---------|---------|-----|-------|
| AI Chat | ✅ | ✅ | Offline models on both |
| Ask Image | ✅ | ✅ | Camera on Android, file upload on Web |
| Ask Audio | ✅ | ✅ | Microphone access on both |
| Prompt Lab | ✅ | ✅ | Identical templates |
| Model Management | ✅ | ✅ | Download & cache |
| Automation System | ✅ | ⚠️ | Limited triggers on Web |
| Offline Mode | ✅ | ✅ | PWA with service workers |
| Search | ✅ | ✅ | Full-text search |
| Bookmarks | ✅ | ✅ | Synced via storage |
| Performance Monitor | ✅ | ✅ | Platform-specific metrics |
| Dark Mode | ✅ | ✅ | System preference detection |
| Export Data | ✅ | ✅ | JSON export |

## Platform-Specific Implementations

### Storage Layer

#### Android (Room)
```kotlin
@Database(entities = [...], version = 4)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun workflowDao(): WorkflowDao
}
```

#### Web (IndexedDB via Dexie)
```typescript
class ChatDatabase extends Dexie {
    sessions!: Table<ChatSession>;
    messages!: Table<ChatMessage>;
    workflows!: Table<Workflow>;

    constructor() {
        super('LocalAiChatDB');
        this.version(4).stores({
            sessions: '++id, modelId, createdAt',
            messages: '++id, sessionId, timestamp',
            workflows: '++id, triggerType, enabled'
        });
    }
}
```

### AI Model Integration

#### Android (TensorFlow Lite)
```kotlin
class AndroidAiInference : AiInferenceRepository {
    private val interpreter: Interpreter

    override suspend fun generateResponse(
        input: String
    ): Flow<ResponseChunk> = flow {
        // TFLite inference
    }
}
```

#### Web (TensorFlow.js)
```typescript
class WebAiInference implements AiInferenceRepository {
    private model?: tf.GraphModel;

    async generateResponse(
        input: string
    ): Promise<AsyncIterable<ResponseChunk>> {
        // TF.js inference
    }
}
```

### Dependency Injection

#### Android (Hilt)
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    fun provideAiInference(
        impl: AndroidAiInference
    ): AiInferenceRepository = impl
}
```

#### Web (React Context)
```typescript
export const RepositoryContext = createContext<Repositories>({
    aiInference: new WebAiInference(),
    chatRepository: new IndexedDBChatRepository(),
    // ...
});
```

## Data Synchronization (Optional)

For users who want cross-device sync:

```
┌─────────┐     ┌─────────┐     ┌─────────┐
│ Android │────▶│  Cloud  │◀────│   Web   │
│  Device │     │ Storage │     │ Browser │
└─────────┘     └─────────┘     └─────────┘
                     │
                  Optional
                  (User opt-in)
```

Options:
1. **No Sync** (default): Fully local, no cloud
2. **Manual Export/Import**: JSON files
3. **Cloud Sync** (opt-in): Firebase, Supabase, or self-hosted

## Offline Capabilities

### Android
- Native offline support
- Models stored in app storage
- Database persisted locally

### Web (PWA)
```javascript
// Service Worker for offline support
self.addEventListener('fetch', (event) => {
    event.respondWith(
        caches.match(event.request)
            .then(response => response || fetch(event.request))
    );
});

// Cache AI models
const MODEL_CACHE = 'ai-models-v1';
```

## Build & Deployment

### Android
```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Deploy to Play Store
./gradlew bundleRelease
```

### Web
```bash
# Development
npm run dev

# Production build
npm run build

# Preview production build
npm run preview

# Deploy to hosting
npm run deploy
```

## Deployment Targets

### Android
- **Google Play Store**
- **F-Droid** (open source)
- **Direct APK download**
- **Amazon Appstore**

### Web
- **Vercel** (recommended for static hosting)
- **Netlify**
- **GitHub Pages**
- **Firebase Hosting**
- **Self-hosted** (Nginx, Apache)

## Performance Considerations

### Android
- Native performance
- NNAPI hardware acceleration
- Efficient memory management
- Battery optimization

### Web
- WebAssembly for compute-intensive tasks
- Web Workers for background processing
- IndexedDB for large datasets
- Service Worker caching
- Code splitting for fast initial load

## Security

### Android
- Android Keystore for sensitive data
- App sandbox isolation
- Runtime permissions
- Certificate pinning (optional)

### Web
- HTTPS only
- Content Security Policy (CSP)
- Secure cookie handling
- XSS protection
- CORS configuration

## Browser Support

### Web Platform Requirements
- **Modern Browsers**:
  - Chrome/Edge 90+
  - Firefox 88+
  - Safari 14+
  - Opera 76+

- **Required APIs**:
  - IndexedDB
  - Service Workers
  - Web Workers
  - WebAssembly
  - FileReader API
  - MediaDevices (camera/microphone)

## Development Workflow

### Setup
1. Clone repository
2. Install Android Studio (for Android)
3. Install Node.js 18+ (for Web)
4. Run `npm install` in web directory
5. Sync Gradle in Android Studio

### Development
```bash
# Android - Open in Android Studio
# Run/Debug as normal

# Web - Terminal
cd web
npm run dev        # Starts dev server at localhost:5173
```

### Testing
```bash
# Android
./gradlew test
./gradlew connectedAndroidTest

# Web
cd web
npm run test       # Jest tests
npm run test:e2e   # Playwright E2E tests
```

## Migration Path

For existing Android users:

1. **Export data** from Android app
2. **Import to web** via JSON upload
3. **Download models** in web version
4. **Resume usage** seamlessly

## Future Enhancements

### Planned Features
- [ ] Desktop apps (Electron or Tauri)
- [ ] iOS native app (Swift UI)
- [ ] Browser extensions
- [ ] Real-time collaboration
- [ ] Cloud model inference (optional)
- [ ] Voice-to-text in browser
- [ ] Offline model training

### Kotlin Multiplatform Migration
Phase 2: Migrate more code to shared module
- Repository implementations
- Use case logic
- Validation logic
- Utility functions

## Advantages of This Approach

### For Users
✅ Consistent experience across devices
✅ Access from any device with browser
✅ No installation required for web version
✅ Offline-first on both platforms
✅ Data portability

### For Developers
✅ Shared business logic reduces duplication
✅ Easier to maintain consistency
✅ Platform-specific optimizations possible
✅ Incremental migration to multiplatform
✅ Larger potential user base

## Resources

- [Kotlin Multiplatform Docs](https://kotlinlang.org/docs/multiplatform.html)
- [Compose for Web](https://compose-web.ui.pages.jetbrains.team/)
- [React Documentation](https://react.dev/)
- [TensorFlow.js](https://www.tensorflow.org/js)
- [PWA Documentation](https://web.dev/progressive-web-apps/)
- [IndexedDB Guide](https://developer.mozilla.org/en-US/docs/Web/API/IndexedDB_API)

---

This multi-platform architecture enables LocalAiChatApp to reach users on any device while maintaining the core values of privacy, offline functionality, and powerful AI capabilities.
