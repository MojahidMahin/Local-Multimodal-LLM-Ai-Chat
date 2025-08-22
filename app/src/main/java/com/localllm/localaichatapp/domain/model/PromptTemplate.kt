package com.localllm.localaichatapp.domain.model

data class PromptTemplate(
    val id: String,
    val name: String,
    val description: String,
    val template: String,
    val category: PromptCategory,
    val parameters: List<String> = emptyList(),
    val isBuiltIn: Boolean = false,
    val usageCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun formatTemplate(parameterValues: Map<String, String>): String {
        var formatted = template
        parameters.forEach { param ->
            val placeholder = "{$param}"
            val value = parameterValues[param] ?: placeholder
            formatted = formatted.replace(placeholder, value)
        }
        return formatted
    }
}

enum class PromptCategory(val displayName: String, val description: String) {
    SUMMARY("Summarization", "Summarize long text content"),
    REWRITE("Rewriting", "Rewrite text in different styles"),
    CODE_GEN("Code Generation", "Generate code snippets"),
    ANALYSIS("Analysis", "Analyze and explain content"),
    CREATIVE("Creative", "Creative writing and content"),
    FREEFORM("Freeform", "Custom prompts for any purpose")
}