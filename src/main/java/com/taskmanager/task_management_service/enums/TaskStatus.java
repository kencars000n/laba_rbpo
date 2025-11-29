package com.taskmanager.task_management_service.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TaskStatus {
    OPEN("ОТКРЫТА"),
    IN_PROGRESS("В РАБОТЕ"),
    REVIEW("НА ПРОВЕРКЕ"),
    DONE("ЗАВЕРШЕНА"),
    CANCELLED("ОТМЕНЕНА");

    private final String russianValue;

    TaskStatus(String russianValue) {
        this.russianValue = russianValue;
    }

    @JsonValue
    public String getRussianValue() {
        return russianValue;
    }
}