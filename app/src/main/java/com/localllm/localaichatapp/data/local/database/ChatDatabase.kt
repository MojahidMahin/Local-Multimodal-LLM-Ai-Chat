package com.localllm.localaichatapp.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import com.localllm.localaichatapp.data.local.database.dao.ChatDao
import com.localllm.localaichatapp.data.local.database.dao.ModelDao
import com.localllm.localaichatapp.data.local.database.dao.PromptTemplateDao
import com.localllm.localaichatapp.data.local.database.dao.BenchmarkDao
import com.localllm.localaichatapp.data.local.database.entity.ChatMessageEntity
import com.localllm.localaichatapp.data.local.database.entity.ChatSessionEntity
import com.localllm.localaichatapp.data.local.database.entity.ModelEntity
import com.localllm.localaichatapp.data.local.database.entity.PromptTemplateEntity
import com.localllm.localaichatapp.data.local.database.entity.BenchmarkEntity
import com.localllm.localaichatapp.data.local.database.converter.Converters

@Database(
    entities = [
        ChatSessionEntity::class, 
        ChatMessageEntity::class,
        ModelEntity::class,
        PromptTemplateEntity::class,
        BenchmarkEntity::class
    ],
    version = 3,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun modelDao(): ModelDao
    abstract fun promptTemplateDao(): PromptTemplateDao
    abstract fun benchmarkDao(): BenchmarkDao
    
    companion object {
        @Volatile
        private var INSTANCE: ChatDatabase? = null
        
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create models table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `models` (
                        `id` TEXT NOT NULL,
                        `name` TEXT NOT NULL,
                        `displayName` TEXT NOT NULL,
                        `description` TEXT NOT NULL,
                        `author` TEXT NOT NULL,
                        `size` INTEGER NOT NULL,
                        `downloadUrl` TEXT NOT NULL,
                        `modelPath` TEXT,
                        `isDownloaded` INTEGER NOT NULL DEFAULT 0,
                        `isDownloading` INTEGER NOT NULL DEFAULT 0,
                        `downloadProgress` REAL NOT NULL DEFAULT 0,
                        `supportedTasks` TEXT NOT NULL,
                        `parameters` TEXT,
                        `createdAt` INTEGER NOT NULL DEFAULT 0,
                        `updatedAt` INTEGER NOT NULL DEFAULT 0,
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())
                
                // Create prompt_templates table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `prompt_templates` (
                        `id` TEXT NOT NULL,
                        `name` TEXT NOT NULL,
                        `description` TEXT NOT NULL,
                        `template` TEXT NOT NULL,
                        `category` TEXT NOT NULL,
                        `parameters` TEXT NOT NULL,
                        `isBuiltIn` INTEGER NOT NULL DEFAULT 0,
                        `usageCount` INTEGER NOT NULL DEFAULT 0,
                        `createdAt` INTEGER NOT NULL DEFAULT 0,
                        `updatedAt` INTEGER NOT NULL DEFAULT 0,
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())
                
                // Create benchmarks table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `benchmarks` (
                        `id` TEXT NOT NULL,
                        `modelId` TEXT NOT NULL,
                        `sessionId` TEXT NOT NULL,
                        `taskType` TEXT NOT NULL,
                        `ttftMs` INTEGER NOT NULL,
                        `decodeSpeedTokensPerSecond` REAL NOT NULL,
                        `totalLatencyMs` INTEGER NOT NULL,
                        `inputTokenCount` INTEGER NOT NULL,
                        `outputTokenCount` INTEGER NOT NULL,
                        `memoryUsageMb` REAL NOT NULL,
                        `cpuUsagePercent` REAL NOT NULL,
                        `batteryLevel` REAL,
                        `timestamp` INTEGER NOT NULL DEFAULT 0,
                        PRIMARY KEY(`id`),
                        FOREIGN KEY(`modelId`) REFERENCES `models`(`id`) ON DELETE CASCADE,
                        FOREIGN KEY(`sessionId`) REFERENCES `chat_sessions`(`id`) ON DELETE CASCADE
                    )
                """.trimIndent())
                
                // Update existing chat_sessions table
                database.execSQL("ALTER TABLE chat_sessions ADD COLUMN taskType TEXT NOT NULL DEFAULT 'CHAT'")
                database.execSQL("ALTER TABLE chat_sessions ADD COLUMN messageCount INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE chat_sessions ADD COLUMN isBookmarked INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE chat_sessions ADD COLUMN tags TEXT NOT NULL DEFAULT '[]'")
                
                // Update existing chat_messages table
                database.execSQL("ALTER TABLE chat_messages ADD COLUMN audioUri TEXT")
                database.execSQL("ALTER TABLE chat_messages ADD COLUMN metadata TEXT")
                database.execSQL("ALTER TABLE chat_messages ADD COLUMN tokenCount INTEGER")
                database.execSQL("ALTER TABLE chat_messages ADD COLUMN responseTimeMs INTEGER")
                
                // Create indices
                database.execSQL("CREATE INDEX IF NOT EXISTS index_models_isDownloaded ON models(isDownloaded)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_benchmarks_modelId ON benchmarks(modelId)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_benchmarks_sessionId ON benchmarks(sessionId)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_chat_sessions_taskType ON chat_sessions(taskType)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_chat_messages_timestamp ON chat_messages(timestamp)")
            }
        }
        
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add missing columns to models table
                database.execSQL("ALTER TABLE models ADD COLUMN status TEXT NOT NULL DEFAULT 'AVAILABLE'")
                database.execSQL("ALTER TABLE models ADD COLUMN isPrimary INTEGER NOT NULL DEFAULT 0")
            }
        }
        
        fun getDatabase(context: Context): ChatDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChatDatabase::class.java,
                    "chat_database"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .fallbackToDestructiveMigration() // Only for development
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}