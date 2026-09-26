package com.defectiq.service;

import com.defectiq.dto.AiDraft;
import com.defectiq.dto.EnhanceRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class OllamaService {
    private final boolean enabled;
    private final String model;
    private final RestClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public OllamaService(
            @Value("${defectiq.ollama.enabled:false}") boolean enabled,
            @Value("${defectiq.ollama.base-url:http://localhost:11434}") String base,
            @Value("${defectiq.ollama.model:llama3.2}") String model) {
        this.enabled = enabled;
        this.model = model;
        this.client = RestClient.builder().baseUrl(base).build();
    }

    public boolean enabled() {
        return enabled;
    }

    public AiDraft enhance(EnhanceRequest r) throws Exception {
        String prompt = """
                You are a senior software QA analyst.
                Convert the raw defect into a developer-ready defect report.
                Return ONLY valid JSON. No markdown and no explanation.
                Required JSON keys:
                title, summary, stepsToReproduce, expectedResult, actualResult,
                severity, priority, reproducibility, missingInformation,
                clarificationQuestions.
                stepsToReproduce, missingInformation and clarificationQuestions must be JSON arrays.
                Do not invent environment, browser or application version.

                Raw title: %s
                Raw description: %s
                Environment: %s
                Browser/device: %s
                Application version: %s
                """.formatted(
                String.valueOf(r.title()),
                r.description(),
                String.valueOf(r.environment()),
                String.valueOf(r.browser()),
                String.valueOf(r.applicationVersion())
        );

        Map<?, ?> response = client.post()
                .uri("/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("model", model, "prompt", prompt, "stream", false))
                .retrieve()
                .body(Map.class);

        String text = String.valueOf(response.get("response")).trim();
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start < 0 || end <= start) {
            throw new IllegalStateException("Ollama did not return JSON");
        }

        JsonNode node = mapper.readTree(text.substring(start, end + 1));
        return mapper.treeToValue(node, AiDraft.class);
    }
}
