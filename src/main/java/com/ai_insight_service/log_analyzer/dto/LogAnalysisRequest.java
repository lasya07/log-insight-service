package com.ai_insight_service.log_analyzer.dto;

import jakarta.validation.constraints.NotBlank;

public record LogAnalysisRequest(
        @NotBlank(message = "logs must not be blank")
        String logs) {}