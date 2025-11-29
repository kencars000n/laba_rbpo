package com.taskmanager.task_management_service.service;

import com.taskmanager.task_management_service.entity.Comment;
import com.taskmanager.task_management_service.entity.Task;
import com.taskmanager.task_management_service.entity.User;
import com.taskmanager.task_management_service.enums.TaskStatus;
import com.taskmanager.task_management_service.repository.CommentRepository;
import com.taskmanager.task_management_service.repository.TaskRepository;
import com.taskmanager.task_management_service.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public TaskService(TaskRepository taskRepository,
                       UserRepository userRepository,
                       CommentRepository commentRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
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

    @Transactional
    public boolean assignTaskToUser(Long taskId, Long userId) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        Optional<User> userOptional = userRepository.findById(userId);

        if (taskOptional.isPresent() && userOptional.isPresent()) {
            Task task = taskOptional.get();
            User user = userOptional.get();

            task.setAssignee(user);
            taskRepository.save(task);

            return true;
        }
        return false;
    }

    // НОВЫЙ МЕТОД: Копировать задачу с комментариями
    @Transactional
    public Task copyTaskWithComments(Long taskId, String newTitle) {
        Optional<Task> originalTaskOpt = taskRepository.findById(taskId);

        if (originalTaskOpt.isEmpty()) {
            return null;
        }

        Task originalTask = originalTaskOpt.get();

        // Создаем копию задачи
        Task copiedTask = new Task();
        copiedTask.setTitle(newTitle != null ? newTitle : "Копия: " + originalTask.getTitle());
        copiedTask.setDescription(originalTask.getDescription());
        copiedTask.setStatus(TaskStatus.OPEN); // Новая задача всегда открыта
        copiedTask.setPriority(originalTask.getPriority());
        copiedTask.setProject(originalTask.getProject());
        copiedTask.setAssignee(originalTask.getAssignee());

        // Копируем теги
        if (originalTask.getTags() != null) {
            copiedTask.setTags(new ArrayList<>(originalTask.getTags()));
        }

        // Сохраняем новую задачу
        Task savedCopiedTask = taskRepository.save(copiedTask);

        // Копируем комментарии
        List<Comment> originalComments = commentRepository.findByTask(originalTask);
        if (!originalComments.isEmpty()) {
            List<Comment> copiedComments = new ArrayList<>();

            for (Comment originalComment : originalComments) {
                Comment copiedComment = new Comment();
                copiedComment.setText(originalComment.getText());
                copiedComment.setTask(savedCopiedTask);
                copiedComment.setAuthor(originalComment.getAuthor());
                copiedComment.setCreatedAt(originalComment.getCreatedAt());
                copiedComments.add(copiedComment);
            }

            commentRepository.saveAll(copiedComments);
        }

        return savedCopiedTask;
    }
}