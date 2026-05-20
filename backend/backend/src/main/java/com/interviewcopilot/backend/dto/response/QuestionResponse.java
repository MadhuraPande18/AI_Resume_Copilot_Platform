package com.interviewcopilot.backend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionResponse {
    private String id;
    private String question;
    private String category;
    private String recommendedAnswer;
    private String jobRole;
}
