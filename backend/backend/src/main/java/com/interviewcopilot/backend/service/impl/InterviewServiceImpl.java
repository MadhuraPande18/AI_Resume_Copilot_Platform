package com.interviewcopilot.backend.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewcopilot.backend.dto.request.QuestionRequest;
import com.interviewcopilot.backend.dto.response.QuestionResponse;
import com.interviewcopilot.backend.model.InterviewQuestion;
import com.interviewcopilot.backend.model.Resume;
import com.interviewcopilot.backend.repository.InterviewQuestionRepository;
import com.interviewcopilot.backend.repository.ResumeRepository;
import com.interviewcopilot.backend.service.InterviewService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InterviewServiceImpl implements InterviewService {

    private final InterviewQuestionRepository questionRepository;
    private final ResumeRepository resumeRepository;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.model}")
    private String geminiModel;

    public InterviewServiceImpl(InterviewQuestionRepository questionRepository,
                                ResumeRepository resumeRepository,
                                ObjectMapper objectMapper) {
        this.questionRepository = questionRepository;
        this.resumeRepository = resumeRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<QuestionResponse> generateQuestions(QuestionRequest request, String userEmail) {
        Resume resume = resumeRepository.findByUserIdAndIsActiveTrue(userEmail)
                .orElseThrow(() -> new RuntimeException("No active resume found. Please upload one first."));

        String resumeText = extractTextFromPdf(resume.getFilePath());
        String aiResponse = callGeminiApi(resumeText, request.getJobRole(), request.getQuestionCount());
        
        List<InterviewQuestion> questions = parseAiResponse(aiResponse, userEmail, resume.getId(), request.getJobRole());
        
        // Save all generated questions
        List<InterviewQuestion> savedQuestions = questionRepository.saveAll(questions);
        
        return savedQuestions.stream()
                .map(this::buildResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuestionResponse> getUserQuestions(String userEmail) {
        return questionRepository.findByUserId(userEmail)
                .stream()
                .map(this::buildResponse)
                .collect(Collectors.toList());
    }

    private String extractTextFromPdf(String filePath) {
        File file = Paths.get(filePath).toAbsolutePath().toFile();

        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            if (text == null || text.trim().isEmpty()) {
                throw new RuntimeException("Could not extract text from PDF");
            }
            return text.trim();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read PDF: " + e.getMessage());
        }
    }

    private String callGeminiApi(String resumeText, String jobRole, int count) {
        System.out.println("[INFO] Local Mock Mode Active. Generating realistic interview questions locally.");
        
        return """
        [
          {
            "question": "Can you walk me through a challenging problem you solved in your recent projects using modern software engineering patterns?",
            "category": "Behavioral",
            "recommendedAnswer": "Start with the STAR method (Situation, Task, Action, Result). Highlight your specific contribution and the architectural layout."
          },
          {
            "question": "How do you manage concurrency, thread pooling, and database connection leaks under high traffic environments in a Spring Boot environment?",
            "category": "Technical",
            "recommendedAnswer": "Explain the role of HikariCP, @Async thread pool executors, and why caching patterns prevent high CPU footprints."
          },
          {
            "question": "Why did you choose MongoDB over a traditional relational database like PostgreSQL for storing ATS Keyword Audit statistics?",
            "category": "Technical",
            "recommendedAnswer": "Highlight the flexible schema layout, fast read throughput of JSON-based document storage, and why it fits our dynamic scorecard structure."
          }
        ]
        """;
    }

    private String buildPrompt(String resumeText, String jobRole, int count) {
        return """
                You are an expert technical interviewer.
                Based on the provided resume and the target job role, generate %d interview questions.
                Make them a mix of Behavioral, Technical, and Experience-based questions.
                Respond ONLY with a valid JSON array containing objects.
                No explanation, no markdown, just pure JSON array.
                
                RESUME:
                %s
                
                JOB ROLE:
                %s
                
                Respond with exactly this JSON structure:
                [
                  {
                    "question": "<The interview question>",
                    "category": "<Behavioral | Technical | Experience>",
                    "recommendedAnswer": "<A brief hint or structure on how the candidate should answer based on their resume>"
                  }
                ]
                """.formatted(count, resumeText, jobRole);
    }

    private List<InterviewQuestion> parseAiResponse(String aiResponse, String userEmail, String resumeId, String jobRole) {
        try {
            String cleaned = aiResponse.replace("```json", "").replace("```", "").trim();
            JsonNode arrayNode = objectMapper.readTree(cleaned);
            
            List<InterviewQuestion> questions = new ArrayList<>();
            if (arrayNode.isArray()) {
                for (JsonNode node : arrayNode) {
                    questions.add(InterviewQuestion.builder()
                            .userId(userEmail)
                            .resumeId(resumeId)
                            .jobRole(jobRole)
                            .question(node.path("question").asText(""))
                            .category(node.path("category").asText(""))
                            .recommendedAnswer(node.path("recommendedAnswer").asText(""))
                            .generatedAt(LocalDateTime.now())
                            .build());
                }
            }
            return questions;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response: " + e.getMessage());
        }
    }

    private QuestionResponse buildResponse(InterviewQuestion question) {
        return QuestionResponse.builder()
                .id(question.getId())
                .question(question.getQuestion())
                .category(question.getCategory())
                .recommendedAnswer(question.getRecommendedAnswer())
                .jobRole(question.getJobRole())
                .build();
    }
}
