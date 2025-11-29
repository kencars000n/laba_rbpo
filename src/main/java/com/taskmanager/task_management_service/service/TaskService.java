package com.taskmanager.task_management_service.service;

import com.taskmanager.task_management_service.entity.Task;
import com.taskmanager.task_management_service.enums.TaskStatus;
import com.taskmanager.task_management_service.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public boolean updateTaskStatus(Long taskId, TaskStatus newStatus) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            boolean success = task.changeStatus(newStatus);
            if (success) {
                taskRepository.save(task);
            }
            return success;
        }
        return false;
    }
}