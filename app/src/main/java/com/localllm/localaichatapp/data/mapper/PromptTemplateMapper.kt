package com.localllm.localaichatapp.data.mapper

import com.localllm.localaichatapp.data.local.database.entity.PromptTemplateEntity
import com.localllm.localaichatapp.domain.model.PromptTemplate
import com.localllm.localaichatapp.domain.model.PromptCategory

class PromptTemplateMapper {
    
    fun toEntity(domain: PromptTemplate): PromptTemplateEntity {
        return PromptTemplateEntity(
            id = domain.id,
            name = domain.name,
            description = domain.description,
            template = domain.template,
            category = domain.category.name,
            parameters = domain.parameters,
            isBuiltIn = domain.isBuiltIn,
            usageCount = domain.usageCount,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }
    
    fun toDomain(entity: PromptTemplateEntity): PromptTemplate {
        val category = try {
            PromptCategory.valueOf(entity.category)
        } catch (e: Exception) {
            PromptCategory.FREEFORM
        }
        
        return PromptTemplate(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            template = entity.template,
            category = category,
            parameters = entity.parameters,
            isBuiltIn = entity.isBuiltIn,
            usageCount = entity.usageCount,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
    
    fun toDomainList(entities: List<PromptTemplateEntity>): List<PromptTemplate> {
        return entities.map { toDomain(it) }
    }
    
    fun toEntityList(domains: List<PromptTemplate>): List<PromptTemplateEntity> {
        return domains.map { toEntity(it) }
    }
}