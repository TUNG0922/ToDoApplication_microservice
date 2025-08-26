package com.springbootproject.todobackend.repository;

import com.springbootproject.todobackend.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // Remove this line — no longer exists in Task entity
    // List<Task> findByProjectId(Long projectId);

    // Use projectName (string) to filter tasks by project
    List<Task> findByProjectName(String projectName);
    List<Task> findByAssignee(String assignee);
    List<Task> findByCreatedBy(String createdBy);
}
