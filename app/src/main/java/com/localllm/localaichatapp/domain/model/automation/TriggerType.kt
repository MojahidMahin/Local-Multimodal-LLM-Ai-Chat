package com.localllm.localaichatapp.domain.model.automation

/**
 * Types of triggers that can initiate workflow execution.
 * Inspired by Zapier's trigger system.
 */
enum class TriggerType {
    // App Triggers
    MESSAGE_SENT,
    MESSAGE_RECEIVED,
    SESSION_CREATED,
    SESSION_DELETED,
    SESSION_UPDATED,
    IMAGE_UPLOADED,
    AUDIO_UPLOADED,

    // Model Triggers
    MODEL_DOWNLOADED,
    MODEL_DELETED,
    MODEL_SWITCHED,
    MODEL_DOWNLOAD_FAILED,

    // System Triggers
    APP_LAUNCHED,
    APP_BACKGROUNDED,
    LOW_STORAGE,
    SCHEDULED,
    TIME_BASED,

    // Performance Triggers
    HIGH_MEMORY_USAGE,
    SLOW_RESPONSE,
    BENCHMARK_COMPLETED,

    // User Interaction Triggers
    BOOKMARK_ADDED,
    SEARCH_PERFORMED,
    SETTINGS_CHANGED,

    // Data Triggers
    DATABASE_BACKUP_NEEDED,
    DATA_EXPORT_REQUESTED,
    STORAGE_THRESHOLD_REACHED
}
