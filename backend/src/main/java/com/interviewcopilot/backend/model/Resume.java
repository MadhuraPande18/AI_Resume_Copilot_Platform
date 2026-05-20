package com.interviewcopilot.backend.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "resumes")
public class Resume {

    @Id
    private String id;
    private String userId;
    private String fileName;
    private String filePath;
    private String fileSize;
    private String status;
    private boolean isActive;

    @CreatedDate
    private LocalDateTime uploadedAt;

    // ── Constructor ──────────────────────────
    public Resume() {}

    // ── Builder Pattern (manual) ─────────────
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String userId;
        private String fileName;
        private String filePath;
        private String fileSize;
        private String status;
        private boolean isActive = true;

        public Builder userId(String userId) { this.userId = userId; return this; }
        public Builder fileName(String fileName) { this.fileName = fileName; return this; }
        public Builder filePath(String filePath) { this.filePath = filePath; return this; }
        public Builder fileSize(String fileSize) { this.fileSize = fileSize; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder isActive(boolean isActive) { this.isActive = isActive; return this; }

        public Resume build() {
            Resume r = new Resume();
            r.userId = this.userId;
            r.fileName = this.fileName;
            r.filePath = this.filePath;
            r.fileSize = this.fileSize;
            r.status = this.status;
            r.isActive = this.isActive;
            return r;
        }
    }

    // ── Getters ──────────────────────────────
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getFileName() { return fileName; }
    public String getFilePath() { return filePath; }
    public String getFileSize() { return fileSize; }
    public String getStatus() { return status; }
    public boolean isActive() { return isActive; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }

    // ── Setters ──────────────────────────────
    public void setId(String id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public void setFileSize(String fileSize) { this.fileSize = fileSize; }
    public void setStatus(String status) { this.status = status; }
    public void setActive(boolean active) { isActive = active; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}