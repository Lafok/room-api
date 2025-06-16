package com.example.roomapi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GeminiService {

    private final WebClient webClient;
    private final String apiKey;

    public GeminiService(WebClient.Builder builder,
                         @Value("${GEMINI_KEY}") String apiKey) {
        this.apiKey = apiKey;
        this.webClient = builder
                .baseUrl("https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent")
                .build();
    }

    public String chatWithGemini(String userMessage) {
        String requestBody = """
        {
          "contents": [
            {
              "parts": [
                {
                  "text": "%s"
                }
              ]
            }
          ]
        }
        """.formatted(userMessage);

        try {
            return webClient.post()
                    .uri(uriBuilder -> uriBuilder.queryParam("key", apiKey).build())
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(this::extractTextFromResponse)
                    .block();
        } catch (Exception e) {
            e.printStackTrace(); // <-- print full error to console/log
            return "⚠️ Gemini error occurred: " + e.getMessage();
        }
    }

    private String extractTextFromResponse(String json) {
        try {
            int startIndex = json.indexOf("\"text\":\"") + 8;
            int endIndex = json.indexOf("\"", startIndex);
            return json.substring(startIndex, endIndex).replace("\\n", "\n");
        } catch (Exception e) {
            return "⚠️ Failed to parse Gemini response.";
        }
    }
}


