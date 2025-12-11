package com.ai_insight_service.log_analyzer.dto;

import java.util.List;

public record LogAnalysisResponse(
        String summary,
        List<String> keyErrors,
        List<String> probableCauses,
        List<String> suggestedNextSteps
        ) {}