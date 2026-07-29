package com.anu.job_tracker.dto;

import java.time.LocalDateTime;

public class JobApplicationResponse {

    private Long id;
    private String companyName;
    private String role;
    private String status;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public JobApplicationResponse(
            Long id,
            String companyName,
            String role,
            String status,
            String location,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.companyName = companyName;
        this.role = role;
        this.status = status;
        this.location = location;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getRole() {
        return role;
    }

    public String getStatus() {
        return status;
    }

    public String getLocation() {
        return location;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}