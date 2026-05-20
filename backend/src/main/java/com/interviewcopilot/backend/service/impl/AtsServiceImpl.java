package com.interviewcopilot.backend.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewcopilot.backend.dto.request.AtsRequest;
import com.interviewcopilot.backend.dto.response.AtsResponse;
import com.interviewcopilot.backend.model.AtsScore;
import com.interviewcopilot.backend.model.Resume;
import com.interviewcopilot.backend.repository.AtsScoreRepository;
import com.interviewcopilot.backend.repository.ResumeRepository;
import com.interviewcopilot.backend.service.AtsService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AtsServiceImpl implements AtsService {

    private final AtsScoreRepository atsScoreRepository;
    private final ResumeRepository resumeRepository;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public AtsServiceImpl(AtsScoreRepository atsScoreRepository,
                          ResumeRepository resumeRepository,
                          ObjectMapper objectMapper) {
        this.atsScoreRepository = atsScoreRepository;
        this.resumeRepository = resumeRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public AtsResponse analyzeResume(AtsRequest request, String userEmail) {
        try {
            Resume resume = resumeRepository.findByUserIdAndIsActiveTrue(userEmail)
                    .orElseThrow(() -> new RuntimeException("No active resume found. Please upload one first."));

            String resumeText = extractTextFromPdf(resume.getFilePath());
            String aiResponse = callGeminiApi(resumeText, request.getJobDescription());
            AtsScore atsScore = parseAiResponse(aiResponse, userEmail, resume.getId(), request.getJobDescription());
            AtsScore saved = atsScoreRepository.save(atsScore);
            return buildResponse(saved);
        } catch (Exception e) {
            System.err.println("[ERROR] ATS Analysis failed!");
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<AtsResponse> getUserAnalyses(String userEmail) {
        return atsScoreRepository.findByUserId(userEmail)
                .stream()
                .map(this::buildResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AtsResponse getLatestAnalysis(String userEmail) {
        AtsScore latest = atsScoreRepository
                .findTopByUserIdOrderByAnalyzedAtDesc(userEmail)
                .orElseThrow(() -> new RuntimeException("No analysis found"));
        return buildResponse(latest);
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

    private boolean isMockMode() {
        return true;
    }

    private String getMockAtsAnalysis(String resumeText, String jobDescription) {
        String[] possibleKeywords = {
            "Java", "Spring Boot", "REST APIs", "Git", "Maven", "Docker", "Kubernetes", "AWS",
            "Azure", "CI/CD", "React", "Angular", "Vue", "TypeScript", "JavaScript", "Python",
            "SQL", "MongoDB", "PostgreSQL", "Microservices", "Node.js", "Express", "GraphQL",
            "HTML", "CSS", "Linux", "Scrum", "Agile", "Redux", "Jira"
        };

        List<String> matchedKeywords = new ArrayList<>();
        List<String> missingKeywords = new ArrayList<>();

        String resumeLower = resumeText != null ? resumeText.toLowerCase() : "";
        String jdLower = jobDescription != null ? jobDescription.toLowerCase() : "";

        int jdKeywordsCount = 0;
        for (String kw : possibleKeywords) {
            String kwLower = kw.toLowerCase();
            if (jdLower.contains(kwLower)) {
                jdKeywordsCount++;
                if (resumeLower.contains(kwLower)) {
                    matchedKeywords.add(kw);
                } else {
                    missingKeywords.add(kw);
                }
            }
        }

        if (jdKeywordsCount < 3) {
            for (String kw : possibleKeywords) {
                String kwLower = kw.toLowerCase();
                if (resumeLower.contains(kwLower)) {
                    if (matchedKeywords.size() < 5) matchedKeywords.add(kw);
                } else {
                    if (missingKeywords.size() < 4) missingKeywords.add(kw);
                }
            }
        }

        int score = 70;
        int total = matchedKeywords.size() + missingKeywords.size();
        if (total > 0) {
            score = (int) (60.0 + ((double) matchedKeywords.size() / total) * 35.0);
        }
        if (score > 100) score = 100;
        if (score < 30) score = 30;

        String experienceLevel = "Mid Level";
        if (resumeLower.contains("senior") || resumeLower.contains("lead") || resumeLower.contains("architect") || resumeLower.contains("years of experience") || resumeLower.contains("yrs exp")) {
            experienceLevel = "Senior Level";
        } else if (resumeLower.contains("junior") || resumeLower.contains("intern") || resumeLower.contains("fresher") || resumeLower.contains("student")) {
            experienceLevel = "Entry Level";
        }

        String feedback;
        if (matchedKeywords.isEmpty()) {
            feedback = "The resume does not seem to contain the primary technical keywords mentioned in the job description. Consider updating your resume to explicitly reflect your relevant skillset.";
        } else {
            String matchedStr = String.join(", ", matchedKeywords.subList(0, Math.min(3, matchedKeywords.size())));
            String missingStr = missingKeywords.isEmpty() ? "" : String.join(", ", missingKeywords.subList(0, Math.min(3, missingKeywords.size())));
            
            if (missingKeywords.isEmpty()) {
                feedback = "Excellent match! Your resume highlights the core skills required for this role, including " + matchedStr + ". Your experience aligns exceptionally well.";
            } else {
                feedback = "Your resume shows a good foundation with matching skills like " + matchedStr + ". To further improve your score and match, consider highlighting experience with missing skills such as " + missingStr + ".";
            }
        }

        try {
            java.util.Map<String, Object> responseMap = java.util.Map.of(
                "score", score,
                "experienceLevel", experienceLevel,
                "matchedKeywords", matchedKeywords,
                "missingKeywords", missingKeywords,
                "feedback", feedback
            );
            return objectMapper.writeValueAsString(responseMap);
        } catch (Exception e) {
            return """
            {
              "score": 75,
              "experienceLevel": "Mid Level",
              "matchedKeywords": ["Java", "Git"],
              "missingKeywords": ["Docker"],
              "feedback": "Your resume shows solid alignment with the requirements."
            }
            """;
        }
    }

    private String callGeminiApi(String resumeText, String jobDescription) {
        if (isMockMode()) {
            System.out.println("[INFO] Gemini API is in Mock Mode. Generating realistic ATS analysis locally.");
            return getMockAtsAnalysis(resumeText, jobDescription);
        }

        try {
            String prompt = buildPrompt(resumeText, jobDescription);
            var parts = List.of(java.util.Map.of("text", prompt));
            var contents = List.of(java.util.Map.of("parts", parts));
            var bodyMap = java.util.Map.of("contents", contents);
            String requestBody = objectMapper.writeValueAsString(bodyMap);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(
                            "https://generativelanguage.googleapis.com/v1beta/models/" +
                                    "gemini-2.0-flash:generateContent?key=" + geminiApiKey))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(httpRequest,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                return root.path("candidates")
                        .path(0).path("content").path("parts").path(0).path("text").asText();
            } else {
                System.err.println("[WARNING] Gemini API returned error code " + response.statusCode() + ": " + response.body() + ". Falling back to local mock ATS generation.");
                return getMockAtsAnalysis(resumeText, jobDescription);
            }

        } catch (Exception e) {
            System.err.println("[WARNING] Failed to call Gemini API (" + e.getMessage() + "). Falling back to local mock ATS generation.");
            return getMockAtsAnalysis(resumeText, jobDescription);
        }
    }

    private String buildPrompt(String resumeText, String jobDescription) {
        return """
                You are an expert ATS (Applicant Tracking System) analyzer.
                Analyze this resume against the job description and respond ONLY with a valid JSON object.
                No explanation, no markdown, just pure JSON.
                
                RESUME:
                %s
                
                JOB DESCRIPTION:
                %s
                
                Respond with exactly this JSON structure:
                {
                  "score": <number 0-100>,
                  "experienceLevel": "<Entry Level / Mid Level / Senior Level>",
                  "matchedKeywords": ["keyword1", "keyword2", ...],
                  "missingKeywords": ["keyword1", "keyword2", ...],
                  "feedback": "<2-3 sentences of specific actionable improvement advice>"
                }
                """.formatted(resumeText, jobDescription);
    }

    private AtsScore parseAiResponse(String aiResponse, String userEmail,
                                     String resumeId, String jobDescription) {
        try {
            String cleaned = aiResponse.replace("```json", "").replace("```", "").trim();
            JsonNode json = objectMapper.readTree(cleaned);

            int score = json.path("score").asInt(0);
            String experienceLevel = json.path("experienceLevel").asText("Unknown");
            String feedback = json.path("feedback").asText("");

            List<String> matched = new ArrayList<>();
            json.path("matchedKeywords").forEach(n -> matched.add(n.asText()));

            List<String> missing = new ArrayList<>();
            json.path("missingKeywords").forEach(n -> missing.add(n.asText()));

            return AtsScore.builder()
                    .userId(userEmail).resumeId(resumeId).jobDescription(jobDescription)
                    .score(score).experienceLevel(experienceLevel)
                    .matchedKeywords(matched).missingKeywords(missing).feedback(feedback)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response: " + e.getMessage());
        }
    }

    private String getScoreLabel(int score) {
        if (score >= 80) return "Excellent";
        else if (score >= 60) return "Good";
        else if (score >= 40) return "Average";
        else return "Poor";
    }

    private AtsResponse buildResponse(AtsScore ats) {
        return AtsResponse.builder()
                .analysisId(ats.getId()).score(ats.getScore())
                .scoreLabel(getScoreLabel(ats.getScore()))
                .matchedKeywords(ats.getMatchedKeywords())
                .missingKeywords(ats.getMissingKeywords())
                .feedback(ats.getFeedback()).experienceLevel(ats.getExperienceLevel())
                .analyzedAt(ats.getAnalyzedAt() != null ? ats.getAnalyzedAt().toString() : "")
                .build();
    }
}