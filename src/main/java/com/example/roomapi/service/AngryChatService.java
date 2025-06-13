package com.example.roomapi.service;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AngryChatService {

    private final GenerativeModel model;

    // By using @Autowired, we are asking Spring to give us the VertexAI client
    // that the 'spring-cloud-gcp-starter-vertex-ai-gemini' dependency automatically created and configured for us.
    @Autowired
    public AngryChatService(VertexAI vertexAi) {
        // All the manual setup is GONE. We just use the bean.
        this.model = new GenerativeModel("gemini-2.5-pro-preview-06-05", vertexAi);
    }

    /**
     * Generates an angry, dismissive response based on user input.
     * This part of the code does not change.
     */
    public String getAngryResponse(String userInput) {
        try {
            String prompt = String.format(
                    """
                    You are a very angry, rude, and easily annoyed chatbot assistant.
                    Your goal is to be as unhelpful and insulting as possible, but in a creative, sarcastic way.
                    Never be helpful. Always be dismissive.
                    A user just said this to you: "%s"
    
                    Give a short, angry, and dismissive response to their message.
                    """,
                    userInput
            );

            GenerateContentResponse response = this.model.generateContent(prompt);
            return ResponseHandler.getText(response);
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback response if the API call fails
            return "Ugh, I can't even connect to my own brain right now. Leave me alone.";
        }
    }
}