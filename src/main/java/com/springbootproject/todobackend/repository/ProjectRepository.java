package com.springbootproject.todobackend.repository;

import com.springbootproject.todobackend.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;       // <-- add this import
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    boolean existsByName(String name);

    Optional<Project> findByName(String projName);  // also fix Optional<Object> -> Optional<Project>

    List<Project> findByCreatedByOrAssignee(String createdBy, String assignee);
}
