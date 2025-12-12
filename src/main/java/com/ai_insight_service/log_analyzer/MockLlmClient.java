package com.ai_insight_service.log_analyzer;

import com.ai_insight_service.log_analyzer.client.LlmClient;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
public class MockLlmClient implements LlmClient {
    @Override
    public String generate(String prompt) {
        // Return static JSON that your service can parse
        return """
        {
          "summary": "Mock analysis of logs. OpenAI not called (no quota).",
          "keyErrors": [
            "ERROR: Simulated DB timeout"
          ],
          "probableCauses": [
            "Database under heavy load",
            "Network latency or misconfigured pool size"
          ],
          "suggestedNextSteps": [
            "Check DB connections and pool config",
            "Review recent deployments affecting DB",
            "Add retry with backoff for transient errors"
          ]
        }
        """;
    }
}
