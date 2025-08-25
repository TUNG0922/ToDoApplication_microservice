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

    @PostMapping
    public ResponseEntity<?> createProject(@RequestBody Map<String, Object> payload) {
        Object maybeName = payload.get("name");
        if (maybeName == null) {
            return ResponseEntity.badRequest().body("Missing 'name' field");
        }
        String name = String.valueOf(maybeName).trim();
        try {
            Project created = svc.createProject(name);
            URI location = URI.create("/api/projects/" + created.getId());
            return ResponseEntity.created(location).body(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // List projects
    @GetMapping
    public ResponseEntity<List<Project>> listProjects() {
        return ResponseEntity.ok(svc.listProjects());
    }
}
