package com.nsure.LLM_OLLAMA.StartUp;

import com.nsure.LLM_OLLAMA.Client.OllamaClient;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OllamaStartup {
    @Autowired
    private OllamaClient ollamaClient;

    @PostConstruct
    public void ollamaStartup() {
        try {
            ollamaClient.generate("ping", false);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ollama cold start error::: " +  e.getMessage());
        }
    }
}
