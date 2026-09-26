package com.defectiq.dto;

import java.util.List;

public record AiDraft(
    String title,
    String summary,
    List<String> stepsToReproduce,
    String expectedResult,
    String actualResult,
    String severity,
    String priority,
    String reproducibility,
    List<String> missingInformation,
    List<String> clarificationQuestions
) {}
