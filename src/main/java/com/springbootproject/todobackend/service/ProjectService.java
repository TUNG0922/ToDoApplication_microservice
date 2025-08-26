package com.springbootproject.todobackend.service;

import com.springbootproject.todobackend.model.Project;
import com.springbootproject.todobackend.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository repo;

    public ProjectService(ProjectRepository repo) {
        this.repo = repo;
    }

    // Create project with name, assignee, createdBy
    public Project createProject(String name, String createdBy) {
        // Check for duplicate project name
        if (repo.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Project already exists: " + name);
        }

        Project project = new Project();
        project.setName(name);
        project.setCreatedBy(createdBy); // set who created the project

        return repo.save(project);
    }

    // List all projects
    public List<Project> listProjects() {
        return repo.findAll();
    }

    public List<Project> listProjectsByUser(String username) {
        return repo.findByCreatedByOrAssignee(username, username);
    }
}
