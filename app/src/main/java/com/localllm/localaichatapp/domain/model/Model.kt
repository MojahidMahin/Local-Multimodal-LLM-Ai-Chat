package com.localllm.localaichatapp.domain.model

data class Model(
    val id: String,
    val name: String,
    val displayName: String,
    val description: String,
    val author: String,
    val size: Long,
    val downloadUrl: String,
    val modelPath: String? = null,
    val isDownloaded: Boolean = false,
    val isDownloading: Boolean = false,
    val downloadProgress: Float = 0f,
    val status: ModelStatus = ModelStatus.AVAILABLE,
    val isPrimary: Boolean = false,
    val supportedTasks: List<TaskType> = emptyList(),
    val parameters: ModelParameters? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val formattedSize: String
        get() = formatBytes(size)
    
    val isAvailable: Boolean
        get() = isDownloaded && !modelPath.isNullOrEmpty()
}

data class ModelParameters(
    val contextLength: Int,
    val vocabularySize: Int,
    val layerCount: Int,
    val hiddenSize: Int,
    val attentionHeads: Int,
    val intermediateSize: Int?,
    val architecture: String,
    val quantization: String?,
    val license: String?
)

private fun formatBytes(bytes: Long): String {
    val kb = 1024
    val mb = kb * 1024
    val gb = mb * 1024
    
    return when {
        bytes >= gb -> String.format("%.2f GB", bytes.toDouble() / gb)
        bytes >= mb -> String.format("%.2f MB", bytes.toDouble() / mb)
        bytes >= kb -> String.format("%.2f KB", bytes.toDouble() / kb)
        else -> "$bytes B"
    }
}