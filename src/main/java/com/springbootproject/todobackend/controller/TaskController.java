package com.springbootproject.todobackend.controller;

import com.springbootproject.todobackend.model.Project;
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
    public List<Task> getAllTasks(
            @RequestParam(required = false) String project,
            @RequestParam(required = false) String assignee,
            @RequestParam(required = false, name = "created_by") String createdBy) {

        // If project, assignee, or createdBy filters are provided
        if (project != null && !project.isEmpty()) {
            return taskService.getTasksByProjectName(project);
        } else if (assignee != null && !assignee.isEmpty()) {
            return taskService.getTasksByAssignee(assignee);
        } else if (createdBy != null && !createdBy.isEmpty()) {
            return taskService.getTasksByCreatedBy(createdBy);
        }

        // Default: return all tasks
        return taskService.getAllTasks();
    }

    // CREATE task
    @PostMapping
    public Task createTask(@RequestBody Task task) {
        // Project name is required
        if (task.getProjectName() == null || task.getProjectName().isEmpty()) {
            throw new IllegalArgumentException("Project name is required to create a task.");
        }

        // Validate that project exists
        Project project = projectRepository.findByName(task.getProjectName())
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + task.getProjectName()));

        // Validate assignee by name (optional, if provided)
        if (task.getAssignee() != null && !task.getAssignee().isEmpty()) {
            boolean userExists = userRepository.existsByName(task.getAssignee());
            if (!userExists) {
                throw new IllegalArgumentException("Assignee not found: " + task.getAssignee());
            }
        }

        // Ensure task stores project name
        task.setProjectName(project.getName());

        // Create the task
        Task createdTask = taskService.createTask(task);

        // Optionally update project assignee if task assignee is provided
        if (task.getAssignee() != null && !task.getAssignee().isEmpty()) {
            project.setAssignee(task.getAssignee());
            projectRepository.save(project);
        }

        return createdTask;
    }

    // UPDATE task
    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Long id, @RequestBody Task taskDetails) {
        // Fetch existing task
        Task existingTask = taskService.getTaskById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + id));

        // Update project if provided
        if (taskDetails.getProjectName() != null && !taskDetails.getProjectName().isEmpty()) {
            projectRepository.findByName(taskDetails.getProjectName())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Project not found: " + taskDetails.getProjectName()));
            existingTask.setProjectName(taskDetails.getProjectName());
        }

        // Update assignee by name
        if (taskDetails.getAssignee() != null && !taskDetails.getAssignee().isEmpty()) {
            boolean userExists = userRepository.existsByName(taskDetails.getAssignee());
            if (!userExists) {
                throw new IllegalArgumentException("Assignee not found: " + taskDetails.getAssignee());
            }
            existingTask.setAssignee(taskDetails.getAssignee());
        }

        // Update status if provided
        if (taskDetails.getStatus() != null && !taskDetails.getStatus().isEmpty()) {
            existingTask.setStatus(taskDetails.getStatus());
        }

        // Update other fields
        if (taskDetails.getTitle() != null) {
            existingTask.setTitle(taskDetails.getTitle());
        }
        if (taskDetails.getDescription() != null) {
            existingTask.setDescription(taskDetails.getDescription());
        }
        existingTask.setPriority(taskDetails.getPriority());
        existingTask.setDeadline(taskDetails.getDeadline());
        existingTask.setCreatedBy(taskDetails.getCreatedBy());

        // Save updated task
        return taskService.updateTask(id, existingTask);
    }

    // DELETE task
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}
