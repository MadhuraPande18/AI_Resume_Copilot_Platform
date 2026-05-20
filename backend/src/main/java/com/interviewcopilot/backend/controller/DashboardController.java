package com.interviewcopilot.backend.controller;

import com.interviewcopilot.backend.dto.response.DashboardAnalyticsResponse;
import com.interviewcopilot.backend.model.AtsScore;
import com.interviewcopilot.backend.repository.AtsScoreRepository;
import com.interviewcopilot.backend.repository.InterviewQuestionRepository;
import com.interviewcopilot.backend.repository.ResumeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final ResumeRepository resumeRepository;
    private final AtsScoreRepository atsScoreRepository;
    private final InterviewQuestionRepository questionRepository;

    public DashboardController(ResumeRepository resumeRepository,
                               AtsScoreRepository atsScoreRepository,
                               InterviewQuestionRepository questionRepository) {
        this.resumeRepository = resumeRepository;
        this.atsScoreRepository = atsScoreRepository;
        this.questionRepository = questionRepository;
    }

    @GetMapping("/analytics")
    public ResponseEntity<DashboardAnalyticsResponse> getAnalytics(Authentication authentication) {
        String userEmail = authentication.getName();

        long totalResumes = resumeRepository.findByUserIdAndIsActiveTrue(userEmail).isPresent() ? 1 : 0;
        // In a real scenario, this might count all historical resumes, but we just want to know if they have one active.

        List<AtsScore> atsScores = atsScoreRepository.findByUserId(userEmail);
        double avgScore = atsScores.stream()
                .mapToInt(AtsScore::getScore)
                .average()
                .orElse(0.0);

        long totalQuestions = questionRepository.findByUserId(userEmail).size();

        DashboardAnalyticsResponse response = DashboardAnalyticsResponse.builder()
                .totalResumes(totalResumes)
                .averageAtsScore(Math.round(avgScore * 100.0) / 100.0)
                .totalQuestionsGenerated(totalQuestions)
                .build();

        return ResponseEntity.ok(response);
    }
}
