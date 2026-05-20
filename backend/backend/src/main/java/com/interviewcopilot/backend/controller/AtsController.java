package com.interviewcopilot.backend.controller;

import com.interviewcopilot.backend.dto.request.AtsRequest;
import com.interviewcopilot.backend.dto.response.AtsResponse;
import com.interviewcopilot.backend.security.JwtService;
import com.interviewcopilot.backend.service.impl.AtsServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ats")
@CrossOrigin(origins = "*")
public class AtsController {

    private final AtsServiceImpl atsService;
    private final JwtService jwtService;

    public AtsController(AtsServiceImpl atsService, JwtService jwtService) {
        this.atsService = atsService;
        this.jwtService = jwtService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<AtsResponse> analyze(
            @Valid @RequestBody AtsRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String userEmail = extractEmail(authHeader);
        return ResponseEntity.ok(
                atsService.analyzeResume(request, userEmail)
        );
    }

    @GetMapping("/history")
    public ResponseEntity<List<AtsResponse>> getHistory(
            @RequestHeader("Authorization") String authHeader) {
        String userEmail = extractEmail(authHeader);
        return ResponseEntity.ok(atsService.getUserAnalyses(userEmail));
    }

    @GetMapping("/latest")
    public ResponseEntity<AtsResponse> getLatest(
            @RequestHeader("Authorization") String authHeader) {
        String userEmail = extractEmail(authHeader);
        return ResponseEntity.ok(atsService.getLatestAnalysis(userEmail));
    }

    private String extractEmail(String authHeader) {
        return jwtService.extractEmail(authHeader.substring(7));
    }
}