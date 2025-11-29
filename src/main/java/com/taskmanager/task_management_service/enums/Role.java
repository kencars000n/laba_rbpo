package com.taskmanager.task_management_service.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {
    ADMIN("АДМИНИСТРАТОР"),
    USER("ПОЛЬЗОВАТЕЛЬ");

    private final String russianValue;

    Role(String russianValue) {
        this.russianValue = russianValue;
    }

    @JsonValue
    public String getRussianValue() {
        return russianValue;
    }
}