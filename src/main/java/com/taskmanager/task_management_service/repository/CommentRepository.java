package com.taskmanager.task_management_service.repository;

import com.taskmanager.task_management_service.entity.Comment;
import com.taskmanager.task_management_service.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTask(Task task);
}