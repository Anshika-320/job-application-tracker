package com.anu.job_tracker.entity;

import jakarta.persistence.*;

    @Entity
    @Table(name = "application_notes")
    public class ApplicationNote {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "content", nullable = false, length = 500)
        private String content;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "job_application_id", nullable = false)
        private JobApplication jobApplication;

        public ApplicationNote() {
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public JobApplication getJobApplication() {
            return jobApplication;
        }

        public void setJobApplication(JobApplication jobApplication) {
            this.jobApplication = jobApplication;
        }
    }
