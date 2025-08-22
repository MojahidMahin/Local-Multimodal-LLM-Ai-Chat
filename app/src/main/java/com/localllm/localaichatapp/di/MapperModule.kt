package com.localllm.localaichatapp.di

import com.localllm.localaichatapp.data.mapper.ModelMapper
import com.localllm.localaichatapp.data.mapper.PromptTemplateMapper
import com.localllm.localaichatapp.data.mapper.BenchmarkMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MapperModule {
    
    @Provides
    @Singleton
    fun provideModelMapper(): ModelMapper {
        return ModelMapper()
    }
    
    @Provides
    @Singleton
    fun providePromptTemplateMapper(): PromptTemplateMapper {
        return PromptTemplateMapper()
    }
    
    @Provides
    @Singleton
    fun provideBenchmarkMapper(): BenchmarkMapper {
        return BenchmarkMapper()
    }
}