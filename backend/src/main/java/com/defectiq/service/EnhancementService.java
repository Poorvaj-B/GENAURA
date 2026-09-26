package com.defectiq.service;

import com.defectiq.dto.*;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.regex.*;

@Service
public class EnhancementService {
    private final QualityService quality;
    private final DuplicateService duplicates;
    private final OllamaService ollama;

    public EnhancementService(QualityService quality, DuplicateService duplicates, OllamaService ollama) {
        this.quality = quality;
        this.duplicates = duplicates;
        this.ollama = ollama;
    }

    public EnhanceResponse enhance(EnhanceRequest r) {
        if (ollama.enabled()) {
            try {
                AiDraft draft = ollama.enhance(r);
                return fromAiDraft(r, draft);
            } catch (Exception ignored) {
                // Stable hackathon fallback when Ollama is unavailable/malformed.
            }
        }
        return fallback(r);
    }

    private EnhanceResponse fromAiDraft(EnhanceRequest r, AiDraft a) {
        String raw = r.description();
        String title = has(a.title()) ? a.title() : makeTitle(raw);
        List<String> steps = a.stepsToReproduce() == null || a.stepsToReproduce().isEmpty()
                ? steps(raw.toLowerCase()) : a.stepsToReproduce();
        String expected = has(a.expectedResult()) ? a.expectedResult() : expected(raw.toLowerCase());
        String actual = has(a.actualResult()) ? a.actualResult() : raw;

        List<String> missing = new ArrayList<>();
        if (a.missingInformation() != null) missing.addAll(a.missingInformation());
        addMissing(missing, "Environment", r.environment());
        addMissing(missing, "Browser/device", r.browser());
        addMissing(missing, "Application version", r.applicationVersion());

        List<String> questions = a.clarificationQuestions() == null
                ? missing.stream().map(this::question).toList()
                : a.clarificationQuestions();

        String severity = has(a.severity()) ? a.severity().toUpperCase() : severity(raw.toLowerCase());
        String priority = has(a.priority()) ? a.priority().toUpperCase() : priorityFor(severity);
        String repro = has(a.reproducibility()) ? a.reproducibility() : "Unknown – needs confirmation";

        var q = quality.score(raw, title, steps, expected, actual, r.environment(), r.browser(), r.applicationVersion());
        int score = quality.overall(q);

        String summary = has(a.summary()) ? a.summary() : summary(title, raw, r.environment());
        return new EnhanceResponse(title, summary, steps,
                expected == null ? "Not provided – confirm with reporter." : expected, actual,
                safe(r.environment(), "Not provided"), safe(r.browser(), "Not provided"),
                safe(r.applicationVersion(), "Not provided"), severity, priority, repro, score, q,
                missing, questions, duplicates.find(title + " " + raw), "Ollama local LLM");
    }

    private void addMissing(List<String> list, String name, String value) {
        if (!has(value) && !list.contains(name)) list.add(name);
    }

    private EnhanceResponse fallback(EnhanceRequest r) {
        String raw = r.description().trim(), low = raw.toLowerCase();
        String title = has(r.title()) ? r.title().trim() : makeTitle(raw);
        List<String> steps = steps(low);
        String expected = expected(low);
        String actual = raw;

        Matcher m = Pattern.compile("(?i)((?:http\\s*)?\\d{3}\\s*[-:]?\\s*[A-Za-z][^.!?]*|error(?: message)?[:\\-]?\\s*[^.!?]+)").matcher(raw);
        if (m.find()) actual = m.group(1).trim();

        List<String> missing = new ArrayList<>();
        if (steps.size() < 3) missing.add("Detailed reproduction steps");
        if (expected == null) missing.add("Expected result");
        addMissing(missing, "Environment", r.environment());
        addMissing(missing, "Browser/device", r.browser());
        addMissing(missing, "Application version", r.applicationVersion());
        if (!low.matches("(?s).*(error|exception|http\\s*[45]\\d\\d|timeout|failed|failure).*"))
            missing.add("Exact error message / evidence");
        if (!low.matches("(?s).*(always|sometimes|intermittent|once|every time|reproduc).*"))
            missing.add("Reproducibility frequency");

        List<String> questions = missing.stream().map(this::question).toList();
        String sev = severity(low);
        String priority = priorityFor(sev);
        String repro = low.matches("(?s).*(always|every time|100%|cannot login|unable to login).*")
                ? "Always" : "Unknown – needs confirmation";

        var q = quality.score(raw, title, steps, expected, actual,
                r.environment(), r.browser(), r.applicationVersion());
        int score = quality.overall(q);

        return new EnhanceResponse(title, summary(title, raw, r.environment()), steps,
                expected == null ? "Not provided – confirm with reporter." : expected, actual,
                safe(r.environment(), "Not provided"), safe(r.browser(), "Not provided"),
                safe(r.applicationVersion(), "Not provided"), sev, priority, repro, score, q,
                missing, questions, duplicates.find(title + " " + raw), "DefectIQ deterministic AI fallback");
    }

