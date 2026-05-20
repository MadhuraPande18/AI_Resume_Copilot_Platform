package com.interviewcopilot.backend.service;

import com.interviewcopilot.backend.dto.response.ResumeUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResumeService {
    ResumeUploadResponse uploadResume(MultipartFile file, String userEmail);
    List<ResumeUploadResponse> getUserResumes(String userEmail);
    ResumeUploadResponse getActiveResume(String userEmail);
    void deleteResume(String resumeId, String userEmail);
}