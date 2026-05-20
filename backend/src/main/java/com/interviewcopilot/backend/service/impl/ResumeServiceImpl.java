package com.interviewcopilot.backend.service.impl;

import com.interviewcopilot.backend.dto.response.ResumeUploadResponse;
import com.interviewcopilot.backend.model.Resume;
import com.interviewcopilot.backend.repository.ResumeRepository;
import com.interviewcopilot.backend.service.ResumeService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;

    public ResumeServiceImpl(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    private static final String UPLOAD_DIR = "uploads/resumes/";

    @Override
    public ResumeUploadResponse uploadResume(MultipartFile file, String userEmail) {
        validateFile(file);

        List<Resume> oldResumes = resumeRepository.findByUserId(userEmail);
        oldResumes.forEach(r -> r.setActive(false));
        resumeRepository.saveAll(oldResumes);

        String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get(UPLOAD_DIR);
        Path filePath = uploadPath.resolve(uniqueFileName);

        try {
            Files.createDirectories(uploadPath);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage());
        }

        Resume resume = Resume.builder()
                .userId(userEmail)
                .fileName(file.getOriginalFilename())
                .filePath(filePath.toString())
                .fileSize(formatFileSize(file.getSize()))
                .status("UPLOADED")
                .isActive(true)
                .build();

        Resume saved = resumeRepository.save(resume);
        return buildResponse(saved, "Resume uploaded successfully");
    }

    @Override
    public List<ResumeUploadResponse> getUserResumes(String userEmail) {
        return resumeRepository.findByUserId(userEmail)
                .stream()
                .map(r -> buildResponse(r, ""))
                .collect(Collectors.toList());
    }

    @Override
    public ResumeUploadResponse getActiveResume(String userEmail) {
        Resume resume = resumeRepository.findByUserIdAndIsActiveTrue(userEmail)
                .orElseThrow(() -> new RuntimeException("No active resume found"));
        return buildResponse(resume, "Active resume fetched");
    }

    @Override
    public void deleteResume(String resumeId, String userEmail) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        if (!resume.getUserId().equals(userEmail)) {
            throw new RuntimeException("Unauthorized");
        }

        try {
            Files.deleteIfExists(Paths.get(resume.getFilePath()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + e.getMessage());
        }

        resumeRepository.deleteById(resumeId);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) throw new RuntimeException("Please select a file");
        String name = file.getOriginalFilename();
        if (name == null || !name.toLowerCase().endsWith(".pdf"))
            throw new RuntimeException("Only PDF files are accepted");
        if (file.getSize() > 10 * 1024 * 1024)
            throw new RuntimeException("File size must be under 10MB");
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        else if (bytes < 1024 * 1024) return (bytes / 1024) + " KB";
        else return (bytes / (1024 * 1024)) + " MB";
    }

    private ResumeUploadResponse buildResponse(Resume resume, String message) {
        return ResumeUploadResponse.builder()
                .resumeId(resume.getId())
                .fileName(resume.getFileName())
                .fileSize(resume.getFileSize())
                .status(resume.getStatus())
                .message(message)
                .uploadedAt(resume.getUploadedAt() != null
                        ? resume.getUploadedAt().toString() : "")
                .build();
    }
}