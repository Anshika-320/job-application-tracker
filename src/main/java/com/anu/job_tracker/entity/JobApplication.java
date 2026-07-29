package com.anu.job_tracker.entity;

import com.anu.job_tracker.ApplicationStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "job_applications")
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "company_name", nullable = false, length = 150)
    private String companyName;
    @Column(name = "role", nullable = false, length = 150)
    private String role;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ApplicationStatus status;
    @Column(name = "location", length = 150)
    private String location;
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;
    @OneToMany(
            mappedBy = "jobApplication",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ApplicationNote> notes = new ArrayList<>();
    @PrePersist
    private void setCreationTimestamps() {
        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    private void setUpdateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    public JobApplication() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    public List<ApplicationNote> getNotes() {
        return notes;
    }

    public void setNotes(List<ApplicationNote> notes) {
        this.notes = notes;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void removeNote(ApplicationNote note) {
        notes.remove(note);
        note.setJobApplication(null);

    }
}
