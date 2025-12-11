package com.ai_insight_service.log_analyzer.service;

import com.ai_insight_service.log_analyzer.client.LlmClient;
import com.ai_insight_service.log_analyzer.dto.LogAnalysisResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class LogAnalysisServiceImpl implements LogAnalysisService{
    private final LlmClient llmClient;
    private final ObjectMapper objectMapper;
    @Override
    public LogAnalysisResponse analyzeLogs(String logs) {
        String prompt = """
            You are an expert backend engineer.
            Analyze the following application logs and return a JSON object with this exact structure:
            {
              "summary": "string",
              "keyErrors": ["string"],
              "probableCauses": ["string"],
              "suggestedNextSteps": ["string"]
            }

            STRICT REQUIREMENTS:
            - Return ONLY valid JSON.
            - No markdown, no code fences, no extra text.
            - Do not wrap JSON in ```.

            Logs:
            %s
            """.formatted(logs);
        try {
            String llmContent = llmClient.generate(prompt);
            log.info("LLM raw content: {}", llmContent);
            Map<String, Object> json = objectMapper.readValue(llmContent, new TypeReference<Map<String, Object>>() {});
            String summary = (String) json.getOrDefault("summary", "");
            List<String> keyErrors = castList(json.get("keyErrors"));
            List<String> probableCauses = castList(json.get("probableCauses"));
            List<String> suggestedNextSteps = castList(json.get("suggestedNextSteps"));
            return new LogAnalysisResponse(
                    summary,
                    keyErrors,
                    probableCauses,
                    suggestedNextSteps
            );
        } catch (Exception e) {
            log.error("Failed to analyze logs via LLM, returning fallback response", e);
            // Fallback instead of 500
            return new LogAnalysisResponse(
                    "Failed to parse AI response. See server logs for details.",
                    List.of(),
                    List.of(),
                    List.of("Check application logs for the raw LLM response and error.")
            );
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> castList(Object value) {
        if (value == null) {
            return List.of();
        }
        if (value instanceof List<?> list) {
            return (List<String>) list.stream()
                    .map(String::valueOf)
                    .toList();
        }
        return List.of(String.valueOf(value));
    }
}
