package com.defectiq.dto;

import java.util.List;

public record EnhanceResponse(
    String title, String summary, List<String> stepsToReproduce,
    String expectedResult, String actualResult, String environment,
    String browser, String applicationVersion, String severity,
    String priority, String reproducibility, Integer qualityScore,
    QualityBreakdown qualityBreakdown, List<String> missingInformation,
    List<String> clarificationQuestions, List<DuplicateMatch> duplicateMatches,
    String aiProvider
) {
    public record QualityBreakdown(Integer clarity,Integer completeness,Integer reproducibility,
        Integer expectedActual,Integer environment,Integer evidence) {}
    public record DuplicateMatch(Long id,String title,Integer qualityScore,Double similarity) {}
}
