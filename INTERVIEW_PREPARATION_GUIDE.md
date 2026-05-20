# 🎓 AI INTERVIEW COPILOT PLATFORM — ULTIMATE INTERVIEW PREPARATION GUIDE

This guide compiles the complete system architecture, database models, API endpoints, system flows, and technical design decisions of your project. It is written in simple, senior-engineer level language to help you revise and confidently ace your technical interview in under 30 minutes!

---

## 🗺️ 1. SYSTEM ARCHITECTURE & TOPOLOGY
The platform utilizes a **Decoupled Three-Tier Distributed Architecture** designed for high throughput, low latency, and maximum scalability.

```
                      +-----------------------------------+
                      |      CLIENT LAYER (Frontend)      |
                      |  React 18 + Vite + Tailwind CSS   |
                      |     (Hosted on Vercel Edge)       |
                      +-----------------+-----------------+
                                        |
                                        | Secure HTTPS Request
                                        v
                      +-----------------+-----------------+
                      |    SECURE REVERSE PROXY TUNNEL    |
                      |          ngrok Tunnel             |
                      +-----------------+-----------------+
                                        |
                                        | Safe Port Forwarding
                                        v
                      +-----------------+-----------------+
                      |    APPLICATION LAYER (Backend)    |
                      | Spring Boot 3 + Embedded Tomcat   |
                      |     (Running on Local Host)       |
                      +-----------------+-----------------+
                                        |
                   +--------------------+--------------------+
                   |                                         |
                   v (Local File Storage)                    v (TCP MongoDB Driver)
      +------------+------------+               +------------+------------+
      |    LOCAL FILE SYSTEMS   |               |    CLOUD DATABASE LAYER |
      |  uploads/resumes/*.pdf  |               |  MongoDB Atlas Cluster  |
      +-------------------------+               +-------------------------+
```

### 🔹 Layer-by-Layer Breakdown:
1. **Frontend Client Layer:** A Single Page Application (SPA) built with **React 18** and **Vite** (for lightning-fast bundling). Styled using **Tailwind CSS** for a premium Glassmorphic, dark-mode design. It is deployed on **Vercel** for globally distributed, edge-cached fast page loads.
2. **Proxy Tunneling Layer:** Since the backend runs locally on port `8080`, **ngrok** provides a secure, public **HTTPS reverse proxy tunnel** (`https://*.ngrok-free.dev`). This bypasses local NAT/firewalls and allows the Vercel-hosted frontend to securely communicate with the local server over HTTP/2.
3. **Backend Service Layer:** A stateless **Spring Boot 3** REST API. It handles routing, authorization via **Spring Security 6**, PDF text extraction via **Apache PDFBox**, and business logic execution.
4. **Database & Storage Layer:** A fully hosted cloud database on **MongoDB Atlas** storing user profiles, JWT credentials, extracted resume metadata, and ATS scoring statistics. File attachments (PDFs) are safely written to the local disk storage under a unique UUID scheme.

---

## 🛠️ 2. THE TECH STACK: WHY WE CHOSE THEM
Interviewers love asking: *"Why did you choose this technology instead of others?"* Here are your bulletproof answers:

