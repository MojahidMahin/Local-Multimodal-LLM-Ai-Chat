package com.localllm.localaichatapp.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prompt_templates")
data class PromptTemplateEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val template: String,
    val category: String, // SUMMARY, REWRITE, CODE_GEN, FREEFORM
    val parameters: List<String> = emptyList(), // Parameter names for template
    val isBuiltIn: Boolean = false,
    val usageCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)