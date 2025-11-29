package com.taskmanager.task_management_service.service;

import com.taskmanager.task_management_service.entity.Project;
import com.taskmanager.task_management_service.entity.Task;
import com.taskmanager.task_management_service.entity.User;
import com.taskmanager.task_management_service.enums.TaskStatus;
import com.taskmanager.task_management_service.repository.ProjectRepository;
import com.taskmanager.task_management_service.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public ProjectService(ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    // НОВЫЙ МЕТОД: Получить статистику по проекту
    public Map<String, Object> getProjectStatistics(Long projectId) {
        Optional<Project> projectOptional = projectRepository.findById(projectId);

        if (projectOptional.isEmpty()) {
            return Map.of("error", "Project not found");
        }

        Project project = projectOptional.get();
        List<Task> projectTasks = taskRepository.findByProject(project);

        // Статистика по статусам
        Map<TaskStatus, Long> statusStats = projectTasks.stream()
                .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()));

        // Статистика по пользователям (назначенные задачи)
        Map<String, Long> userStats = projectTasks.stream()
                .filter(task -> task.getAssignee() != null)
                .collect(Collectors.groupingBy(
                        task -> task.getAssignee().getUsername(),
                        Collectors.counting()
                ));

        // Статистика по приоритетам
        Map<String, Long> priorityStats = projectTasks.stream()
                .filter(task -> task.getPriority() != null)
                .collect(Collectors.groupingBy(
                        task -> task.getPriority().getRussianValue(),
                        Collectors.counting()
                ));

        // Популярные теги
        Map<String, Long> tagStats = projectTasks.stream()
                .flatMap(task -> task.getTags().stream())
                .collect(Collectors.groupingBy(
                        tag -> tag.getName(),
                        Collectors.counting()
                ));

        // Собираем всю статистику
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("projectId", project.getId());
        statistics.put("projectName", project.getName());
        statistics.put("totalTasks", projectTasks.size());
        statistics.put("tasksByStatus", statusStats);
        statistics.put("tasksByUser", userStats);
        statistics.put("tasksByPriority", priorityStats);
        statistics.put("popularTags", tagStats);
        statistics.put("completionRate", calculateCompletionRate(projectTasks));

        return statistics;
    }

    // Метод для расчета процента завершенных задач
    private double calculateCompletionRate(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return 0.0;
        }

        long completedTasks = tasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.DONE)
                .count();

        return (double) completedTasks / tasks.size() * 100;
    }

    // Дополнительный метод: Получить недавние задачи проекта
    public Map<String, Object> getRecentProjectActivity(Long projectId, int limit) {
        Optional<Project> projectOptional = projectRepository.findById(projectId);

        if (projectOptional.isEmpty()) {
            return Map.of("error", "Project not found");
        }

        Project project = projectOptional.get();
        List<Task> projectTasks = taskRepository.findByProject(project);

        // Берем последние N задач (можно добавить сортировку по дате создания)
        List<Map<String, Object>> recentTasks = projectTasks.stream()
                .limit(limit)
                .map(task -> {
                    Map<String, Object> taskInfo = new HashMap<>();
                    taskInfo.put("id", task.getId());
                    taskInfo.put("title", task.getTitle());
                    taskInfo.put("status", task.getStatus().getRussianValue());
                    taskInfo.put("assignee", task.getAssignee() != null ?
                            task.getAssignee().getUsername() : "Не назначена");
                    return taskInfo;
                })
                .collect(Collectors.toList());

        Map<String, Object> activity = new HashMap<>();
        activity.put("projectId", project.getId());
        activity.put("projectName", project.getName());
        activity.put("recentTasks", recentTasks);

        return activity;
    }
}