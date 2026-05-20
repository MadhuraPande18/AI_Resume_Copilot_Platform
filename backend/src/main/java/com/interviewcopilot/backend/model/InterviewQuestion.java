package com.interviewcopilot.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "interview_questions")
public class InterviewQuestion {
    @Id
    private String id;
    private String userId;
    private String resumeId;
    private String jobRole;
    
    private String question;
    private String category; // e.g., "Behavioral", "Technical", "Experience"
    private String recommendedAnswer;
    
    @Builder.Default
    private LocalDateTime generatedAt = LocalDateTime.now();
}
