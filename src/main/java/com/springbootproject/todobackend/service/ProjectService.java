package com.springbootproject.todobackend.service;

import com.springbootproject.todobackend.model.Project;
import com.springbootproject.todobackend.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository repo;

    public ProjectService(ProjectRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public Project createProject(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name must not be empty");
        }
        if (repo.existsByName(name.trim())) {
            throw new IllegalArgumentException("Project with the same name already exists");
        }
        Project p = new Project(name.trim());
        return repo.save(p);
    }

    @Transactional(readOnly = true)
    public List<Project> listProjects() {
        return repo.findAll();
    }
}
