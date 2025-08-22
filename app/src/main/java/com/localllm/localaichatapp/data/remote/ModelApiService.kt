package com.localllm.localaichatapp.data.remote

import com.localllm.localaichatapp.domain.model.ModelParameters
import javax.inject.Inject
import javax.inject.Singleton

data class ApiModel(
    val id: String,
    val name: String,
    val displayName: String?,
    val description: String?,
    val author: String?,
    val size: Long,
    val downloadUrl: String,
    val supportedTasks: List<String>?,
    val parameters: ModelParameters?
)

@Singleton
class ModelApiService @Inject constructor() {
    
    suspend fun getAvailableModels(): List<ApiModel> {
        // This would typically make a network call to fetch available models
        // For now, return some default models
        return listOf(
            ApiModel(
                id = "gemma-2b",
                name = "Gemma 2B",
                displayName = "Google Gemma 2B",
                description = "Small and efficient model for basic text generation tasks",
                author = "Google",
                size = 1_500_000_000L, // 1.5GB
                downloadUrl = "https://huggingface.co/google/gemma-2b-it/resolve/main/model.safetensors",
                supportedTasks = listOf("CHAT", "PROMPT_LAB"),
                parameters = ModelParameters(
                    contextLength = 8192,
                    vocabularySize = 256000,
                    layerCount = 18,
                    hiddenSize = 2048,
                    attentionHeads = 8,
                    intermediateSize = 16384,
                    architecture = "GemmaForCausalLM",
                    quantization = "int4",
                    license = "Gemma Terms of Use"
                )
            ),
            ApiModel(
                id = "gemma-7b",
                name = "Gemma 7B",
                displayName = "Google Gemma 7B",
                description = "Larger model with better performance for complex tasks",
                author = "Google",
                size = 4_200_000_000L, // 4.2GB
                downloadUrl = "https://huggingface.co/google/gemma-7b-it/resolve/main/model.safetensors",
                supportedTasks = listOf("CHAT", "ASK_IMAGE", "PROMPT_LAB"),
                parameters = ModelParameters(
                    contextLength = 8192,
                    vocabularySize = 256000,
                    layerCount = 28,
                    hiddenSize = 3072,
                    attentionHeads = 16,
                    intermediateSize = 24576,
                    architecture = "GemmaForCausalLM",
                    quantization = "int4",
                    license = "Gemma Terms of Use"
                )
            ),
            ApiModel(
                id = "phi-3-mini",
                name = "Phi-3 Mini",
                displayName = "Microsoft Phi-3 Mini",
                description = "Microsoft's small but powerful model optimized for mobile devices",
                author = "Microsoft",
                size = 2_100_000_000L, // 2.1GB
                downloadUrl = "https://huggingface.co/microsoft/Phi-3-mini-4k-instruct/resolve/main/model.safetensors",
                supportedTasks = listOf("CHAT", "PROMPT_LAB"),
                parameters = ModelParameters(
                    contextLength = 4096,
                    vocabularySize = 32064,
                    layerCount = 32,
                    hiddenSize = 3072,
                    attentionHeads = 32,
                    intermediateSize = 8192,
                    architecture = "PhiForCausalLM",
                    quantization = "int4",
                    license = "MIT"
                )
            ),
            ApiModel(
                id = "llama-3.2-1b",
                name = "Llama 3.2 1B",
                displayName = "Meta Llama 3.2 1B",
                description = "Compact Llama model optimized for edge deployment",
                author = "Meta",
                size = 1_200_000_000L, // 1.2GB
                downloadUrl = "https://huggingface.co/meta-llama/Llama-3.2-1B-Instruct/resolve/main/model.safetensors",
                supportedTasks = listOf("CHAT", "ASK_AUDIO", "PROMPT_LAB"),
                parameters = ModelParameters(
                    contextLength = 131072,
                    vocabularySize = 128256,
                    layerCount = 16,
                    hiddenSize = 2048,
                    attentionHeads = 32,
                    intermediateSize = 8192,
                    architecture = "LlamaForCausalLM",
                    quantization = "int4",
                    license = "Llama 3.2 Community License"
                )
            )
        )
    }
}