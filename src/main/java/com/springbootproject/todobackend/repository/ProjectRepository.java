package com.springbootproject.todobackend.repository;

import com.springbootproject.todobackend.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    boolean existsByName(String name);

    Optional<Object> findByName(String projName);
}