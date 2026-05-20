package com.interviewcopilot.backend.service;

import com.interviewcopilot.backend.dto.request.QuestionRequest;
import com.interviewcopilot.backend.dto.response.QuestionResponse;

import java.util.List;

public interface InterviewService {
    List<QuestionResponse> generateQuestions(QuestionRequest request, String userEmail);
    List<QuestionResponse> getUserQuestions(String userEmail);
}
