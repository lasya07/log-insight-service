# ai-log-insight-service
A small side-project where I turned raw application logs into structured, helpful insights using Spring Boot + a pluggable LLM layer.

This service exposes a simple REST endpoint where you can paste logs, and it returns:

1. A short summary of what happened
2. Key errors extracted from the logs
3. Probable causes
4. Suggested next steps for debugging

It’s meant to feel like pairing with an experienced backend engineer who reads your logs and tells you what’s going on.

###  Why I Built This

I wanted something practical that:
* Uses modern Java (21) + Spring Boot 3.5
* Integrates with LLMs (OpenAI or mock)
* Has a clean architecture that can be extended easily
* Is useful for real-world debugging scenarios

Instead of pasting logs into ChatGPT manually, this project turns the idea into an API you can plug into CI pipelines, monitoring tools, internal dashboards, or automation bots.

### How It Works
The service follows a simple, modular flow:

1. A client sends a POST request to /api/logs/analyze with raw logs.
2. The controller receives the request and forwards the log text to LogAnalysisService.
3. The service prepares a structured prompt and calls the LlmClient interface.
4. The active LlmClient implementation runs:
   * MockLlmClient (default): returns a static JSON response.
   * OpenAiChatLlmClient: sends the prompt to the OpenAI Chat Completions API using WebClient.
5. The LLM's JSON-only response is parsed using Jackson.
6. The service maps the structured fields into a LogAnalysisResponse record.
7. The controller returns this JSON back to the client.

This design allows:
* Clean separation between API, logic, and LLM integration.
* Easy swapping between real AI and mock implementation.
* Zero external dependencies in local development.
* Future support for multiple LLM providers.

Here is the conceptual flow:

Client → REST Controller → LogAnalysisService → LlmClient → (OpenAI or Mock) → Structured JSON Result

###What I Want to Add Next

This is just the beginning. In the future, I’d like to explore:

* A small React UI for pasting logs
* Docker support
* GitHub Actions pipeline
* Connecting this to a Slack bot
* Using embeddings or RAG for deeper analysis
* Supporting multiple logs / stack traces