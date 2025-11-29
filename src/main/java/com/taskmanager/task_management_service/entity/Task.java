package com.taskmanager.task_management_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.taskmanager.task_management_service.enums.TaskStatus;
import com.taskmanager.task_management_service.enums.Priority;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.OPEN;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @ManyToMany
    @JoinTable(
            name = "task_tags",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Comment> comments = new ArrayList<>();

    // Конструкторы
    public Task() {}

    public Task(String title, String description, Project project, User assignee) {
        this.title = title;
        this.description = description;
        this.project = project;
        this.assignee = assignee;
    }

    // Метод для смены статуса с проверкой допустимых переходов
    public boolean changeStatus(TaskStatus newStatus) {
        if (isValidStatusTransition(this.status, newStatus)) {
            this.status = newStatus;
            return true;
        }
        return false;
    }

    // Логика допустимых переходов статусов
    private boolean isValidStatusTransition(TaskStatus current, TaskStatus newStatus) {
        switch (current) {
            case OPEN:
                return newStatus == TaskStatus.IN_PROGRESS || newStatus == TaskStatus.CANCELLED;
            case IN_PROGRESS:
                return newStatus == TaskStatus.REVIEW || newStatus == TaskStatus.CANCELLED;
            case REVIEW:
                return newStatus == TaskStatus.DONE || newStatus == TaskStatus.IN_PROGRESS;
            case DONE:
                return false;
            case CANCELLED:
                return false;
            default:
                return false;
        }
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
    public User getAssignee() { return assignee; }
    public void setAssignee(User assignee) { this.assignee = assignee; }
    public List<Tag> getTags() { return tags; }
    public void setTags(List<Tag> tags) { this.tags = tags; }
    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }
}