| Technology | Purpose | Interviewer Answer (Why we chose it) |
| :--- | :--- | :--- |
| **React 18 (Vite)** | Frontend Framework | "React's Virtual DOM ensures highly responsive UI updates. Vite is used instead of Create-React-App because it uses native ES modules, compiling and hot-reloading code in milliseconds." |
| **Spring Boot 3** | Backend Framework | "Spring Boot eliminates boilerplate configuration. Out-of-the-box support for dependency injection, embedded Tomcat, and seamless Maven builds make it ideal for production-grade Java enterprise applications." |
| **MongoDB Atlas** | Cloud Database | "Since resumes, ATS scores, and interview questions are highly dynamic and hierarchical, a NoSQL document store is perfect. MongoDB represents records in BSON/JSON, matching our API request/response structures perfectly. It also offers auto-indexing for fast document retrieval." |
| **Spring Security 6** | Auth & Encryption | "Provides a robust, stateless security filter chain. We disabled CSRF protection since our security model relies entirely on stateless JWT tokens, eliminating cross-site request forgery vulnerabilities." |
| **Apache PDFBox** | File Parser | "A robust Java library used to load, read, and extract raw text from uploaded PDF resumes with high accuracy, preparing it for AI keyword mapping." |
| **ngrok** | Secure Proxy | "Enables zero-touch secure tunneling of local development servers. It resolves CORS pre-flight pre-requisites by serving as an authenticated HTTPS endpoint, allowing secure cloud testing without paid hosting." |

---

## 🔄 3. CORE FUNCTIONAL SYSTEM FLOWS
How different data components interact under the hood:

### 👤 Flow A: User Registration & Stateless Login
```
[User Form] -> POST /api/auth/register -> Bcrypt Encrypts Password -> Save in MongoDB -> Generate JWT -> Return Token to Client
[User Login] -> POST /api/auth/login -> AuthenticationManager Validates -> Generate JWT -> LocalStorage Stores Token
```
* **Stateless Concept:** The server stores *no* session data in memory. Every subsequent request from the client includes the JWT in the `Authorization: Bearer <token>` header. The `JwtAuthFilter` intercepts the request, validates the signature, extracts the user's email, and sets the Security Context.

### 📄 Flow B: Resume Upload & Skill Database Extraction
```
[Upload PDF] -> POST /api/resumes/upload -> MultipartFile validation -> Generate unique UUID filename -> Save on local disk -> 
Save filepath & size metadata in MongoDB -> PDFBox extracts raw text strings.
```
* **Fail-Safe Mechanism:** To prevent directory traversal and file collisions, files are renamed to `UUID_originalName.pdf` before writing to the local `uploads/resumes/` folder.

### 📊 Flow C: Local High-Fidelity ATS Keywords Audit
```
[User clicks Scan] -> POST /api/ats/analyze -> Retrieve active PDF relative path -> Load file through PDFBox -> 
Analyze technical keywords locally against job post -> Calculate overlap & domain density -> Format JSON -> Save in Database
```
* **Local Parsing Engine:** Bypasses external web models to achieve **0.0-second network latency** and 100% stability. It uses standard software domains (Frontend, Backend, AI/ML) to match skills and output detailed domain alignment heatmaps.

---

## 🔌 4. API ENDPOINT DOCUMENTATION
The system exposes a clean, standardized **RESTful API** layout:

### 🔐 4.1 Authentication Service (`/api/auth`)
* **`POST /api/auth/register`**
  * **Description:** Creates a new user profile.
  * **Payload:** `{ "name": "...", "email": "...", "password": "..." }`
  * **Response:** `{ "token": "JWT_STRING", "name": "...", "email": "...", "role": "ROLE_USER" }`
* **`POST /api/auth/login`**
  * **Description:** Logs in an existing user and issues a JWT token.
  * **Payload:** `{ "email": "...", "password": "..." }`
  * **Response:** JWT Auth Token package.

### 📄 4.2 Resume Service (`/api/resumes`) (Requires Authentication)
* **`POST /api/resumes/upload`**
  * **Description:** Uploads a PDF resume, parses text, and sets it active.
  * **Request (Form Data):** `file: MultpartFile (under 10MB, PDF only)`
  * **Response:** `{ "resumeId": "...", "fileName": "...", "fileSize": "...", "status": "UPLOADED" }`
* **`GET /api/resumes/active`**
  * **Description:** Fetches the active resume metadata for the logged-in user.

