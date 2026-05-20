package com.interviewcopilot.backend.controller;

import com.interviewcopilot.backend.dto.request.QuestionRequest;
import com.interviewcopilot.backend.dto.response.QuestionResponse;
import com.interviewcopilot.backend.service.InterviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interview")
@CrossOrigin(origins = "*")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @PostMapping("/generate")
    public ResponseEntity<List<QuestionResponse>> generateQuestions(
            @RequestBody QuestionRequest request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        List<QuestionResponse> response = interviewService.generateQuestions(request, userEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<QuestionResponse>> getUserQuestions(Authentication authentication) {
        String userEmail = authentication.getName();
        List<QuestionResponse> response = interviewService.getUserQuestions(userEmail);
        return ResponseEntity.ok(response);
    }
}
