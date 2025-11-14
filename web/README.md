# LocalAI Chat - Web Version

## 🌐 Overview

This is the web version of LocalAI Chat, built with React, TypeScript, and Material-UI. It provides the same powerful AI capabilities as the Android app, running entirely in your browser with offline support via Progressive Web App (PWA) features.

## ✨ Features

- 🤖 **AI Chat** - Natural conversations with AI models
- 🖼️ **Image Analysis** - Upload and analyze images
- 🎵 **Audio Processing** - Transcribe and analyze audio
- 🔬 **Prompt Lab** - Template-based AI interactions
- 🗂️ **Model Management** - Download and manage AI models
- ⚙️ **Automation** - Create workflows to automate tasks
- 📱 **PWA Support** - Install as standalone app
- 🌙 **Dark Mode** - System-aware theming
- 💾 **Offline First** - Works without internet connection
- 🔒 **Privacy** - All data stays in your browser

## 🚀 Quick Start

### Prerequisites

- Node.js 18+ and npm 9+
- Modern web browser (Chrome 90+, Firefox 88+, Safari 14+)

### Installation

```bash
# Install dependencies
npm install

# Start development server
npm run dev

# Open browser at http://localhost:5173
```

### Building for Production

```bash
# Create optimized build
npm run build

# Preview production build
npm run preview
```

## 📁 Project Structure

```
web/
├── public/              # Static assets
├── src/
│   ├── components/      # Reusable React components
│   │   └── Layout.tsx   # App layout with navigation
│   ├── features/        # Feature-specific components
│   │   ├── home/       # Home page
│   │   ├── chat/       # Chat feature
│   │   ├── askimage/   # Image analysis
│   │   ├── askaudio/   # Audio processing
│   │   ├── promptlab/  # Prompt templates
│   │   ├── models/     # Model management
│   │   ├── automation/ # Workflow automation
│   │   └── settings/   # App settings
│   ├── services/       # Core services
│   │   ├── database.ts      # IndexedDB wrapper
│   │   └── ai-inference.ts  # AI inference service
│   ├── store/          # Redux state management
│   │   ├── index.ts         # Store configuration
│   │   └── slices/          # Redux slices
│   ├── types/          # TypeScript type definitions
│   ├── utils/          # Utility functions
│   ├── App.tsx         # Root component
│   ├── main.tsx        # Entry point
│   └── theme.ts        # Material-UI theme
├── package.json
├── vite.config.ts
└── tsconfig.json
```

## 🔧 Technology Stack

### Core
- **React 18** - UI framework
- **TypeScript** - Type safety
- **Vite** - Build tool and dev server
- **Material-UI (MUI)** - Component library

### State Management
- **Redux Toolkit** - Global state
- **RTK Query** - Data fetching (optional)
- **React Context** - Local state

### Storage
- **Dexie.js** - IndexedDB wrapper
- **LocalStorage** - Settings persistence

### AI & ML
- **TensorFlow.js** - In-browser AI inference
- **ONNX Runtime Web** - Alternative ML runtime

### PWA
- **Workbox** - Service worker generation
- **vite-plugin-pwa** - PWA plugin for Vite

## 💾 Data Storage

All data is stored locally in your browser:

### IndexedDB Tables

```
LocalAiChatDB (v4)
├── sessions      # Chat sessions
├── messages      # Chat messages
├── models        # AI models
├── templates     # Prompt templates
├── benchmarks    # Performance metrics
├── workflows     # Automation workflows
└── executions    # Workflow execution history
```

### Export/Import

```typescript
// Export data
import { db } from '@services/database';
const backup = await db.exportToJSON();
downloadFile(backup, 'backup.json');

// Import data
const data = await readFile();
await db.importFromJSON(data);
```

## 🤖 AI Models

### Supported Models

- **Gemma-2B** - Fast, lightweight model
- **Gemma-7B** - More capable model
- **Phi-3-Mini** - Balanced performance

### Model Integration

```typescript
import { aiService } from '@services/ai-inference';

// Load model
await aiService.loadModel('/models/gemma-2b/model.json');

// Generate response
for await (const chunk of aiService.generateResponse(input, modelId, sessionId)) {
  console.log(chunk.content);
}
```

## 📱 Progressive Web App

### Installation

Users can install the web app:

**Desktop**:
1. Click install icon in address bar
2. Confirm installation
3. App appears in application menu

**Mobile**:
1. Open in browser
2. Tap "Share" → "Add to Home Screen"
3. App appears on home screen

### Offline Support

- Service worker caches app shell
- IndexedDB stores all data
- AI models cached for offline use
- Works without internet (after initial load)

## 🔌 API & Services

### Database Service

```typescript
import { db } from '@services/database';

// Sessions
const sessions = await db.sessions.toArray();
await db.sessions.add(newSession);

// Messages
const messages = await db.messages
  .where('sessionId').equals(sessionId)
  .reverse()
  .toArray();

// Export
const backup = await db.exportToJSON();
```

### AI Inference Service

```typescript
import { aiService } from '@services/ai-inference';

// Generate text
async function* generateResponse(input: string) {
  yield* aiService.generateResponse(input, modelId, sessionId);
}

// Analyze image
const analysis = await aiService.analyzeImage(imageData, query);

// Process audio
const transcription = await aiService.analyzeAudio(audioData, query);
```

## 🧪 Testing

```bash
# Run unit tests
npm run test

# Run with UI
npm run test:ui

# E2E tests
npm run test:e2e
```

## 🚀 Deployment

### Vercel (Recommended)

```bash
# Install Vercel CLI
npm i -g vercel

# Deploy
vercel

# Production deployment
vercel --prod
```

### Netlify

```bash
# Build
npm run build

# Deploy dist/ folder to Netlify
```

### Self-Hosted

```bash
# Build
npm run build

# Serve dist/ with any static server
# Nginx example:
server {
  listen 80;
  root /path/to/dist;
  location / {
    try_files $uri $uri/ /index.html;
  }
}
```

## 🔐 Security

- **HTTPS Only** - Required for PWA and camera/mic access
- **Content Security Policy** - Prevents XSS attacks
- **No External Dependencies** - AI runs locally
- **IndexedDB Encryption** - Optional for sensitive data

## 🌍 Browser Support

| Browser | Minimum Version |
|---------|----------------|
| Chrome  | 90+            |
| Edge    | 90+            |
| Firefox | 88+            |
| Safari  | 14+            |
| Opera   | 76+            |

### Required APIs
- IndexedDB
- Service Workers
- Web Workers
- WebAssembly
- FileReader API
- MediaDevices (camera/microphone)

## 🤝 Contributing

See [CONTRIBUTING.md](../docs/CONTRIBUTING.md) for contribution guidelines.

## 📄 License

Apache License 2.0 - See [LICENSE](../LICENSE) for details.

## 📚 Related Documentation

- [Multi-Platform Architecture](../docs/MULTI_PLATFORM_ARCHITECTURE.md)
- [Android Setup](../docs/ANDROID_SETUP.md)
- [Automation System](../docs/AUTOMATION_SYSTEM.md)

## 🆘 Support

- **Issues**: [GitHub Issues](https://github.com/yourusername/Local-Multimodal-LLM-Ai-Chat/issues)
- **Discussions**: [GitHub Discussions](https://github.com/yourusername/Local-Multimodal-LLM-Ai-Chat/discussions)

---

Built with ❤️ for privacy-focused AI applications
