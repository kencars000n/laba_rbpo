package com.taskmanager.task_management_service.repository;

import com.taskmanager.task_management_service.entity.Task;
import com.taskmanager.task_management_service.entity.Project;
import com.taskmanager.task_management_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProject(Project project);
    List<Task> findByAssignee(User assignee);
}