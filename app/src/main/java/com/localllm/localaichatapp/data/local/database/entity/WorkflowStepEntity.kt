package com.localllm.localaichatapp.data.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Database entity for storing workflow steps (conditions and actions).
 */
@Entity(
    tableName = "workflow_steps",
    foreignKeys = [
        ForeignKey(
            entity = WorkflowEntity::class,
            parentColumns = ["id"],
            childColumns = ["workflowId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("workflowId")]
)
data class WorkflowStepEntity(
    @PrimaryKey
    val id: String,
    val workflowId: String,
    val order: Int,
    val stepType: String, // CONDITION or ACTION
    val type: String, // ConditionType or ActionType enum name
    val config: String, // JSON serialized config
    val operator: String? = null // For conditions: AND, OR, NOT
)
