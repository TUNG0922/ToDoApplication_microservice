package com.springbootproject.todobackend.controller;

import com.springbootproject.todobackend.model.Project;
import com.springbootproject.todobackend.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin
public class ProjectController {

    private final ProjectService svc;

    public ProjectController(ProjectService svc) {
        this.svc = svc;
    }

    // CREATE project with name, assignee, and createdBy
    @PostMapping
    public ResponseEntity<?> createProject(@RequestBody Map<String, Object> payload) {
        Object maybeName = payload.get("name");
        if (maybeName == null) {
            return ResponseEntity.badRequest().body("Missing 'name' field");
        }
        String name = String.valueOf(maybeName).trim();

        String createdBy = payload.get("created_by") != null ? String.valueOf(payload.get("created_by")).trim() : null;

        try {
            Project created = svc.createProject(name, createdBy);
            URI location = URI.create("/api/projects/" + created.getId());
            return ResponseEntity.created(location).body(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // LIST projects
    @GetMapping
    public ResponseEntity<List<Project>> listProjects() {
        return ResponseEntity.ok(svc.listProjects());
    }

    // LIST projects by user (created_by or assignee)
    @GetMapping("/user")
    public ResponseEntity<List<Project>> listProjectsByUser(@RequestParam String username) {
        List<Project> projects = svc.listProjectsByUser(username);
        return ResponseEntity.ok(projects);
    }
}
