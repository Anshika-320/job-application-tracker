package com.anu.job_tracker.controller;

import com.anu.job_tracker.ApplicationStatus;
import com.anu.job_tracker.dto.JobApplicationNoteCountResponse;
import com.anu.job_tracker.dto.JobApplicationRequest;
import com.anu.job_tracker.dto.JobApplicationResponse;
import com.anu.job_tracker.service.JobApplicationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

    @RestController
    @RequestMapping("/api/applications")
    public class JobApplicationController {

        private final JobApplicationService jobApplicationService;

        public JobApplicationController(JobApplicationService jobApplicationService) {
            this.jobApplicationService = jobApplicationService;
        }

        @PostMapping
        public ResponseEntity<JobApplicationResponse> createApplication(
                @Valid @RequestBody JobApplicationRequest request
        ) {
            JobApplicationResponse response = jobApplicationService.createApplication(request);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @GetMapping
        public ResponseEntity<List<JobApplicationResponse>> getAllApplications() {
            List<JobApplicationResponse> applications = jobApplicationService.getAllApplications();
            return ResponseEntity.ok(applications);
        }
        @GetMapping("/status")
        public ResponseEntity<List<JobApplicationResponse>> getApplicationsByStatus(
                @RequestParam("status") ApplicationStatus status
        ) {
            List<JobApplicationResponse> applications =
                    jobApplicationService.getApplicationsByStatus(status);

            return ResponseEntity.ok(applications);
        }
        @GetMapping("/search")
        public ResponseEntity<List<JobApplicationResponse>> searchByCompanyName(
                @RequestParam("companyName") String companyName
        ) {
            return ResponseEntity.ok(
                    jobApplicationService.searchByCompanyName(companyName)
            );
        }
        @GetMapping("/location")
        public ResponseEntity<List<JobApplicationResponse>> getApplicationsByLocation(
                @RequestParam("location") String location
        ) {
            return ResponseEntity.ok(
                    jobApplicationService.getApplicationsByLocation(location)
            );
        }
        @GetMapping("/native/status")
        public ResponseEntity<List<JobApplicationResponse>>
        getApplicationsByStatusNative(
                @RequestParam("status") String status
        ) {
            return ResponseEntity.ok(
                    jobApplicationService.getApplicationsByStatusNative(status)
            );
        }
        @GetMapping("/page")
        public ResponseEntity<Page<JobApplicationResponse>> getApplicationsPage(
                Pageable pageable
        ) {
            return ResponseEntity.ok(
                    jobApplicationService.getApplicationsPage(pageable)
            );
        }

        @GetMapping("/{id}")
        public ResponseEntity<JobApplicationResponse> getApplicationById(@PathVariable Long id) {
            JobApplicationResponse response = jobApplicationService.getApplicationById(id);
            return ResponseEntity.ok(response);
        }

        @PutMapping("/{id}")
        public ResponseEntity<JobApplicationResponse> updateApplication(
                @PathVariable Long id,
                @Valid @RequestBody JobApplicationRequest request
        ) {
            JobApplicationResponse response = jobApplicationService.updateApplication(id, request);
            return ResponseEntity.ok(response);
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {
            jobApplicationService.deleteApplication(id);
            return ResponseEntity.noContent().build();
        }
        @GetMapping("/note-counts")
        public ResponseEntity<List<JobApplicationNoteCountResponse>>
        getApplicationsWithNoteCounts() {

            return ResponseEntity.ok(
                    jobApplicationService.getApplicationsWithNoteCounts()
            );
        }
    }

