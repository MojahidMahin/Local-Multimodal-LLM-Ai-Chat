package com.localllm.localaichatapp.domain.model.automation

/**
 * Types of actions that can be executed in workflows.
 * Inspired by Zapier's action system.
 */
enum class ActionType {
    // Notification Actions
    SHOW_NOTIFICATION,
    SHOW_TOAST,
    VIBRATE_DEVICE,
    PLAY_SOUND,

    // Data Actions
    SAVE_CONVERSATION,
    BACKUP_DATABASE,
    DELETE_OLD_SESSIONS,
    ARCHIVE_SESSION,
    EXPORT_DATA,
    CLEAR_CACHE,

    // Model Actions
    DOWNLOAD_MODEL,
    SWITCH_MODEL,
    DELETE_MODEL,
    OPTIMIZE_MODEL,

    // Integration Actions
    SHARE_TEXT,
    COPY_TO_CLIPBOARD,
    SAVE_TO_FILE,
    SEND_TO_APP,

    // AI Actions
    GENERATE_SUMMARY,
    ANALYZE_SENTIMENT,
    EXTRACT_KEYWORDS,
    TRANSLATE_TEXT,
    GENERATE_RESPONSE,

    // System Actions
    OPEN_SCREEN,
    SET_SETTING,
    LOG_EVENT,
    TRIGGER_SYNC,

    // Custom Actions
    WEBHOOK,
    CUSTOM_SCRIPT,
    RUN_WORKFLOW
}
