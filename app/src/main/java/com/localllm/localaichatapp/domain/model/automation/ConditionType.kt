package com.localllm.localaichatapp.domain.model.automation

/**
 * Types of conditions that can control workflow execution.
 */
enum class ConditionType {
    // Comparison Conditions
    EQUALS,
    NOT_EQUALS,
    CONTAINS,
    NOT_CONTAINS,
    STARTS_WITH,
    ENDS_WITH,
    GREATER_THAN,
    LESS_THAN,
    GREATER_THAN_OR_EQUAL,
    LESS_THAN_OR_EQUAL,

    // Logical Conditions
    AND,
    OR,
    NOT,

    // Context Conditions
    TIME_OF_DAY,
    DAY_OF_WEEK,
    BATTERY_LEVEL,
    NETWORK_AVAILABLE,
    STORAGE_AVAILABLE,
    CHARGING_STATE,

    // App State Conditions
    SESSION_COUNT,
    MESSAGE_COUNT,
    MODEL_ACTIVE,
    FEATURE_ENABLED,

    // Pattern Matching
    REGEX_MATCH,
    KEYWORD_MATCH,
    LENGTH_CHECK
}
