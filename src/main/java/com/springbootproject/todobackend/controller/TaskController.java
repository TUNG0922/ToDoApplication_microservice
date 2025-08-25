package com.springbootproject.todobackend.controller;

import com.springbootproject.todobackend.model.Task;
import com.springbootproject.todobackend.repository.ProjectRepository;
import com.springbootproject.todobackend.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ProjectRepository projectRepository;

    // GET tasks, optionally filter by project name
    @GetMapping
    public List<Task> getAllTasks(@RequestParam(required = false) String project) {
        if (project != null && !project.isEmpty()) {
            return taskService.getTasksByProjectName(project);
        }
        return taskService.getAllTasks();
    }

    // CREATE task
    @PostMapping
    public Task createTask(@RequestBody Task task) {
        // Ensure project name is valid
        if (task.getProjectName() != null) {
            projectRepository.findByName(task.getProjectName())
                    .orElseThrow(() -> new IllegalArgumentException("Project not found: " + task.getProjectName()));
        }
        return taskService.createTask(task);
    }

    // UPDATE task
    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Long id, @RequestBody Task taskDetails) {
        // Ensure project name is valid
        if (taskDetails.getProjectName() != null) {
            projectRepository.findByName(taskDetails.getProjectName())
                    .orElseThrow(() -> new IllegalArgumentException("Project not found: " + taskDetails.getProjectName()));
        }
        return taskService.updateTask(id, taskDetails);
    }

    // DELETE task
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}
