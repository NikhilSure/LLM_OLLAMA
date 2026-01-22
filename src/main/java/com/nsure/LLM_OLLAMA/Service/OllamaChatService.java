package com.nsure.LLM_OLLAMA.Service;

import com.nsure.LLM_OLLAMA.Client.OllamaClient;
import com.nsure.LLM_OLLAMA.DTO.OllamaGenerateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.http.HttpResponse;

@Service
public class OllamaChatService {


    @Autowired
    private OllamaClient ollamaClient;


    public String chat(String message) {
        String prompt = message;
        OllamaGenerateResponse response = ollamaClient.generate(prompt);
        return response.getResponse();
    }
}
