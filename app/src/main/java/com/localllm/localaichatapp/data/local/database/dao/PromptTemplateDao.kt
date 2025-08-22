package com.localllm.localaichatapp.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.localllm.localaichatapp.data.local.database.entity.PromptTemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PromptTemplateDao {
    
    @Query("SELECT * FROM prompt_templates ORDER BY name ASC")
    fun getAllTemplates(): Flow<List<PromptTemplateEntity>>
    
    @Query("SELECT * FROM prompt_templates WHERE category = :category ORDER BY name ASC")
    fun getTemplatesByCategory(category: String): Flow<List<PromptTemplateEntity>>
    
    @Query("SELECT * FROM prompt_templates WHERE isBuiltIn = 1 ORDER BY name ASC")
    fun getBuiltInTemplates(): Flow<List<PromptTemplateEntity>>
    
    @Query("SELECT * FROM prompt_templates WHERE isBuiltIn = 0 ORDER BY updatedAt DESC")
    fun getCustomTemplates(): Flow<List<PromptTemplateEntity>>
    
    @Query("SELECT * FROM prompt_templates WHERE id = :templateId")
    suspend fun getTemplateById(templateId: String): PromptTemplateEntity?
    
    @Query("SELECT * FROM prompt_templates ORDER BY usageCount DESC LIMIT :limit")
    fun getMostUsedTemplates(limit: Int = 10): Flow<List<PromptTemplateEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: PromptTemplateEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplates(templates: List<PromptTemplateEntity>)
    
    @Update
    suspend fun updateTemplate(template: PromptTemplateEntity)
    
    @Query("UPDATE prompt_templates SET usageCount = usageCount + 1, updatedAt = :updatedAt WHERE id = :templateId")
    suspend fun incrementUsageCount(templateId: String, updatedAt: Long = System.currentTimeMillis())
    
    @Delete
    suspend fun deleteTemplate(template: PromptTemplateEntity)
    
    @Query("DELETE FROM prompt_templates WHERE id = :templateId AND isBuiltIn = 0")
    suspend fun deleteCustomTemplate(templateId: String)
    
    @Query("SELECT DISTINCT category FROM prompt_templates ORDER BY category ASC")
    fun getCategories(): Flow<List<String>>
}