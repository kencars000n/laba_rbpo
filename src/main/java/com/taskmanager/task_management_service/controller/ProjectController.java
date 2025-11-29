package com.taskmanager.task_management_service.controller;

import com.taskmanager.task_management_service.entity.Project;
import com.taskmanager.task_management_service.repository.ProjectRepository;
import com.taskmanager.task_management_service.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectRepository projectRepository;
    private final ProjectService projectService;

    public ProjectController(ProjectRepository projectRepository, ProjectService projectService) {
        this.projectRepository = projectRepository;
        this.projectService = projectService;
    }

    // GET все проекты
    @GetMapping
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    // GET проект по ID
    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        Optional<Project> project = projectRepository.findById(id);
        return project.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // POST создать проект
    @PostMapping
    public Project createProject(@RequestBody Project project) {
        return projectRepository.save(project);
    }

    // PUT обновить проект
    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @RequestBody Project projectDetails) {
        Optional<Project> projectOpt = projectRepository.findById(id);
        if (projectOpt.isPresent()) {
            Project existingProject = projectOpt.get();
            existingProject.setName(projectDetails.getName());
            existingProject.setDescription(projectDetails.getDescription());
            return ResponseEntity.ok(projectRepository.save(existingProject));
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE удалить проект
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        if (projectRepository.existsById(id)) {
            projectRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // НОВЫЙ ENDPOINT: Получить статистику по проекту
    @GetMapping("/{id}/statistics")
    public ResponseEntity<Map<String, Object>> getProjectStatistics(@PathVariable Long id) {
        Map<String, Object> statistics = projectService.getProjectStatistics(id);

        if (statistics.containsKey("error")) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(statistics);
    }

    // Дополнительный endpoint: Получить последнюю активность по проекту
    @GetMapping("/{id}/recent-activity")
    public ResponseEntity<Map<String, Object>> getRecentProjectActivity(
            @PathVariable Long id,
            @RequestParam(defaultValue = "5") int limit) {

        Map<String, Object> activity = projectService.getRecentProjectActivity(id, limit);

        if (activity.containsKey("error")) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(activity);
    }
}