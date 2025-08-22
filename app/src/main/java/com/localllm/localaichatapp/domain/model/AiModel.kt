package com.localllm.localaichatapp.domain.model

data class AiModel(
    val id: String,
    val name: String,
    val description: String,
    val isDownloaded: Boolean = false,
    val downloadProgress: Float = 0f,
    val modelSize: Long = 0L,
    val isInitialized: Boolean = false,
    val supportedFeatures: Set<ModelFeature> = emptySet()
)

enum class ModelFeature {
    TEXT_GENERATION,
    IMAGE_UNDERSTANDING,
    AUDIO_INPUT,
    STREAMING_RESPONSE
}

