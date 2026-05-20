package com.interviewcopilot.backend.service;

import com.interviewcopilot.backend.dto.request.AtsRequest;
import com.interviewcopilot.backend.dto.response.AtsResponse;

import java.util.List;

public interface AtsService {
    AtsResponse analyzeResume(AtsRequest request, String userEmail);
    List<AtsResponse> getUserAnalyses(String userEmail);
    AtsResponse getLatestAnalysis(String userEmail);
}