package com.anu.job_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class JobApplicationRequest {
    @NotBlank(message = "Company name is required")
    @Size(max = 150, message = "Company name must be 150 characters or fewer")
    private String companyName;

    @NotBlank(message = "Role is required")
    @Size(max = 150, message = "Role must be 150 characters or fewer")
    private String role;

    @NotBlank(message = "Status is required")
    private String status;

    @Size(max = 150, message = "Location must be 150 characters or fewer")
    private String location;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

