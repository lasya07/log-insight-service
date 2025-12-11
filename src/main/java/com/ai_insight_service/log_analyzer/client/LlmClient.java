package com.ai_insight_service.log_analyzer.client;

public interface LlmClient {
    /**
     * Sends a prompt to the LLM and returns the raw response text.
     */
    String generate(String prompt);
}
