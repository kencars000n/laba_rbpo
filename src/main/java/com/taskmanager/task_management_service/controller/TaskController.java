package com.taskmanager.task_management_service.controller;

import com.taskmanager.task_management_service.entity.Project;
import com.taskmanager.task_management_service.entity.Task;
import com.taskmanager.task_management_service.entity.User;
import com.taskmanager.task_management_service.enums.TaskStatus;
import com.taskmanager.task_management_service.repository.CommentRepository;
import com.taskmanager.task_management_service.repository.ProjectRepository;
import com.taskmanager.task_management_service.repository.TaskRepository;
import com.taskmanager.task_management_service.repository.UserRepository;
import com.taskmanager.task_management_service.service.SearchService;
import com.taskmanager.task_management_service.service.TagService;
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
    private final TagService tagService;
    private final SearchService searchService;
    private final CommentRepository commentRepository;

    public TaskController(TaskRepository taskRepository,
                          ProjectRepository projectRepository,
                          UserRepository userRepository,
                          TaskService taskService,
                          TagService tagService,
                          SearchService searchService,
                          CommentRepository commentRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.taskService = taskService;
        this.tagService = tagService;
        this.searchService = searchService;
        this.commentRepository = commentRepository;
    }

    // GET все задачи
    @GetMapping
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // GET задача по ID
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Optional<Task> task = taskRepository.findById(id);
        return task.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // POST создать задачу
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        return ResponseEntity.ok(taskRepository.save(task));
    }

    // PUT обновить задачу
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

    // DELETE удалить задачу
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // POST обновить статус задачи
    @PostMapping("/{id}/status")
    public ResponseEntity<String> updateTaskStatus(@PathVariable Long id, @RequestBody TaskStatus newStatus) {
        boolean success = taskService.updateTaskStatus(id, newStatus);
        if (success) {
            return ResponseEntity.ok("Task status updated successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid status transition or task not found");
        }
    }

    // POST назначить задачу пользователю
    @PostMapping("/{taskId}/assign/{userId}")
    public ResponseEntity<String> assignTaskToUser(
            @PathVariable Long taskId,
            @PathVariable Long userId) {

        boolean success = taskService.assignTaskToUser(taskId, userId);

        if (success) {
            return ResponseEntity.ok("Task successfully assigned to user");
        } else {
            return ResponseEntity.badRequest().body("Task or User not found");
        }
    }

    // POST добавить теги к задаче
    @PostMapping("/{taskId}/tags")
    public ResponseEntity<String> addTagsToTask(
            @PathVariable Long taskId,
            @RequestBody List<Long> tagIds) {

        boolean success = tagService.addTagsToTask(taskId, tagIds);

        if (success) {
            return ResponseEntity.ok("Tags successfully added to task");
        } else {
            return ResponseEntity.badRequest().body("Task not found or no valid tags provided");
        }
    }

    // POST создать и добавить тег
    @PostMapping("/{taskId}/tags/new")
    public ResponseEntity<String> createAndAddTagToTask(
            @PathVariable Long taskId,
            @RequestBody String tagName) {

        boolean success = tagService.createAndAddTagToTask(taskId, tagName);

        if (success) {
            return ResponseEntity.ok("Tag created and added to task successfully");
        } else {
            return ResponseEntity.badRequest().body("Task not found");
        }
    }

    // НОВЫЙ ENDPOINT: Поиск задач с фильтрами
    @PostMapping("/search")
    public List<Task> searchTasks(@RequestBody SearchService.TaskSearchCriteria criteria) {
        return searchService.searchTasks(criteria);
    }

    // Дополнительный endpoint: Быстрый поиск
    @GetMapping("/search/quick")
    public List<Task> quickSearch(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) String status) {

        SearchService.TaskSearchCriteria criteria = new SearchService.TaskSearchCriteria();
        criteria.setProjectId(projectId);
        criteria.setStatus(status);

        return searchService.searchTasks(criteria);
    }

    // НОВЫЙ ENDPOINT: Копировать задачу с комментариями
    @PostMapping("/{taskId}/copy")
    public ResponseEntity<?> copyTaskWithComments(
            @PathVariable Long taskId,
            @RequestBody(required = false) String newTitle) {

        Task copiedTask = taskService.copyTaskWithComments(taskId, newTitle);

        if (copiedTask != null) {
            return ResponseEntity.ok(copiedTask);
        } else {
            return ResponseEntity.badRequest().body("Task not found");
        }
    }

    // DTO для запроса копирования
    public static class CopyTaskRequest {
        private String newTitle;

        public String getNewTitle() { return newTitle; }
        public void setNewTitle(String newTitle) { this.newTitle = newTitle; }
    }

    // Альтернативный endpoint с DTO
    @PostMapping("/{taskId}/copy-with-title")
    public ResponseEntity<?> copyTaskWithTitle(
            @PathVariable Long taskId,
            @RequestBody CopyTaskRequest request) {

        Task copiedTask = taskService.copyTaskWithComments(taskId, request.getNewTitle());

        if (copiedTask != null) {
            return ResponseEntity.ok(copiedTask);
        } else {
            return ResponseEntity.badRequest().body("Task not found");
        }
    }

    // GET задачи по проекту
    @GetMapping("/project/{projectId}")
    public List<Task> getTasksByProject(@PathVariable Long projectId) {
        Optional<Project> project = projectRepository.findById(projectId);
        return project.map(taskRepository::findByProject).orElse(List.of());
    }

    // GET задачи по пользователю
    @GetMapping("/user/{userId}")
    public List<Task> getTasksByUser(@PathVariable Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(taskRepository::findByAssignee).orElse(List.of());
    }
}