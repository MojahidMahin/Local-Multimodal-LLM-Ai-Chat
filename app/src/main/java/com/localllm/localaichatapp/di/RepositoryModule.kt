package com.localllm.localaichatapp.di

import com.localllm.localaichatapp.data.repository.ChatRepositoryImpl
import com.localllm.localaichatapp.data.repository.ModelRepositoryImpl
import com.localllm.localaichatapp.data.repository.PromptTemplateRepositoryImpl
import com.localllm.localaichatapp.data.repository.BenchmarkRepositoryImpl
import com.localllm.localaichatapp.data.repository.AiInferenceRepositoryImpl
import com.localllm.localaichatapp.domain.repository.ChatRepository
import com.localllm.localaichatapp.domain.repository.ModelRepository
import com.localllm.localaichatapp.domain.repository.PromptTemplateRepository
import com.localllm.localaichatapp.domain.repository.BenchmarkRepository
import com.localllm.localaichatapp.domain.repository.AiInferenceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindChatRepository(
        chatRepositoryImpl: ChatRepositoryImpl
    ): ChatRepository
    
    @Binds
    @Singleton
    abstract fun bindModelRepository(
        modelRepositoryImpl: ModelRepositoryImpl
    ): ModelRepository
    
    @Binds
    @Singleton
    abstract fun bindPromptTemplateRepository(
        promptTemplateRepositoryImpl: PromptTemplateRepositoryImpl
    ): PromptTemplateRepository
    
    @Binds
    @Singleton
    abstract fun bindBenchmarkRepository(
        benchmarkRepositoryImpl: BenchmarkRepositoryImpl
    ): BenchmarkRepository
    
    @Binds
    @Singleton
    abstract fun bindAiInferenceRepository(
        aiInferenceRepositoryImpl: AiInferenceRepositoryImpl
    ): AiInferenceRepository
}