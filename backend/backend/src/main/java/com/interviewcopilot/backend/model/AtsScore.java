package com.interviewcopilot.backend.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "ats_scores")
public class AtsScore {

    @Id
    private String id;

    private String userId;
    private String resumeId;
    private String jobDescription;
    private int score;                    // 0-100
    private List<String> matchedKeywords;
    private List<String> missingKeywords;
    private String feedback;              // AI generated feedback
    private String experienceLevel;       // Entry/Mid/Senior

    @CreatedDate
    private LocalDateTime analyzedAt;

    // ── Constructor ──────────────────────────
    public AtsScore() {}

    // ── Builder ──────────────────────────────
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String userId;
        private String resumeId;
        private String jobDescription;
        private int score;
        private List<String> matchedKeywords;
        private List<String> missingKeywords;
        private String feedback;
        private String experienceLevel;

        public Builder userId(String v) { this.userId = v; return this; }
        public Builder resumeId(String v) { this.resumeId = v; return this; }
        public Builder jobDescription(String v) { this.jobDescription = v; return this; }
        public Builder score(int v) { this.score = v; return this; }
        public Builder matchedKeywords(List<String> v) { this.matchedKeywords = v; return this; }
        public Builder missingKeywords(List<String> v) { this.missingKeywords = v; return this; }
        public Builder feedback(String v) { this.feedback = v; return this; }
        public Builder experienceLevel(String v) { this.experienceLevel = v; return this; }

        public AtsScore build() {
            AtsScore a = new AtsScore();
            a.userId = this.userId;
            a.resumeId = this.resumeId;
            a.jobDescription = this.jobDescription;
            a.score = this.score;
            a.matchedKeywords = this.matchedKeywords;
            a.missingKeywords = this.missingKeywords;
            a.feedback = this.feedback;
            a.experienceLevel = this.experienceLevel;
            return a;
        }
    }

    // ── Getters & Setters ─────────────────────
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getResumeId() { return resumeId; }
    public void setResumeId(String resumeId) { this.resumeId = resumeId; }
    public String getJobDescription() { return jobDescription; }
    public void setJobDescription(String jobDescription) { this.jobDescription = jobDescription; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public List<String> getMatchedKeywords() { return matchedKeywords; }
    public void setMatchedKeywords(List<String> matchedKeywords) { this.matchedKeywords = matchedKeywords; }
    public List<String> getMissingKeywords() { return missingKeywords; }
    public void setMissingKeywords(List<String> missingKeywords) { this.missingKeywords = missingKeywords; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }
    public LocalDateTime getAnalyzedAt() { return analyzedAt; }
    public void setAnalyzedAt(LocalDateTime analyzedAt) { this.analyzedAt = analyzedAt; }
}