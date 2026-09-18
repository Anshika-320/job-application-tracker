package com.anu.job_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ApplicationNoteRequest {
    @NotBlank(message = "Note content is required")
    @Size(max = 500, message = "Note content must be 500 characters or fewer")
    private String content;

    public ApplicationNoteRequest() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
