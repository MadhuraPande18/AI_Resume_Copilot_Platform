package com.interviewcopilot.backend.controller;

import com.interviewcopilot.backend.dto.response.ResumeUploadResponse;
import com.interviewcopilot.backend.security.JwtService;
import com.interviewcopilot.backend.service.ResumeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/resumes")
@CrossOrigin(origins = "*")
public class ResumeController {

    private final ResumeService resumeService;
    private final JwtService jwtService;

    public ResumeController(ResumeService resumeService, JwtService jwtService) {
        this.resumeService = resumeService;
        this.jwtService = jwtService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ResumeUploadResponse> upload(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String authHeader) {

        String userEmail = extractEmail(authHeader);
        return ResponseEntity.ok(resumeService.uploadResume(file, userEmail));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ResumeUploadResponse>> getAll(
            @RequestHeader("Authorization") String authHeader) {

        String userEmail = extractEmail(authHeader);
        return ResponseEntity.ok(resumeService.getUserResumes(userEmail));
    }

    @GetMapping("/active")
    public ResponseEntity<ResumeUploadResponse> getActive(
            @RequestHeader("Authorization") String authHeader) {

        String userEmail = extractEmail(authHeader);
        return ResponseEntity.ok(resumeService.getActiveResume(userEmail));
    }

    @DeleteMapping("/{resumeId}")
    public ResponseEntity<String> delete(
            @PathVariable String resumeId,
            @RequestHeader("Authorization") String authHeader) {

        String userEmail = extractEmail(authHeader);
        resumeService.deleteResume(resumeId, userEmail);
        return ResponseEntity.ok("Resume deleted successfully");
    }

    private String extractEmail(String authHeader) {
        String token = authHeader.substring(7);
        return jwtService.extractEmail(token);
    }
}