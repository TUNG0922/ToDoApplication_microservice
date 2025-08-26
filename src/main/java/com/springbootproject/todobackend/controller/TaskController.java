package com.springbootproject.todobackend.controller;

import com.springbootproject.todobackend.model.Task;
import com.springbootproject.todobackend.repository.ProjectRepository;
import com.springbootproject.todobackend.repository.UserRepository;
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

    @Autowired
    private UserRepository userRepository;

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
        // Validate project
        if (task.getProjectName() != null) {
            projectRepository.findByName(task.getProjectName())
                    .orElseThrow(() -> new IllegalArgumentException("Project not found: " + task.getProjectName()));
        }

        // Validate assignee by name
        if (task.getAssignee() != null && !task.getAssignee().isEmpty()) {
            boolean userExists = userRepository.existsByName(task.getAssignee());
            if (!userExists) {
                throw new IllegalArgumentException("Assignee not found: " + task.getAssignee());
            }
        }

        return taskService.createTask(task);
    }

    // UPDATE task
    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Long id, @RequestBody Task taskDetails) {
        // Validate project
        if (taskDetails.getProjectName() != null) {
            projectRepository.findByName(taskDetails.getProjectName())
                    .orElseThrow(() -> new IllegalArgumentException("Project not found: " + taskDetails.getProjectName()));
        }

        // Validate assignee
        if (taskDetails.getAssignee() != null && !taskDetails.getAssignee().isEmpty()) {
            try {
                Long assigneeId = Long.parseLong(taskDetails.getAssignee());
                if (!userRepository.existsById(assigneeId)) {
                    throw new IllegalArgumentException("Assignee not found with ID: " + assigneeId);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid assignee ID: " + taskDetails.getAssignee());
            }
        }

        return taskService.updateTask(id, taskDetails);
    }

    // DELETE task
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}
