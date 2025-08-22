# Database Schema Documentation

## 📊 Overview

LocalAiChatApp uses Room database with a comprehensive schema supporting all AI gallery features.

## 🗂️ Tables

### 1. Models Table
```sql
CREATE TABLE models (
    id TEXT PRIMARY KEY NOT NULL,
    name TEXT NOT NULL,
    displayName TEXT NOT NULL,
    description TEXT NOT NULL,
    author TEXT NOT NULL,
    size INTEGER NOT NULL,
    downloadUrl TEXT NOT NULL,
    modelPath TEXT,
    isDownloaded INTEGER NOT NULL DEFAULT 0,
    isDownloading INTEGER NOT NULL DEFAULT 0,
    downloadProgress REAL NOT NULL DEFAULT 0,
    supportedTasks TEXT NOT NULL,  -- JSON array
    parameters TEXT,               -- JSON object
    createdAt INTEGER NOT NULL DEFAULT 0,
    updatedAt INTEGER NOT NULL DEFAULT 0
);
```

### 2. Chat Sessions Table
```sql
CREATE TABLE chat_sessions (
    id TEXT PRIMARY KEY NOT NULL,
    title TEXT NOT NULL,
    modelId TEXT NOT NULL,
    taskType TEXT NOT NULL,        -- CHAT, ASK_IMAGE, ASK_AUDIO, PROMPT_LAB
    createdAt INTEGER NOT NULL DEFAULT 0,
    updatedAt INTEGER NOT NULL DEFAULT 0,
    messageCount INTEGER NOT NULL DEFAULT 0,
    isBookmarked INTEGER NOT NULL DEFAULT 0,
    tags TEXT NOT NULL DEFAULT '[]', -- JSON array
    FOREIGN KEY(modelId) REFERENCES models(id) ON DELETE CASCADE
);
```

### 3. Chat Messages Table
```sql
CREATE TABLE chat_messages (
    id TEXT PRIMARY KEY NOT NULL,
    sessionId TEXT NOT NULL,
    content TEXT NOT NULL,
    sender INTEGER NOT NULL,       -- USER = 0, AI = 1, SYSTEM = 2
    timestamp INTEGER NOT NULL,
    messageType TEXT NOT NULL,     -- TEXT, IMAGE, AUDIO, BENCHMARK, LOADING, ERROR
    imageUri TEXT,
    audioUri TEXT,
    metadata TEXT,                 -- JSON object for additional data
    isStreaming INTEGER NOT NULL DEFAULT 0,
    tokenCount INTEGER,
    responseTimeMs INTEGER,
    FOREIGN KEY(sessionId) REFERENCES chat_sessions(id) ON DELETE CASCADE
);
```

### 4. Prompt Templates Table
```sql
CREATE TABLE prompt_templates (
    id TEXT PRIMARY KEY NOT NULL,
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    template TEXT NOT NULL,
    category TEXT NOT NULL,        -- SUMMARY, REWRITE, CODE_GEN, ANALYSIS, CREATIVE, FREEFORM
    parameters TEXT NOT NULL,      -- JSON array of parameter names
    isBuiltIn INTEGER NOT NULL DEFAULT 0,
    usageCount INTEGER NOT NULL DEFAULT 0,
    createdAt INTEGER NOT NULL DEFAULT 0,
    updatedAt INTEGER NOT NULL DEFAULT 0
);
```

### 5. Benchmarks Table
```sql
CREATE TABLE benchmarks (
    id TEXT PRIMARY KEY NOT NULL,
    modelId TEXT NOT NULL,
    sessionId TEXT NOT NULL,
    taskType TEXT NOT NULL,
    ttftMs INTEGER NOT NULL,       -- Time to First Token
    decodeSpeedTokensPerSecond REAL NOT NULL,
    totalLatencyMs INTEGER NOT NULL,
    inputTokenCount INTEGER NOT NULL,
    outputTokenCount INTEGER NOT NULL,
    memoryUsageMb REAL NOT NULL,
    cpuUsagePercent REAL NOT NULL,
    batteryLevel REAL,
    timestamp INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY(modelId) REFERENCES models(id) ON DELETE CASCADE,
    FOREIGN KEY(sessionId) REFERENCES chat_sessions(id) ON DELETE CASCADE
);
```

## 🔗 Relationships

### One-to-Many Relationships
- `models` → `chat_sessions`
- `chat_sessions` → `chat_messages`
- `models` → `benchmarks`
- `chat_sessions` → `benchmarks`

### Indices for Performance
```sql
-- Models
CREATE INDEX index_models_isDownloaded ON models(isDownloaded);

-- Chat Sessions
CREATE INDEX index_chat_sessions_taskType ON chat_sessions(taskType);
CREATE INDEX index_chat_sessions_modelId ON chat_sessions(modelId);

-- Chat Messages
CREATE INDEX index_chat_messages_sessionId ON chat_messages(sessionId);
CREATE INDEX index_chat_messages_timestamp ON chat_messages(timestamp);

-- Benchmarks
CREATE INDEX index_benchmarks_modelId ON benchmarks(modelId);
CREATE INDEX index_benchmarks_sessionId ON benchmarks(sessionId);
```

## 🔄 Migration Strategy

### Version 1 → Version 2
- Add new tables: `models`, `prompt_templates`, `benchmarks`
- Modify `chat_sessions`: Add `taskType`, `messageCount`, `isBookmarked`, `tags`
- Modify `chat_messages`: Add `audioUri`, `metadata`, `tokenCount`, `responseTimeMs`
- Create all necessary indices

## 📝 Data Types & Converters

### Type Converters
- **List\<String>** ↔ JSON array
- **ChatSender enum** ↔ String
- **Custom objects** ↔ JSON using Gson

### JSON Storage Examples
```json
// supportedTasks in models table
["CHAT", "ASK_IMAGE", "PROMPT_LAB"]

// parameters in models table
{
  "contextLength": 8192,
  "vocabularySize": 256000,
  "architecture": "GemmaForCausalLM"
}

// metadata in chat_messages table
{
  "analysisResult": "Image contains a cat",
  "transcription": "Hello world",
  "duration": 5000
}
```

## 🎯 Query Examples

### Common Queries
```kotlin
// Get all downloaded models for a specific task
SELECT * FROM models 
WHERE isDownloaded = 1 
AND supportedTasks LIKE '%CHAT%'

// Get recent chat sessions with message count
SELECT s.*, COUNT(m.id) as actualMessageCount
FROM chat_sessions s
LEFT JOIN chat_messages m ON s.id = m.sessionId
GROUP BY s.id
ORDER BY s.updatedAt DESC

// Get benchmark summary for a model
SELECT 
    AVG(ttftMs) as avgTTFT,
    AVG(decodeSpeedTokensPerSecond) as avgSpeed,
    AVG(totalLatencyMs) as avgLatency
FROM benchmarks 
WHERE modelId = ? AND taskType = ?
```

## 🔧 Database Configuration

### Room Setup
```kotlin
@Database(
    entities = [
        ChatSessionEntity::class,
        ChatMessageEntity::class,
        ModelEntity::class,
        PromptTemplateEntity::class,
        BenchmarkEntity::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class ChatDatabase : RoomDatabase()
```

### Features Enabled
- **Export Schema**: For version control
- **Foreign Key Constraints**: Data integrity
- **Migration Support**: Smooth upgrades
- **Type Converters**: Complex data handling