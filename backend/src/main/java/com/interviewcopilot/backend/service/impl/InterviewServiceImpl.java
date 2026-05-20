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
        File file = new File(filePath).getAbsoluteFile();

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

    private String getMockQuestions(String jobRole, int count) {
        List<java.util.Map<String, String>> pool = new ArrayList<>();
        String roleLower = jobRole != null ? jobRole.toLowerCase() : "";

        // 1. DSA (Data Structures & Algorithms)
        if (roleLower.contains("dsa") || roleLower.contains("algorithm") || roleLower.contains("structure") || roleLower.contains("leetcode") || roleLower.contains("coding")) {
            pool.add(java.util.Map.of(
                "question", "Given an array of integers, how do you find two numbers that add up to a specific target? Explain the optimal time and space complexity.",
                "category", "Technical - DSA",
                "recommendedAnswer", "Explain the Two Sum problem. The optimal solution uses a Hash Map (Dictionary) to store elements and their indices. As we traverse the array, we check if the complement (target - current) exists in the map. Time complexity is O(N) and Space complexity is O(N) because we visit each element at most once and store elements in the hash map."
            ));
            pool.add(java.util.Map.of(
                "question", "Explain the difference between a Breadth-First Search (BFS) and Depth-First Search (DFS) traversal of a graph, and when you would use each.",
                "category", "Experience - DSA",
                "recommendedAnswer", "Explain that BFS uses a Queue (FIFO) and explores neighbors level-by-level, making it optimal for finding the shortest path in unweighted graphs. DFS uses a Stack (LIFO or recursion) and explores deep down a branch before backtracking, making it optimal for connectivity, path finding, and cycle detection."
            ));
            pool.add(java.util.Map.of(
                "question", "How do you detect a cycle in a singly linked list? Describe Floyd's Cycle-Finding Algorithm.",
                "category", "Technical - DSA",
                "recommendedAnswer", "Explain Floyd's Tortoise and Hare algorithm. Use two pointers: a slow pointer moving 1 step at a time, and a fast pointer moving 2 steps. If there is a cycle, the fast pointer will eventually catch up and meet the slow pointer. If the fast pointer reaches the end (null), there is no cycle. Time complexity: O(N), Space complexity: O(1)."
            ));
            pool.add(java.util.Map.of(
                "question", "When would you prefer a Binary Search Tree (BST) over a Hash Table, and what are their search time complexities?",
                "category", "Experience - DSA",
                "recommendedAnswer", "Explain that Hash Tables have average O(1) search time but do not maintain order. BSTs (especially self-balancing trees like AVL or Red-Black) have O(log N) search time but keep elements in sorted order, enabling fast range queries, finding min/max, and sorted traversals."
            ));
            pool.add(java.util.Map.of(
                "question", "Tell me about a challenging algorithmic bottleneck you encountered in a project. How did you optimize its Big-O performance?",
                "category", "Behavioral",
                "recommendedAnswer", "Use the STAR method: describe a slow nested-loop operation (O(N^2)) handling large datasets, the profiling tools used to spot the delay, the refactoring to a Hash Map or pre-sorting (O(N log N) or O(N)), and the resulting 90%+ decrease in execution time."
            ));

        // 2. AIML / Data Science
        } else if (roleLower.contains("aiml") || roleLower.contains("ai") || roleLower.contains("ml") || roleLower.contains("machine") || roleLower.contains("data science") || roleLower.contains("deep") || roleLower.contains("neural") || roleLower.contains("nlp")) {
            pool.add(java.util.Map.of(
                "question", "Explain the Bias-Variance tradeoff in machine learning. How does it relate to overfitting and underfitting?",
                "category", "Technical - AI/ML",
                "recommendedAnswer", "Define Bias as errors from erroneous assumptions in the learning algorithm (causes underfitting). Define Variance as sensitivity to small fluctuations in the training set (causes overfitting). The tradeoff states that as model complexity increases, bias decreases but variance increases. The goal is to find the sweet spot that minimizes total error."
            ));
            pool.add(java.util.Map.of(
                "question", "Explain the concept of overfitting in deep neural networks and three distinct ways you can prevent it.",
                "category", "Technical - AI/ML",
                "recommendedAnswer", "Overfitting occurs when a neural network memorizes training noise instead of generalizing. Prevent it using: 1. Dropout (randomly deactivating neurons during training), 2. L1/L2 Regularization (penalizing large weights), and 3. Early Stopping (terminating training when validation loss starts increasing)."
            ));
            pool.add(java.util.Map.of(
                "question", "What is the difference between Precision and Recall? In a medical diagnosis context, which metric is more critical to maximize?",
                "category", "Technical - AI/ML",
                "recommendedAnswer", "Precision is True Positives divided by total predicted positives (TP / (TP + FP)). Recall is True Positives divided by actual positives (TP / (TP + FN)). In medical diagnosis, maximizing Recall is critical because we want to minimize false negatives (failing to diagnose an active disease)."
            ));
            pool.add(java.util.Map.of(
                "question", "How do you handle extensive data preprocessing, missing values, and extreme class imbalance in a production machine learning pipeline?",
                "category", "Experience - AI/ML",
                "recommendedAnswer", "Discuss: 1. Cleaning data and using median imputation or predictive models for missing entries, 2. Feature scaling (standardization), and 3. Addressing class imbalance using SMOTE oversampling, downsampling, or using cost-sensitive learning algorithms with F1-score evaluation instead of accuracy."
            ));
            pool.add(java.util.Map.of(
                "question", "Tell me about a time when your trained model performed with 99% accuracy in training but failed miserably in production testing. How did you resolve it?",
                "category", "Behavioral",
                "recommendedAnswer", "Use the STAR method: explain discovering data leakage (e.g. including target indicators in training features) or dataset shift between training and real-world distributions. Detail how you isolated variables, retrained on clean data, and set up data validation pipelines."
            ));

        // 3. React
        } else if (roleLower.contains("react") || roleLower.contains("redux") || roleLower.contains("hooks") || roleLower.contains("context")) {
            pool.add(java.util.Map.of(
                "question", "Explain the Virtual DOM in React, how the reconciliation process works, and the purpose of the 'key' prop.",
                "category", "Technical - React",
                "recommendedAnswer", "Explain that React maintains a lightweight representation of the UI in memory (Virtual DOM). When state changes, React generates a new virtual tree, compares it with the old one (diffing algorithm), and bats updates to apply only the changes to the real DOM (Reconciliation). The 'key' prop helps React uniquely identify elements in lists to avoid re-rendering unchanged items."
            ));
            pool.add(java.util.Map.of(
                "question", "What is the difference between useEffect, useMemo, and useCallback hooks? When should you use each?",
                "category", "Technical - React",
                "recommendedAnswer", "useEffect runs side effects (APIs, subscriptions) after component render. useMemo memoizes the *result of a calculation* to avoid expensive recalculations. useCallback memoizes the *callback function itself* to prevent child components from re-rendering due to changing function references on every render."
            ));
            pool.add(java.util.Map.of(
                "question", "How do you approach state management in a large-scale React application? Compare Context API and Redux/Zustand.",
                "category", "Experience - React",
                "recommendedAnswer", "Discuss using React local state for isolated UI components, Context API for global read-only variables (like themes/locale) to avoid prop-drilling, and external state stores like Redux Toolkit or Zustand for highly interactive, rapid state modifications to prevent massive Context re-render issues."
            ));
            pool.add(java.util.Map.of(
                "question", "Describe a complex UI animation or page performance bottleneck you had to optimize in React. What tools and techniques did you use?",
                "category", "Behavioral",
                "recommendedAnswer", "Explain diagnosing lag using React DevTools Profiler, finding excessive re-renders, and fixing them using React.memo, windowing for large lists (react-window), lazy loading components (`React.lazy`), and debouncing input handlers."
            ));

        // 4. Node.js
        } else if (roleLower.contains("node") || roleLower.contains("express") || roleLower.contains("npm")) {
            pool.add(java.util.Map.of(
                "question", "Explain the Node.js Event Loop. How does asynchronous non-blocking I/O work under the hood?",
                "category", "Technical - Node.js",
                "recommendedAnswer", "Explain that Node.js runs on a single-threaded execution model using the V8 engine and libuv library. When an asynchronous operation (like file reading or API call) starts, libuv delegates it to the OS kernel or a worker thread thread-pool. Once completed, the callback is pushed to the callback queue and executed by the event loop during its phases (timers, poll, check), keeping the main thread free to handle new requests."
            ));
            pool.add(java.util.Map.of(
                "question", "What are Streams in Node.js, and how do they differ from standard fs.readFile operations?",
                "category", "Technical - Node.js",
                "recommendedAnswer", "Streams let you read/write data chunk-by-chunk in a continuous flow, instead of loading the entire file into buffer memory. fs.readFile loads the whole file into RAM, which crashes the server for gigabyte-sized files. Streams process data progressively, maintaining a low memory footprint (ideal for media serving or file uploads)."
            ));
            pool.add(java.util.Map.of(
                "question", "How do you implement robust error-handling, input validation, and security headers in an Express backend production application?",
                "category", "Experience - Node.js",
                "recommendedAnswer", "Detail: 1. Using centralized async error middleware to catch and format exceptions, 2. Implementing input validation schemas (using Joi or Zod) to filter request bodies, and 3. Securing HTTP headers using Helmet, rate-limiting using express-rate-limit, and preventing SQL/NoSQL injection."
            ));
            pool.add(java.util.Map.of(
                "question", "Describe a memory leak or database pool exhaustion issue you faced in Node.js. How did you debug and resolve it?",
                "category", "Behavioral",
                "recommendedAnswer", "Use the STAR method: describe a slow API that eventually crashed under load, utilizing Chrome DevTools or memory heap dumps to track unclosed event listeners or database connections, fixing it by cleaning up intervals and configuring connection pool limits in the client."
            ));

        // 5. Frontend & JavaScript
        } else if (roleLower.contains("front") || roleLower.contains("html") || roleLower.contains("css") || roleLower.contains("js") || roleLower.contains("javascript") || roleLower.contains("ts") || roleLower.contains("typescript")) {
            pool.add(java.util.Map.of(
                "question", "What is a closure in JavaScript, and can you provide a practical engineering example of its application?",
                "category", "Technical - Frontend",
                "recommendedAnswer", "A closure is the combination of a function bundled together with references to its surrounding state (lexical environment). Closures allow an inner function to access variables from an outer function even after the outer function has returned. A classic use case is data privacy (encapsulation) to create private variables or function factories."
            ));
            pool.add(java.util.Map.of(
                "question", "Explain the difference between absolute, relative, fixed, and sticky positioning in CSS. How do you design robust responsive grids?",
                "category", "Technical - CSS",
                "recommendedAnswer", "Relative positions elements relative to its normal flow. Absolute positions elements relative to the nearest positioned ancestor. Fixed positions relative to the viewport (remains in place). Sticky toggles between relative and fixed depending on scroll position. Responsive grids are built using CSS Grid and Flexbox with media queries and clamp() functions for fluid layouts."
            ));
            pool.add(java.util.Map.of(
                "question", "How do you optimize a website's Performance metrics (Core Web Vitals) like LCP (Largest Contentful Paint) and CLS (Cumulative Layout Shift)?",
                "category", "Experience - Frontend",
                "recommendedAnswer", "Discuss optimizing LCP by compressing/modernizing images (WebP/AVIF), using content delivery networks (CDNs), and deferring non-critical JS. Prevent CLS by always declaring explicit width/height dimensions on media files and reserving layout spaces for dynamic components."
            ));
            pool.add(java.util.Map.of(
                "question", "Why is TypeScript preferred over vanilla JavaScript in large codebases, and how do you avoid structural 'any' traps?",
                "category", "Experience - Frontend",
                "recommendedAnswer", "TypeScript introduces static typing, catching compile-time bugs and providing rich auto-completions. To avoid 'any' traps, configure strict:true in tsconfig, use 'unknown' for unpredictable API payloads, and leverage union types and type guards to narrow variable structures safely."
            ));

        // 6. Fullstack, Databases & System Design
        } else if (roleLower.contains("full") || roleLower.contains("db") || roleLower.contains("sql") || roleLower.contains("nosql") || roleLower.contains("postgres") || roleLower.contains("mongo") || roleLower.contains("system")) {
            pool.add(java.util.Map.of(
                "question", "Compare Relational Databases (SQL) and Document Databases (NoSQL). When would you choose PostgreSQL over MongoDB, and vice versa?",
                "category", "Technical - System Design",
                "recommendedAnswer", "Relational databases (SQL like PostgreSQL) have structured tables, strict schemas, and support complex joins and strong ACID guarantees (ideal for financial transactions). Document databases (NoSQL like MongoDB) have dynamic JSON schemas, scale horizontally easily, and are ideal for unstructured datasets, rapid prototyping, and high write speeds."
            ));
            pool.add(java.util.Map.of(
                "question", "What is database indexing, how does a B-Tree index work under the hood, and what are the negative performance impacts of over-indexing?",
                "category", "Technical - Databases",
                "recommendedAnswer", "An index is a data structure (typically a B-Tree) that speeds up data retrieval queries. It maintains a sorted representation of the column data, enabling logarithmic search time O(log N) instead of linear scan O(N). Negative impacts: over-indexing slows down write operations (INSERT, UPDATE, DELETE) because every index tree must be updated, and consumes excessive disk space."
            ));
            pool.add(java.util.Map.of(
                "question", "What are ACID properties in database transactions? Explain each property.",
                "category", "Technical - Databases",
                "recommendedAnswer", "ACID stands for: 1. Atomicity (all operations in a transaction succeed or all fail), 2. Consistency (ensures database transitions from one valid state to another, upholding constraints), 3. Isolation (concurrent transactions execute independently without interfering), 4. Durability (committed changes survive system failures)."
            ));
            pool.add(java.util.Map.of(
                "question", "Describe how you design a secure stateless JWT user session management and authentication flow in a full-stack system.",
                "category", "Experience - Fullstack",
                "recommendedAnswer", "Explain the flow: user logs in -> server validates credentials and signs an access token (JWT) with short TTL (e.g. 15m) and a secure refresh token (stored as HTTP-Only secure cookie) -> client stores JWT in memory -> client sends JWT in Bearer Authorization header -> server verifies signature stateless. When expired, client requests new JWT using the refresh token."
            ));
            pool.add(java.util.Map.of(
                "question", "Describe a scenario where you had to design the data model and architecture for a highly concurrent system feature. How did you prevent race conditions?",
                "category", "Behavioral",
                "recommendedAnswer", "Use the STAR method: describe designing a seat booking or wallet transaction system, applying optimistic locking (@Version in JPA) or pessimistic locking (SELECT FOR UPDATE) to prevent double-spending/race conditions, and caching active records in Redis to decrease load."
            ));

        // 7. General Software Engineering & OOP
        } else {
            pool.add(java.util.Map.of(
                "question", "Explain the SOLID design principles and how you have applied them in your object-oriented software projects.",
                "category", "Technical - OOP",
                "recommendedAnswer", "Briefly define: 1. Single Responsibility (a class has one reason to change), 2. Open/Closed (open for extension, closed for modification), 3. Liskov Substitution (subtypes must be substitutable for base types), 4. Interface Segregation (clients shouldn't be forced to depend on unused interfaces), 5. Dependency Inversion (depend on abstractions, not concretions). Provide an example of using interfaces to decouple classes."
            ));
            pool.add(java.util.Map.of(
                "question", "How do you approach writing clean, maintainable unit and integration tests? What is your strategy for mocking dependencies?",
                "category", "Experience - Testing",
                "recommendedAnswer", "Discuss writing isolated unit tests testing single methods, using Mockito to mock repository or external API calls, verifying side effects, writing comprehensive integration tests testing database transaction rollbacks, and targeting edge-cases/null-inputs."
            ));
            pool.add(java.util.Map.of(
                "question", "What are the core differences between a monolithic architecture and a microservices architecture? When should a startup adopt microservices?",
                "category", "Technical - System Design",
                "recommendedAnswer", "Monolith stores all modules in a single codebase/deployment (simple to build, fast execution). Microservices split modules into independent services communicating via APIs (decoupled, scalable, polyglot). Startups should almost always start with a monolith for rapid iteration, and only transition to microservices when scaling boundaries are clear and teams grow."
            ));
            pool.add(java.util.Map.of(
                "question", "Describe a situation where you had a strong disagreement with a senior engineer or team member on a technical decision. How did you resolve it?",
                "category", "Behavioral",
                "recommendedAnswer", "Use the STAR method: emphasize active listening to understand their perspectives, researching objective data (performance benchmarks, community standards), building a small POC (Proof of Concept) to test both options side-by-side, and arriving at a compromise that prioritized the product's health."
            ));
        }

        // Shuffle the pool to provide randomized variety!
        java.util.Collections.shuffle(pool);

        // Limit the selection to the requested 'count'
        List<java.util.Map<String, String>> selection = pool.subList(0, java.lang.Math.min(count, pool.size()));

        // Serialize to compliant JSON
        try {
            return objectMapper.writeValueAsString(selection);
        } catch (Exception e) {
            return """
            [
              {
                "question": "Explain the SOLID design principles and how you have applied them in your projects.",
                "category": "Technical",
                "recommendedAnswer": "Briefly define Single Responsibility, Open/Closed, Liskov Substitution, Interface Segregation, and Dependency Inversion."
              }
            ]
            """;
        }
    }

    private String callGeminiApi(String resumeText, String jobRole, int count) {
        if (isMockMode()) {
            System.out.println("[INFO] Gemini API is in Mock Mode. Generating realistic questions locally.");
            return getMockQuestions(jobRole, count);
        }

        try {
            String prompt = buildPrompt(resumeText, jobRole, count);
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
                System.err.println("[WARNING] Gemini API returned error code " + response.statusCode() + ": " + response.body() + ". Falling back to local mock generation.");
                return getMockQuestions(jobRole, count);
            }

        } catch (Exception e) {
            System.err.println("[WARNING] Failed to call Gemini API (" + e.getMessage() + "). Falling back to local mock generation.");
            return getMockQuestions(jobRole, count);
        }
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
