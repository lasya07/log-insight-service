package com.ai_insight_service.log_analyzer.controller;

import com.ai_insight_service.log_analyzer.dto.LogAnalysisRequest;
import com.ai_insight_service.log_analyzer.dto.LogAnalysisResponse;
import com.ai_insight_service.log_analyzer.service.LogAnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/logs")
public class LogAnalysisController {

    private final LogAnalysisService logAnalysisService;

    @PostMapping("/analyze")
    public ResponseEntity<LogAnalysisResponse> analyze(@Valid @RequestBody LogAnalysisRequest request) {
        LogAnalysisResponse response = logAnalysisService.analyzeLogs(request.logs());
        return ResponseEntity.ok(response);
    }
}