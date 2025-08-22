package com.localllm.localaichatapp.domain.model

enum class TaskType(val displayName: String, val description: String) {
    CHAT("AI Chat", "Multi-turn conversational AI"),
    ASK_IMAGE("Ask Image", "Upload images and ask questions about them"),
    ASK_AUDIO("Ask Audio", "Process and analyze audio inputs"),
    PROMPT_LAB("Prompt Lab", "Single-turn AI interactions with templates")
}