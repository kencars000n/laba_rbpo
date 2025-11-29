package com.taskmanager.task_management_service.controller;

import com.taskmanager.task_management_service.entity.Tag;
import com.taskmanager.task_management_service.repository.TagRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagRepository tagRepository;

    public TagController(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @GetMapping
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tag> getTagById(@PathVariable Long id) {
        Optional<Tag> tag = tagRepository.findById(id);
        return tag.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Tag> getTagByName(@PathVariable String name) {
        Optional<Tag> tag = tagRepository.findAll().stream()
                .filter(t -> t.getName().equals(name))
                .findFirst();
        return tag.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createTag(@RequestBody Tag tag) {
        // Проверка уникальности имени тега
        boolean tagExists = tagRepository.findAll().stream()
                .anyMatch(t -> t.getName().equalsIgnoreCase(tag.getName()));

        if (tagExists) {
            return ResponseEntity.badRequest().body("Tag with this name already exists");
        }

        Tag savedTag = tagRepository.save(tag);
        return ResponseEntity.ok(savedTag);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTag(@PathVariable Long id, @RequestBody Tag tagDetails) {
        Optional<Tag> tagOpt = tagRepository.findById(id);
        if (tagOpt.isPresent()) {
            Tag existingTag = tagOpt.get();

            // Проверка уникальности имени (если изменилось)
            if (!existingTag.getName().equals(tagDetails.getName())) {
                boolean tagExists = tagRepository.findAll().stream()
                        .anyMatch(t -> t.getName().equalsIgnoreCase(tagDetails.getName()) && !t.getId().equals(id));

                if (tagExists) {
                    return ResponseEntity.badRequest().body("Tag with this name already exists");
                }
            }

            existingTag.setName(tagDetails.getName());
            return ResponseEntity.ok(tagRepository.save(existingTag));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        if (tagRepository.existsById(id)) {
            tagRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}