package com.nsure.LLM_OLLAMA.Service;

import com.nsure.LLM_OLLAMA.Client.OllamaClient;
import com.nsure.LLM_OLLAMA.DTO.OllamaEmbeddingResponse;
import com.nsure.LLM_OLLAMA.DTO.OllamaGenerateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.function.Consumer;

@Service
public class OllamaChatService {

    @Autowired
    private OllamaClient ollamaClient;

    public String chat(String message) throws IOException, InterruptedException {
        String prompt = message;
        OllamaGenerateResponse response = ollamaClient.generate(prompt);
        return response.getResponse();
    }

    public void chatWithStream(String message, Consumer<String> onReply) {
        ollamaClient.generateWithStream(message, onReply);
    }

    public OllamaEmbeddingResponse getEmbeddings(String userPrompt) {
        return ollamaClient.getEmbeddings(userPrompt);
    }
}
