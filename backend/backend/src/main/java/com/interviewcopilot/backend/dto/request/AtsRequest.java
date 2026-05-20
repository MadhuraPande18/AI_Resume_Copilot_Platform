package com.interviewcopilot.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

// WHY: User sends job description — we already have their resume in MongoDB
public class AtsRequest {

    @NotBlank(message = "Job description is required")
    private String jobDescription;

    public String getJobDescription() { return jobDescription; }
    public void setJobDescription(String jobDescription) { this.jobDescription = jobDescription; }
}