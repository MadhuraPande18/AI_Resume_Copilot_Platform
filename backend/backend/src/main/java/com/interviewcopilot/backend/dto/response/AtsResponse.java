package com.interviewcopilot.backend.dto.response;

import java.util.List;

public class AtsResponse {

    private String analysisId;
    private int score;
    private String scoreLabel;            // "Excellent" / "Good" / "Average" / "Poor"
    private List<String> matchedKeywords;
    private List<String> missingKeywords;
    private String feedback;
    private String experienceLevel;
    private String analyzedAt;

    // ── Builder ──────────────────────────────
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String analysisId;
        private int score;
        private String scoreLabel;
        private List<String> matchedKeywords;
        private List<String> missingKeywords;
        private String feedback;
        private String experienceLevel;
        private String analyzedAt;

        public Builder analysisId(String v) { this.analysisId = v; return this; }
        public Builder score(int v) { this.score = v; return this; }
        public Builder scoreLabel(String v) { this.scoreLabel = v; return this; }
        public Builder matchedKeywords(List<String> v) { this.matchedKeywords = v; return this; }
        public Builder missingKeywords(List<String> v) { this.missingKeywords = v; return this; }
        public Builder feedback(String v) { this.feedback = v; return this; }
        public Builder experienceLevel(String v) { this.experienceLevel = v; return this; }
        public Builder analyzedAt(String v) { this.analyzedAt = v; return this; }

        public AtsResponse build() {
            AtsResponse r = new AtsResponse();
            r.analysisId = this.analysisId;
            r.score = this.score;
            r.scoreLabel = this.scoreLabel;
            r.matchedKeywords = this.matchedKeywords;
            r.missingKeywords = this.missingKeywords;
            r.feedback = this.feedback;
            r.experienceLevel = this.experienceLevel;
            r.analyzedAt = this.analyzedAt;
            return r;
        }
    }

    // ── Getters ──────────────────────────────
    public String getAnalysisId() { return analysisId; }
    public int getScore() { return score; }
    public String getScoreLabel() { return scoreLabel; }
    public List<String> getMatchedKeywords() { return matchedKeywords; }
    public List<String> getMissingKeywords() { return missingKeywords; }
    public String getFeedback() { return feedback; }
    public String getExperienceLevel() { return experienceLevel; }
    public String getAnalyzedAt() { return analyzedAt; }
}