package com.interviewcopilot.backend.dto.request;

import lombok.Data;

@Data
public class QuestionRequest {
    private String jobRole;
    private int questionCount = 5;
}
