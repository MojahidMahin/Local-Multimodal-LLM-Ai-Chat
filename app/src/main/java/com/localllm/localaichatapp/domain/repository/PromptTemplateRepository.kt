package com.localllm.localaichatapp.domain.repository

import com.localllm.localaichatapp.domain.model.PromptTemplate
import com.localllm.localaichatapp.domain.model.PromptCategory
import kotlinx.coroutines.flow.Flow

interface PromptTemplateRepository {
    fun getAllTemplates(): Flow<List<PromptTemplate>>
    fun getTemplatesByCategory(category: PromptCategory): Flow<List<PromptTemplate>>
    fun getBuiltInTemplates(): Flow<List<PromptTemplate>>
    fun getCustomTemplates(): Flow<List<PromptTemplate>>
    fun getMostUsedTemplates(limit: Int = 10): Flow<List<PromptTemplate>>
    fun getCategories(): Flow<List<PromptCategory>>
    suspend fun getTemplateById(templateId: String): PromptTemplate?
    suspend fun insertTemplate(template: PromptTemplate)
    suspend fun insertTemplates(templates: List<PromptTemplate>)
    suspend fun updateTemplate(template: PromptTemplate)
    suspend fun deleteTemplate(templateId: String)
    suspend fun incrementUsageCount(templateId: String)
    suspend fun initializeBuiltInTemplates()
}