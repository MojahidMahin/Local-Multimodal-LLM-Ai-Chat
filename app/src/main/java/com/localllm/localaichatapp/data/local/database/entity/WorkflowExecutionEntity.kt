package com.localllm.localaichatapp.data.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.localllm.localaichatapp.data.local.database.converter.Converters

/**
 * Database entity for storing workflow execution history.
 */
@Entity(
    tableName = "workflow_executions",
    foreignKeys = [
        ForeignKey(
            entity = WorkflowEntity::class,
            parentColumns = ["id"],
            childColumns = ["workflowId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("workflowId"), Index("startedAt")]
)
@TypeConverters(Converters::class)
data class WorkflowExecutionEntity(
    @PrimaryKey
    val id: String,
    val workflowId: String,
    val status: String, // WorkflowStatus enum name
    val startedAt: Long,
    val completedAt: Long?,
    val duration: Long?,
    val error: String?,
    val logs: String, // JSON array of WorkflowLog
    val context: String // JSON serialized WorkflowContext
)
