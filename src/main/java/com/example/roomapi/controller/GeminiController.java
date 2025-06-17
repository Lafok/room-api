package com.example.roomapi.controller;


import com.example.roomapi.service.GeminiService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/google-gemini")
public class GeminiController {

    private final GeminiService GeminiService;

    public GeminiController(GeminiService GeminiService) {
        this.GeminiService = GeminiService;
    }

    public record ChatResponse(String message) {}
}