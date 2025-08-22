package com.localllm.localaichatapp.di

import android.content.Context
import com.google.gson.Gson
import com.localllm.localaichatapp.data.remote.ModelApiService
import com.localllm.localaichatapp.data.remote.ModelDownloadManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideModelApiService(): ModelApiService {
        return ModelApiService()
    }
    
    @Provides
    @Singleton
    fun provideModelDownloadManager(
        @ApplicationContext context: Context
    ): ModelDownloadManager {
        return ModelDownloadManager(context)
    }
    
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }
    
    @Provides
    @Singleton
    fun provideApplicationContext(@ApplicationContext context: Context): Context {
        return context
    }
}