    private String makeTitle(String raw) {
        String t = raw.replaceAll("(?i)please\\s+check.*$", "").trim();
        if (t.length() > 70) t = t.substring(0, 70);
        if (t.toLowerCase().contains("not working"))
            t = "Unable to " + t.replaceFirst("(?i)not working", "").trim();
        return Character.toUpperCase(t.charAt(0)) + t.substring(1);
    }

    private String summary(String title, String raw, String env) {
        String where = has(env) ? env + " environment" : "the application environment";
        return title + " is reported in " + where + ". Original report: \"" +
                (raw.length() > 220 ? raw.substring(0, 220) + "…" : raw) +
                "\". The generated steps and expected/actual results should be verified by the QA engineer before submission.";
    }

    private List<String> steps(String t) {
        if (t.contains("login") || t.contains("sign in") || t.contains("authentication"))
            return List.of("Open the application.", "Navigate to the Login/Sign in page.",
                    "Enter valid credentials.", "Submit the login form.", "Observe the reported behavior.");
        if (t.contains("payment") || t.contains("pay") || t.contains("transaction"))
            return List.of("Open the application.", "Navigate to the Payments/Transaction flow.",
                    "Enter valid payment details.", "Submit the transaction and complete OTP if prompted.",
                    "Observe the transaction status and message.");
        if (t.contains("upload"))
            return List.of("Open the application.", "Navigate to the upload screen.",
                    "Select a valid file.", "Start the upload.", "Observe the upload result.");
        return List.of("Open the affected application/module.", "Navigate to the area where the issue occurs.",
                "Perform the action described in the report.", "Observe the resulting behavior.");
    }

    private String expected(String t) {
        if (t.contains("login") || t.contains("sign in"))
            return "The user should be successfully authenticated and redirected to the application landing page.";
        if (t.contains("payment") || t.contains("pay") || t.contains("transaction"))
            return "The payment/transaction should complete successfully and a confirmation should be displayed.";
        if (t.contains("upload"))
            return "The selected file should upload successfully and the UI should show completion.";
        if (t.contains("save"))
            return "The entered data should be saved successfully and a confirmation should be displayed.";
        return null;
    }

    private String severity(String t) {
        if (t.matches("(?s).*(data loss|security breach|production down|system down|all users|cannot login).*"))
            return "CRITICAL";
        if (t.matches("(?s).*(payment|transaction|login|authentication|crash|500|unable|cannot).*"))
            return "HIGH";
        if (t.matches("(?s).*(slow|delay|intermittent|incorrect|validation).*"))
            return "MEDIUM";
        return "LOW";
    }

    private String priorityFor(String severity) {
        return switch (severity) {
            case "CRITICAL" -> "P0";
            case "HIGH" -> "P1";
            case "MEDIUM" -> "P2";
            default -> "P3";
        };
    }

    private String question(String f) {
        return switch (f) {
            case "Expected result" -> "What should have happened instead?";
            case "Environment" -> "Which environment did this occur in (DEV/QA/UAT/PROD)?";
            case "Browser/device" -> "Which browser, OS, or device was used?";
            case "Application version" -> "What application/build version was tested?";
            case "Exact error message / evidence" -> "What exact error message, HTTP status, request ID, or screenshot was observed?";
            case "Reproducibility frequency" -> "Does the issue happen always, sometimes, or only once?";
            default -> "Please provide detailed " + f.toLowerCase() + ".";
        };
    }

    private boolean has(String s) { return s != null && !s.isBlank(); }
    private String safe(String s, String fallback) { return has(s) ? s.trim() : fallback; }
}
