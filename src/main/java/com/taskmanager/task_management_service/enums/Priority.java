package com.taskmanager.task_management_service.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Priority {
    LOW("НИЗКИЙ"),
    MEDIUM("СРЕДНИЙ"),
    HIGH("ВЫСОКИЙ"),
    CRITICAL("КРИТИЧЕСКИЙ");

    private final String russianValue;

    Priority(String russianValue) {
        this.russianValue = russianValue;
    }

    @JsonValue
    public String getRussianValue() {
        return russianValue;
    }
}