# Architecture Documentation

## 🏗️ Clean Architecture Overview

LocalAiChatApp follows Clean Architecture principles with clear separation between layers:

```
┌─────────────────────────────────────────────┐
│                Presentation                 │
│           (UI, ViewModels)                  │
├─────────────────────────────────────────────┤
│                 Domain                      │
│        (Business Logic, Entities)           │
├─────────────────────────────────────────────┤
│                  Data                       │
│      (Repository Impl, Database, API)       │
└─────────────────────────────────────────────┘
```

## 📊 Database Schema

### Entity Relationships
```
Models ──┐
         ├─── ChatSessions ──── ChatMessages
         │                 └─── Benchmarks
         └─── Benchmarks
         
PromptTemplates (standalone)
```

### Tables
- **`models`** - AI model information and download status
- **`chat_sessions`** - Conversation sessions with metadata
- **`chat_messages`** - Individual messages with rich content
- **`prompt_templates`** - Reusable prompt templates
- **`benchmarks`** - Performance metrics and analytics

## 🔄 Data Flow

```
UI ──→ ViewModel ──→ UseCase ──→ Repository ──→ Database/API
   ←──            ←──         ←──           ←──
```

## 🎯 Core Components

### Domain Models
- `TaskType` - Chat, Ask Image, Ask Audio, Prompt Lab
- `Model` - AI model with capabilities and status
- `ChatSession` - Conversation with metadata
- `ChatMessage` - Sealed class hierarchy for different message types
- `PromptTemplate` - Reusable prompt with parameters
- `Benchmark` - Performance metrics

### Repository Pattern
- **Interfaces** in domain layer
- **Implementations** in data layer
- **Reactive data flow** using Kotlin Flow
- **Error handling** with Result wrapper

### Dependency Injection
- **Hilt modules** for clean dependency management
- **Singleton repositories** for data consistency
- **Context-aware** components where needed

## 🚀 Key Features

### Multi-Task AI Processing
- **Chat**: Multi-turn conversations
- **Ask Image**: Image analysis and understanding
- **Ask Audio**: Audio processing and transcription
- **Prompt Lab**: Template-based single-turn interactions

### Performance Monitoring
- Real-time benchmarking
- Memory and CPU usage tracking
- Token processing metrics
- Battery usage monitoring

### Model Management
- Model discovery and download
- Progress tracking
- Initialization and loading
- Multi-model support

## 🔧 Technology Stack

- **UI**: Jetpack Compose with Material 3
- **Database**: Room with migrations
- **DI**: Hilt
- **Async**: Kotlin Coroutines + Flow
- **Architecture**: MVVM + Clean Architecture
- **AI**: Google AI Edge (mock implementation ready)