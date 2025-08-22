package com.localllm.localaichatapp.data.repository

import com.localllm.localaichatapp.data.local.database.dao.PromptTemplateDao
import com.localllm.localaichatapp.data.mapper.PromptTemplateMapper
import com.localllm.localaichatapp.domain.model.PromptTemplate
import com.localllm.localaichatapp.domain.model.PromptCategory
import com.localllm.localaichatapp.domain.repository.PromptTemplateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PromptTemplateRepositoryImpl @Inject constructor(
    private val promptTemplateDao: PromptTemplateDao,
    private val promptTemplateMapper: PromptTemplateMapper
) : PromptTemplateRepository {
    
    override fun getAllTemplates(): Flow<List<PromptTemplate>> {
        return promptTemplateDao.getAllTemplates().map { entities ->
            promptTemplateMapper.toDomainList(entities)
        }
    }
    
    override fun getTemplatesByCategory(category: PromptCategory): Flow<List<PromptTemplate>> {
        return promptTemplateDao.getTemplatesByCategory(category.name).map { entities ->
            promptTemplateMapper.toDomainList(entities)
        }
    }
    
    override fun getBuiltInTemplates(): Flow<List<PromptTemplate>> {
        return promptTemplateDao.getBuiltInTemplates().map { entities ->
            promptTemplateMapper.toDomainList(entities)
        }
    }
    
    override fun getCustomTemplates(): Flow<List<PromptTemplate>> {
        return promptTemplateDao.getCustomTemplates().map { entities ->
            promptTemplateMapper.toDomainList(entities)
        }
    }
    
    override fun getMostUsedTemplates(limit: Int): Flow<List<PromptTemplate>> {
        return promptTemplateDao.getMostUsedTemplates(limit).map { entities ->
            promptTemplateMapper.toDomainList(entities)
        }
    }
    
    override fun getCategories(): Flow<List<PromptCategory>> {
        return promptTemplateDao.getCategories().map { categoryNames ->
            categoryNames.mapNotNull { name ->
                try {
                    PromptCategory.valueOf(name)
                } catch (e: Exception) {
                    null
                }
            }
        }
    }
    
    override suspend fun getTemplateById(templateId: String): PromptTemplate? {
        return promptTemplateDao.getTemplateById(templateId)?.let { entity ->
            promptTemplateMapper.toDomain(entity)
        }
    }
    
    override suspend fun insertTemplate(template: PromptTemplate) {
        promptTemplateDao.insertTemplate(promptTemplateMapper.toEntity(template))
    }
    
    override suspend fun insertTemplates(templates: List<PromptTemplate>) {
        promptTemplateDao.insertTemplates(promptTemplateMapper.toEntityList(templates))
    }
    
    override suspend fun updateTemplate(template: PromptTemplate) {
        promptTemplateDao.updateTemplate(promptTemplateMapper.toEntity(template))
    }
    
    override suspend fun deleteTemplate(templateId: String) {
        promptTemplateDao.deleteCustomTemplate(templateId)
    }
    
    override suspend fun incrementUsageCount(templateId: String) {
        promptTemplateDao.incrementUsageCount(templateId)
    }
    
    override suspend fun initializeBuiltInTemplates() {
        val builtInTemplates = createBuiltInTemplates()
        insertTemplates(builtInTemplates)
    }
    
    private fun createBuiltInTemplates(): List<PromptTemplate> {
        return listOf(
            // Summarization Templates
            PromptTemplate(
                id = UUID.randomUUID().toString(),
                name = "Summarize Article",
                description = "Summarize a long article or document into key points",
                template = "Please provide a concise summary of the following text, highlighting the main points and key takeaways:\n\n{text}",
                category = PromptCategory.SUMMARY,
                parameters = listOf("text"),
                isBuiltIn = true
            ),
            PromptTemplate(
                id = UUID.randomUUID().toString(),
                name = "Meeting Summary",
                description = "Summarize meeting notes with action items",
                template = "Summarize the following meeting notes and extract action items:\n\nMeeting: {meeting_title}\nNotes: {notes}\n\nProvide:\n1. Key discussion points\n2. Decisions made\n3. Action items with owners",
                category = PromptCategory.SUMMARY,
                parameters = listOf("meeting_title", "notes"),
                isBuiltIn = true
            ),
            
            // Rewriting Templates
            PromptTemplate(
                id = UUID.randomUUID().toString(),
                name = "Professional Email",
                description = "Rewrite text in a professional email format",
                template = "Rewrite the following text as a professional email:\n\n{content}\n\nMake it formal, polite, and clear.",
                category = PromptCategory.REWRITE,
                parameters = listOf("content"),
                isBuiltIn = true
            ),
            PromptTemplate(
                id = UUID.randomUUID().toString(),
                name = "Simplify Language",
                description = "Simplify complex text for easier understanding",
                template = "Simplify the following text to make it easier to understand, using plain language:\n\n{complex_text}",
                category = PromptCategory.REWRITE,
                parameters = listOf("complex_text"),
                isBuiltIn = true
            ),
            
            // Code Generation Templates
            PromptTemplate(
                id = UUID.randomUUID().toString(),
                name = "Function Generator",
                description = "Generate a function based on requirements",
                template = "Write a {language} function that:\n- Name: {function_name}\n- Purpose: {purpose}\n- Parameters: {parameters}\n- Return type: {return_type}\n\nInclude proper documentation and error handling.",
                category = PromptCategory.CODE_GEN,
                parameters = listOf("language", "function_name", "purpose", "parameters", "return_type"),
                isBuiltIn = true
            ),
            PromptTemplate(
                id = UUID.randomUUID().toString(),
                name = "Code Review",
                description = "Review code and suggest improvements",
                template = "Please review the following {language} code and provide feedback on:\n1. Code quality and best practices\n2. Potential bugs or issues\n3. Performance improvements\n4. Readability and maintainability\n\nCode:\n{code}",
                category = PromptCategory.CODE_GEN,
                parameters = listOf("language", "code"),
                isBuiltIn = true
            ),
            
            // Analysis Templates
            PromptTemplate(
                id = UUID.randomUUID().toString(),
                name = "Pros and Cons",
                description = "Analyze pros and cons of a topic or decision",
                template = "Analyze the following topic and provide a balanced view of pros and cons:\n\nTopic: {topic}\n\nPros:\n- \n\nCons:\n- \n\nConclusion:",
                category = PromptCategory.ANALYSIS,
                parameters = listOf("topic"),
                isBuiltIn = true
            ),
            PromptTemplate(
                id = UUID.randomUUID().toString(),
                name = "SWOT Analysis",
                description = "Perform a SWOT analysis on a business or project",
                template = "Perform a SWOT analysis for: {subject}\n\nStrengths:\n- \n\nWeaknesses:\n- \n\nOpportunities:\n- \n\nThreats:\n- ",
                category = PromptCategory.ANALYSIS,
                parameters = listOf("subject"),
                isBuiltIn = true
            ),
            
            // Creative Templates
            PromptTemplate(
                id = UUID.randomUUID().toString(),
                name = "Story Generator",
                description = "Generate a creative story based on prompts",
                template = "Write a {genre} story with the following elements:\n- Setting: {setting}\n- Main character: {character}\n- Conflict: {conflict}\n- Tone: {tone}\n\nMake it engaging and well-structured.",
                category = PromptCategory.CREATIVE,
                parameters = listOf("genre", "setting", "character", "conflict", "tone"),
                isBuiltIn = true
            ),
            PromptTemplate(
                id = UUID.randomUUID().toString(),
                name = "Marketing Copy",
                description = "Create compelling marketing copy",
                template = "Create compelling marketing copy for:\n\nProduct/Service: {product}\nTarget audience: {audience}\nKey benefits: {benefits}\nTone: {tone}\n\nInclude a catchy headline and call-to-action.",
                category = PromptCategory.CREATIVE,
                parameters = listOf("product", "audience", "benefits", "tone"),
                isBuiltIn = true
            )
        )
    }
}