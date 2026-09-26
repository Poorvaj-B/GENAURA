# DefectIQ – AI-Powered Defect Description Enhancer

Hackathon-ready full-stack prototype for the TCS Technology Day problem statement:
**IT Quality Assurance: Automated Defect Description Enhancer**

## Features
- Raw defect input
- AI-style defect enhancement
- Structured title, summary, steps, expected/actual result
- Missing-information detection
- Clarification questions
- Quality score from 0–100
- Quality breakdown
- Severity and priority suggestions
- Reproducibility assessment
- Near-duplicate detection
- Defect history using H2
- Optional Ollama integration point
- Works without any API key using a deterministic fallback

## Tech stack
- Frontend: React + Vite
- Backend: Java 17 + Spring Boot
- Database: H2
- REST APIs
- Optional local AI: Ollama

## Run

### Backend
Requirements: Java 17+ and Maven 3.9+

```bash
cd backend
mvn spring-boot:run
```

Backend: http://localhost:8080

### Frontend
Requirements: Node.js 20+

```bash
cd frontend
npm install
npm run dev
```

Frontend: http://localhost:5173

The Vite server proxies `/api` to Spring Boot.

## Optional local LLM
The default demo works without a model. To connect Ollama later:

```bash
ollama pull llama3.2
```

Set:

```text
OLLAMA_ENABLED=true
OLLAMA_BASE_URL=http://localhost:11434
OLLAMA_MODEL=llama3.2
```

The application still falls back safely if the local model is unavailable.

## Demo input
Use:

> Payment is not working after OTP. Please check ASAP.

Then click **Enhance with AI**.

## Main APIs

`POST /api/defects/enhance`

`POST /api/defects`

`GET /api/defects`

`GET /api/defects/{id}`

`GET /api/health`

## Recommended hackathon extensions
1. Jira integration
2. Screenshot/vision analysis
3. Embedding-based duplicate detection
4. GitHub Issues integration
5. CSV/Jira export
6. Team quality analytics
