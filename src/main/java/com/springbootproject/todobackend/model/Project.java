package com.springbootproject.todobackend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "assignee")
    private String assignee;   // the person assigned to this project

    @Column(name = "created_by")
    private String createdBy;  // the user who created this project

    public Project() {}

    public Project(String name, String assignee, String createdBy) {
        this.name = name;
        this.assignee = assignee;
        this.createdBy = createdBy;
    }

    public Project(String trim) {
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // getters & setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getAssignee() { return assignee; }
    public String getCreatedBy() { return createdBy; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setAssignee(String assignee) { this.assignee = assignee; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}
