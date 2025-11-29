package com.taskmanager.task_management_service.controller;

import com.taskmanager.task_management_service.entity.Project;
import com.taskmanager.task_management_service.entity.Task;
import com.taskmanager.task_management_service.entity.User;
import com.taskmanager.task_management_service.enums.TaskStatus;
import com.taskmanager.task_management_service.repository.ProjectRepository;
import com.taskmanager.task_management_service.repository.TaskRepository;
import com.taskmanager.task_management_service.repository.UserRepository;
import com.taskmanager.task_management_service.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskService taskService;

    public TaskController(TaskRepository taskRepository,
                          ProjectRepository projectRepository,
                          UserRepository userRepository,
                          TaskService taskService) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.taskService = taskService;
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Optional<Task> task = taskRepository.findById(id);
        return task.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        return ResponseEntity.ok(taskRepository.save(task));
    }

    @PostMapping("/{id}/status")
    public ResponseEntity<String> updateTaskStatus(@PathVariable Long id, @RequestBody TaskStatus newStatus) {
        boolean success = taskService.updateTaskStatus(id, newStatus);
        if (success) {
            return ResponseEntity.ok("Task status updated successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid status transition or task not found");
        }
    }

    @GetMapping("/project/{projectId}")
    public List<Task> getTasksByProject(@PathVariable Long projectId) {
        Optional<Project> project = projectRepository.findById(projectId);
        return project.map(taskRepository::findByProject).orElse(List.of());
    }

    @GetMapping("/user/{userId}")
    public List<Task> getTasksByUser(@PathVariable Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(taskRepository::findByAssignee).orElse(List.of());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task taskDetails) {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isPresent()) {
            Task existingTask = task.get();
            existingTask.setTitle(taskDetails.getTitle());
            existingTask.setDescription(taskDetails.getDescription());
            existingTask.setPriority(taskDetails.getPriority());
            existingTask.setTags(taskDetails.getTags());
            return ResponseEntity.ok(taskRepository.save(existingTask));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}