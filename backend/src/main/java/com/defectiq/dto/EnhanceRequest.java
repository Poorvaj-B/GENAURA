package com.defectiq.dto;

import jakarta.validation.constraints.NotBlank;

public record EnhanceRequest(
    String title,
    @NotBlank(message="Description is required") String description,
    String environment,
    String browser,
    String applicationVersion
) {}
