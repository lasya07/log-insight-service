package com.ai_insight_service.log_analyzer.service;

import com.ai_insight_service.log_analyzer.dto.LogAnalysisResponse;

public interface LogAnalysisService {
    LogAnalysisResponse analyzeLogs(String logs);
}