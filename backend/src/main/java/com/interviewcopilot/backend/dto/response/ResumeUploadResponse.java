package com.interviewcopilot.backend.dto.response;

public class ResumeUploadResponse {

    private String resumeId;
    private String fileName;
    private String fileSize;
    private String status;
    private String message;
    private String uploadedAt;

    // ── Constructor ──────────────────────────
    public ResumeUploadResponse() {}

    // ── Builder ──────────────────────────────
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String resumeId;
        private String fileName;
        private String fileSize;
        private String status;
        private String message;
        private String uploadedAt;

        public Builder resumeId(String resumeId) { this.resumeId = resumeId; return this; }
        public Builder fileName(String fileName) { this.fileName = fileName; return this; }
        public Builder fileSize(String fileSize) { this.fileSize = fileSize; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder uploadedAt(String uploadedAt) { this.uploadedAt = uploadedAt; return this; }

        public ResumeUploadResponse build() {
            ResumeUploadResponse r = new ResumeUploadResponse();
            r.resumeId = this.resumeId;
            r.fileName = this.fileName;
            r.fileSize = this.fileSize;
            r.status = this.status;
            r.message = this.message;
            r.uploadedAt = this.uploadedAt;
            return r;
        }
    }

    // ── Getters ──────────────────────────────
    public String getResumeId() { return resumeId; }
    public String getFileName() { return fileName; }
    public String getFileSize() { return fileSize; }
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public String getUploadedAt() { return uploadedAt; }

    // ── Setters ──────────────────────────────
    public void setResumeId(String resumeId) { this.resumeId = resumeId; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setFileSize(String fileSize) { this.fileSize = fileSize; }
    public void setStatus(String status) { this.status = status; }
    public void setMessage(String message) { this.message = message; }
    public void setUploadedAt(String uploadedAt) { this.uploadedAt = uploadedAt; }
}