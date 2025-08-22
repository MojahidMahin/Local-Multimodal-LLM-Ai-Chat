package com.localllm.localaichatapp.domain.model

data class ModelConfiguration(
    val temperature: Float = 0.7f,
    val maxTokens: Int = 1000,
    val topP: Float = 0.9f,
    val topK: Int = 40,
    val repeatPenalty: Float = 1.1f,
    val contextLength: Int = 2048,
    val systemPrompt: String = "You are a helpful AI assistant."
)