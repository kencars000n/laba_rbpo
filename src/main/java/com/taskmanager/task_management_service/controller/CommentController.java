package com.taskmanager.task_management_service.controller;

import com.taskmanager.task_management_service.entity.Comment;
import com.taskmanager.task_management_service.entity.Task;
import com.taskmanager.task_management_service.entity.User;
import com.taskmanager.task_management_service.repository.CommentRepository;
import com.taskmanager.task_management_service.repository.TaskRepository;
import com.taskmanager.task_management_service.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public CommentController(CommentRepository commentRepository,
                             TaskRepository taskRepository,
                             UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody CommentRequest request) {
        try {
            // Находим задачу и пользователя по ID
            Optional<Task> taskOpt = taskRepository.findById(request.getTaskId());
            Optional<User> userOpt = userRepository.findById(request.getAuthorId());

            if (taskOpt.isEmpty() || userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Task or User not found");
            }

            // Создаем комментарий
            Comment comment = new Comment();
            comment.setText(request.getText());
            comment.setTask(taskOpt.get());
            comment.setAuthor(userOpt.get());

            Comment savedComment = commentRepository.save(comment);
            return ResponseEntity.ok(savedComment);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error creating comment: " + e.getMessage());
        }
    }

    // Получить комментарии для задачи
    @GetMapping("/task/{taskId}")
    public List<Comment> getCommentsByTask(@PathVariable Long taskId) {
        Optional<Task> task = taskRepository.findById(taskId);
        return task.map(commentRepository::findByTask).orElse(List.of());
    }

    // DTO класс для запроса
    public static class CommentRequest {
        private String text;
        private Long taskId;
        private Long authorId;

        // Геттеры и сеттеры
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public Long getTaskId() { return taskId; }
        public void setTaskId(Long taskId) { this.taskId = taskId; }
        public Long getAuthorId() { return authorId; }
        public void setAuthorId(Long authorId) { this.authorId = authorId; }
    }
}