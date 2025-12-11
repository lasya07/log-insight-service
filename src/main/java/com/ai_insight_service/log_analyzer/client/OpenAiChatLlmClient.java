package com.ai_insight_service.log_analyzer.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Component
public class OpenAiChatLlmClient implements LlmClient{
    private final WebClient.Builder builder;

    @Value("${openai.base-url}")
    private String baseUrl;

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    @Override
    public String generate(String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("OpenAI API key is not configured");
        }
        WebClient webClient = builder.baseUrl(baseUrl).build();
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", new Object[]{
                        Map.of("role", "system", "content", "You are a JSON-only assistant."),
                        Map.of("role", "user", "content", prompt)
                }
        );
        try {
            Map<String, Object> response = webClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(String.class).map(body -> {
                                log.error("OpenAI error status={} body={}", clientResponse.statusCode(), body);
                                return new WebClientResponseException(
                                        "Error response from OpenAI",
                                        clientResponse.statusCode().value(),
                                        clientResponse.statusCode().toString(),
                                        null,
                                        body.getBytes(),
                                        null
                                );
                            })
                    )
                    .bodyToMono(Map.class)
                    .block();

            if (response == null || !response.containsKey("choices")) {
                throw new IllegalStateException("Invalid response from OpenAI");
            }
            var choices = (java.util.List<Map<String, Object>>) response.get("choices");
            if (choices.isEmpty()) {
                throw new IllegalStateException("No choices returned from OpenAI");
            }

            var message = (Map<String, Object>) choices.get(0).get("message");
            Object content = message.get("content");
            return content != null ? content.toString() : "";
        } catch (WebClientResponseException e) {
            log.error("OpenAI HTTP error: status={} body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error calling OpenAI", e);
            throw new RuntimeException("Unexpected error calling OpenAI", e);
        }
    }
}
