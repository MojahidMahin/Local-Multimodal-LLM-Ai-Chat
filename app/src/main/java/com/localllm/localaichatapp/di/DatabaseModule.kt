package com.localllm.localaichatapp.di

import android.content.Context
import androidx.room.Room
import com.localllm.localaichatapp.data.local.database.ChatDatabase
import com.localllm.localaichatapp.data.local.database.dao.ChatDao
import com.localllm.localaichatapp.data.local.database.dao.ModelDao
import com.localllm.localaichatapp.data.local.database.dao.PromptTemplateDao
import com.localllm.localaichatapp.data.local.database.dao.BenchmarkDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideChatDatabase(@ApplicationContext context: Context): ChatDatabase {
        return ChatDatabase.getDatabase(context)
    }
    
    @Provides
    fun provideChatDao(database: ChatDatabase): ChatDao {
        return database.chatDao()
    }
    
    @Provides
    fun provideModelDao(database: ChatDatabase): ModelDao {
        return database.modelDao()
    }
    
    @Provides
    fun providePromptTemplateDao(database: ChatDatabase): PromptTemplateDao {
        return database.promptTemplateDao()
    }
    
    @Provides
    fun provideBenchmarkDao(database: ChatDatabase): BenchmarkDao {
        return database.benchmarkDao()
    }
}