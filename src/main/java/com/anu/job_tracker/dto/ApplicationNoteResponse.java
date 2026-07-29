package com.anu.job_tracker.dto;

public class ApplicationNoteResponse {
    private Long id;
    private String content;
    private Long jobApplicationId;

    public ApplicationNoteResponse(
            Long id,
            String content,
            Long jobApplicationId
    ) {
        this.id = id;
        this.content = content;
        this.jobApplicationId = jobApplicationId;
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Long getJobApplicationId() {
        return jobApplicationId;
    }
}
