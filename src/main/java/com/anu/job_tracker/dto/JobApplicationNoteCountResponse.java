package com.anu.job_tracker.dto;

public class JobApplicationNoteCountResponse {
    private Long applicationId;
    private String companyName;
    private int noteCount;

    public JobApplicationNoteCountResponse(
            Long applicationId,
            String companyName,
            int noteCount
    ) {
        this.applicationId = applicationId;
        this.companyName = companyName;
        this.noteCount = noteCount;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public int getNoteCount() {
        return noteCount;
    }
}
