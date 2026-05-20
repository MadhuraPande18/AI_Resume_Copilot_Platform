package com.interviewcopilot.backend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardAnalyticsResponse {
    private long totalResumes;
    private double averageAtsScore;
    private long totalQuestionsGenerated;
}
