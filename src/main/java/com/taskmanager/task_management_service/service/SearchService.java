package com.taskmanager.task_management_service.service;

import com.taskmanager.task_management_service.entity.Task;
import com.taskmanager.task_management_service.enums.TaskStatus;
import com.taskmanager.task_management_service.enums.Priority;
import com.taskmanager.task_management_service.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final TaskRepository taskRepository;

    public SearchService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // НОВЫЙ МЕТОД: Поиск задач с фильтрами
    public List<Task> searchTasks(TaskSearchCriteria criteria) {
        List<Task> allTasks = taskRepository.findAll();

        // Конвертируем русские строки в enum
        TaskStatus taskStatus = criteria.getStatus() != null ?
                getStatusFromRussian(criteria.getStatus()) : null;
        Priority taskPriority = criteria.getPriority() != null ?
                getPriorityFromRussian(criteria.getPriority()) : null;

        return allTasks.stream()
                .filter(task -> criteria.getProjectId() == null ||
                        (task.getProject() != null && task.getProject().getId().equals(criteria.getProjectId())))
                .filter(task -> taskStatus == null || task.getStatus() == taskStatus)
                .filter(task -> taskPriority == null || task.getPriority() == taskPriority)
                .filter(task -> criteria.getAssigneeId() == null ||
                        (task.getAssignee() != null && task.getAssignee().getId().equals(criteria.getAssigneeId())))
                .filter(task -> criteria.getTagIds() == null || criteria.getTagIds().isEmpty() ||
                        task.getTags().stream().anyMatch(tag -> criteria.getTagIds().contains(tag.getId())))
                .filter(task -> criteria.getTitle() == null ||
                        task.getTitle().toLowerCase().contains(criteria.getTitle().toLowerCase()))
                .collect(Collectors.toList());
    }

    // Вспомогательный метод для конвертации русских строк в статус
    public static TaskStatus getStatusFromRussian(String russianStatus) {
        if (russianStatus == null) return null;
        for (TaskStatus status : TaskStatus.values()) {
            if (status.getRussianValue().equalsIgnoreCase(russianStatus)) {
                return status;
            }
        }
        return null;
    }

    // Вспомогательный метод для конвертации русских строк в приоритет
    public static Priority getPriorityFromRussian(String russianPriority) {
        if (russianPriority == null) return null;
        for (Priority priority : Priority.values()) {
            if (priority.getRussianValue().equalsIgnoreCase(russianPriority)) {
                return priority;
            }
        }
        return null;
    }

    // DTO класс для критериев поиска
    public static class TaskSearchCriteria {
        private Long projectId;
        private String status;  // Русские значения: "ОТКРЫТА", "В РАБОТЕ" и т.д.
        private String priority; // Русские значения: "НИЗКИЙ", "СРЕДНИЙ", "ВЫСОКИЙ", "КРИТИЧЕСКИЙ"
        private Long assigneeId;
        private List<Long> tagIds;
        private String title;

        // Геттеры и сеттеры
        public Long getProjectId() { return projectId; }
        public void setProjectId(Long projectId) { this.projectId = projectId; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }

        public Long getAssigneeId() { return assigneeId; }
        public void setAssigneeId(Long assigneeId) { this.assigneeId = assigneeId; }

        public List<Long> getTagIds() { return tagIds; }
        public void setTagIds(List<Long> tagIds) { this.tagIds = tagIds; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
    }
}