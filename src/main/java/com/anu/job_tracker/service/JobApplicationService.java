package com.anu.job_tracker.service;

import com.anu.job_tracker.ApplicationStatus;
import com.anu.job_tracker.dto.JobApplicationNoteCountResponse;
import com.anu.job_tracker.dto.JobApplicationRequest;
import com.anu.job_tracker.dto.JobApplicationResponse;
import com.anu.job_tracker.entity.JobApplication;
import com.anu.job_tracker.exception.ResourceNotFoundException;
import com.anu.job_tracker.repository.JobApplicationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JobApplicationService {

    private final JobApplicationRepository repository;

    public JobApplicationService(JobApplicationRepository repository) {
        this.repository = repository;
    }

    public JobApplicationResponse createApplication(JobApplicationRequest request) {

        JobApplication application = new JobApplication();

        application.setCompanyName(request.getCompanyName());
        application.setRole(request.getRole());
        application.setStatus(
                ApplicationStatus.valueOf(request.getStatus().toUpperCase())
        );
        application.setLocation(request.getLocation());

        JobApplication savedApplication = repository.save(application);

        return convertToResponse(savedApplication);
    }
    @Transactional(readOnly = true)
    public List<JobApplicationResponse> getAllApplications() {
        return repository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }
    @Transactional(readOnly = true)
    public List<JobApplicationResponse> getApplicationsByStatus(
            ApplicationStatus status
    ) {
        return repository.findByStatus(status)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }
    @Transactional(readOnly = true)
    public List<JobApplicationResponse> searchByCompanyName(
            String companyName
    ) {
        return repository
                .findByCompanyNameContainingIgnoreCase(companyName)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }
    @Transactional(readOnly = true)
    public List<JobApplicationResponse> getApplicationsByLocation(
            String location
    ) {
        return repository.findApplicationsByLocation(location)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }
    @Transactional(readOnly = true)
    public List<JobApplicationResponse> getApplicationsByStatusNative(
            String status
    ) {
        return repository.findApplicationsByStatusNative(status)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }
    @Transactional(readOnly = true)
    public Page<JobApplicationResponse> getApplicationsPage(
            Pageable pageable
    ) {
        return repository.findAll(pageable)
                .map(this::convertToResponse);
    }
    @Transactional(readOnly = true)
    public JobApplicationResponse getApplicationById(Long id) {
        JobApplication application = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Job application not found with id: " + id
                ));

        return convertToResponse(application);
    }
    @Transactional
    public JobApplicationResponse updateApplication(
            Long id,
            JobApplicationRequest request
    ) {
        JobApplication application = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Job application not found with id: " + id
                ));

        application.setCompanyName(request.getCompanyName());
        application.setRole(request.getRole());
        application.setStatus(
                ApplicationStatus.valueOf(request.getStatus().toUpperCase())
        );
        application.setLocation(request.getLocation());
        repository.flush();

        return convertToResponse(application);
    }
    public void deleteApplication(Long id) {
        JobApplication application = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Job application not found with id: " + id
                ));

        repository.delete(application);
    }
    @Transactional(readOnly = true)
    public List<JobApplicationNoteCountResponse>
    getApplicationsWithNoteCounts() {

        return repository.findAllWithNotes()
                .stream()
                .map(application ->
                        new JobApplicationNoteCountResponse(
                                application.getId(),
                                application.getCompanyName(),
                                application.getNotes().size()
                        )
                )
                .toList();
    }

    private JobApplicationResponse convertToResponse(JobApplication application) {
        return new JobApplicationResponse(
                application.getId(),
                application.getCompanyName(),
                application.getRole(),
                application.getStatus().name(),
                application.getLocation(),
                application.getCreatedAt(),
                application.getUpdatedAt()

                );
        }
    }

