package com.localllm.localaichatapp.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.localllm.localaichatapp.data.local.database.converter.Converters

/**
 * Database entity for storing workflows.
 */
@Entity(tableName = "workflows")
@TypeConverters(Converters::class)
data class WorkflowEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String?,
    val enabled: Boolean,
    val triggerType: String,
    val triggerConfig: String, // JSON serialized TriggerConfig
    val createdAt: Long,
    val updatedAt: Long,
    val lastExecutedAt: Long?,
    val executionCount: Int,
    val tags: String, // JSON array of strings
    val isTemplate: Boolean
)
