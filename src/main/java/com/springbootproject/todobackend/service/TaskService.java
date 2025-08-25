package com.springbootproject.todobackend.service;

import com.springbootproject.todobackend.model.Task;
import com.springbootproject.todobackend.model.Project;
import com.springbootproject.todobackend.repository.TaskRepository;
import com.springbootproject.todobackend.repository.ProjectRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EntityManager entityManager;

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    /**
     * Get tasks filtered by project name (useful for GET /api/tasks?project=ProjectName)
     */
    public List<Task> getTasksByProjectName(String projectName) {
        if (projectName == null || projectName.trim().isEmpty()) {
            return getAllTasks();
        }
        return taskRepository.findByProjectName(projectName);
    }

    /**
     * Create a task and associate with a project by projectName.
     * If projectName is null, you can decide whether to throw or allow null project.
     */
    @Transactional
    public Task createTask(Task task) {
        if (task.getProjectName() != null && !task.getProjectName().trim().isEmpty()) {
            projectRepository.findByName(task.getProjectName().trim())
                    .orElseThrow(() -> new IllegalArgumentException("Project not found: " + task.getProjectName()));
        }

        // set createdAt if not set
        if (task.getCreatedAt() == null) {
            task.setCreatedAt(java.time.LocalDateTime.now());
        }
        return taskRepository.save(task);
    }

    /**
     * Alternative create that accepts projectId instead.
     */
    @Transactional
    public Task createTaskWithProjectId(Task task, Long projectId) {
        if (projectId != null) {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + projectId));
            task.setProjectName(project.getName());  // use project name instead of object
        } else {
            task.setProjectName(null);
        }

        if (task.getCreatedAt() == null) {
            task.setCreatedAt(java.time.LocalDateTime.now());
        }
        return taskRepository.save(task);
    }

    public Task updateTask(Long id, Task taskDetails) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setPriority(taskDetails.getPriority());
        task.setDeadline(taskDetails.getDeadline());
        task.setAssignee(taskDetails.getAssignee());
        task.setCreatedBy(taskDetails.getCreatedBy());

        if (taskDetails.getProjectName() != null && !taskDetails.getProjectName().trim().isEmpty()) {
            projectRepository.findByName(taskDetails.getProjectName().trim())
                    .orElseThrow(() -> new IllegalArgumentException("Project not found: " + taskDetails.getProjectName()));
            task.setProjectName(taskDetails.getProjectName().trim());
        }

        return taskRepository.save(task);
    }

    /**
     * Deleting tasks: keep it simple and avoid reassigning primary keys.
     * Reordering PKs is not recommended in production; commented out by default.
     */
    @Transactional
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
        // Recommended: DO NOT reorder primary keys. If you really must for some reason,
        // implement carefully and be aware of FK constraints and concurrency.
        // reorderTaskIds();
    }

    // Keep reorderTaskIds only if you have a very specific reason. Otherwise remove it.
    @Transactional
    public void reorderTaskIds() {
        // WARNING: changing primary keys is dangerous. Use with extreme caution.
        entityManager.createNativeQuery(
                "WITH reordered AS (" +
                        "  SELECT id, ROW_NUMBER() OVER (ORDER BY id) AS new_id FROM tasks" +
                        ")" +
                        "UPDATE tasks t SET id = r.new_id FROM reordered r WHERE t.id = r.id"
        ).executeUpdate();

        entityManager.createNativeQuery(
                "SELECT setval('tasks_id_seq', COALESCE((SELECT MAX(id) FROM tasks), 1), false)"
        ).getSingleResult();
    }
}
