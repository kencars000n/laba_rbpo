// TagService.java
package com.taskmanager.task_management_service.service;

import com.taskmanager.task_management_service.entity.Tag;
import com.taskmanager.task_management_service.entity.Task;
import com.taskmanager.task_management_service.repository.TagRepository;
import com.taskmanager.task_management_service.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TagService {

    private final TagRepository tagRepository;
    private final TaskRepository taskRepository;

    public TagService(TagRepository tagRepository, TaskRepository taskRepository) {
        this.tagRepository = tagRepository;
        this.taskRepository = taskRepository;
    }

    // НОВЫЙ МЕТОД: Добавить теги к задаче
    @Transactional
    public boolean addTagsToTask(Long taskId, List<Long> tagIds) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);

        if (taskOptional.isEmpty()) {
            return false;
        }

        Task task = taskOptional.get();
        List<Tag> tagsToAdd = new ArrayList<>();

        // Находим все теги по их ID
        for (Long tagId : tagIds) {
            Optional<Tag> tagOptional = tagRepository.findById(tagId);
            if (tagOptional.isPresent()) {
                tagsToAdd.add(tagOptional.get());
            }
        }

        // Добавляем теги к задаче
        task.getTags().addAll(tagsToAdd);
        taskRepository.save(task);

        return true;
    }

    // Дополнительный метод: Создать тег и добавить к задаче
    @Transactional
    public boolean createAndAddTagToTask(Long taskId, String tagName) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);

        if (taskOptional.isEmpty()) {
            return false;
        }

        // Создаем новый тег
        Tag newTag = new Tag(tagName);
        Tag savedTag = tagRepository.save(newTag);

        // Добавляем тег к задаче
        Task task = taskOptional.get();
        task.getTags().add(savedTag);
        taskRepository.save(task);

        return true;
    }
}