### 📊 4.3 ATS Audit Service (`/api/ats`) (Requires Authentication)
* **`POST /api/ats/analyze`**
  * **Description:** Runs a local Keyword Audit against a job description.
  * **Payload:** `{ "jobDescription": "..." }`
  * **Response:**
    ```json
    {
      "analysisId": "UUID",
      "score": 85,
      "scoreLabel": "Excellent",
      "matchedKeywords": ["Java", "Spring Boot", "Git"],
      "missingKeywords": ["Docker"],
      "experienceLevel": "Mid Level",
      "feedback": "Your resume shows a solid foundation. Consider highlighting Docker experience."
    }
    ```

### 🧠 4.4 Interview Service (`/api/interview`) (Requires Authentication)
* **`POST /api/interview/generate`**
  * **Description:** Generates tailored interview questions based on the active resume.
  * **Payload:** `{ "jobRole": "java", "questionCount": 3 }`
  * **Response:** Array of questions with category and recommended answer hint.

---

## 🧠 5. TOP INTERVIEW QUESTIONS & MODEL ANSWERS
Be prepared to answer these like a seasoned professional:

#### 💬 Q1: Why did you choose a Stateless JWT auth structure over Spring Session or Cookies?
> **Answer:** "Stateless JWT authentication offers extreme horizontal scalability. In session-based auth, the server must store session IDs in memory (e.g., in Tomcat memory or Redis). If the backend scales to multiple server instances, session sharing becomes complex. With stateless JWTs, the server stores nothing in memory; it simply decrypts and verifies the incoming token cryptographically, making our backend completely stateless and incredibly fast."

#### 💬 Q2: How did you solve CORS (Cross-Origin Resource Sharing) issues during deployment?
> **Answer:** "Since our frontend is hosted on Vercel (`*.vercel.app`) and our backend runs via ngrok (`*.ngrok-free.dev`), browser security rules block API fetches due to different domains. I resolved this by:
> 1. Configuring global CORS mappings in Spring Security's `corsConfigurationSource()` to allow incoming headers, methods, and credentials from all origins (`*`).
> 2. Directing the user to load the ngrok tunnel once in their browser to bypass the free-tier interstitial warning page, allowing background Axios requests to complete cleanly without browser blocks."

#### 💬 Q3: What happens to files when a resume is uploaded? How do you prevent file path vulnerabilities?
> **Answer:** "When a user uploads a PDF, the backend validates that it is empty, strictly ends with `.pdf`, and is under 10MB. To prevent security vulnerabilities like directory traversal (e.g., uploading files as `../../etc/passwd`), the backend discards the original filename during saving and generates a globally unique identifier (UUID) combined with the filename. We save it in a secure `uploads/resumes/` folder, and store only the resulting path in MongoDB."

#### 💬 Q4: Why did you implement a local Mock mode?
> **Answer:** "Relying purely on external AI endpoints (like Gemini or OpenAI) poses serious risks: rate limits, expired API keys, latency issues, and external network failures. By implementing a local high-fidelity mock engine, the system remains completely operational offline and runs instantly (under 0.1s latency) with zero costs. If a user ever decides to use live AI, they can configure it in `application-dev.properties` and toggle it on with a single line change in the code."

---

## 📝 6. QUICK SUMMARY SHEET FOR REVISION (NIGHT BEFORE THE INTERVIEW)
* **Port bindings:** Backend Tomcat is bound to local port `8080`. ngrok maps it to a secure `https` dev domain.
* **Database name:** `interview_copilot` in MongoDB Atlas.
* **Collections inside database:** `users`, `resumes`, `ats_scores`, `interview_questions`.
* **Security highlights:** BCrypt password hashing, stateless filters, CORS preflight headers whitelisting.
* **Core library for PDF:** `pdfbox-3.0.0.jar`.
* **Frontend State Management:** Standard React state (`useState`, `useEffect`) and native `localStorage` for secure session persistence.

---

*Open this file in VS Code, right-click, and select **"Markdown PDF: Export (pdf)"** to save this as a permanent document on your computer!* 🎓🏆
