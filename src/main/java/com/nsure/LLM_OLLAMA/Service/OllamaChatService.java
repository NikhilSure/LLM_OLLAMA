package com.nsure.LLM_OLLAMA.Service;

import com.nsure.LLM_OLLAMA.Client.OllamaClient;
import com.nsure.LLM_OLLAMA.DTO.OllamaEmbeddingResponse;
import com.nsure.LLM_OLLAMA.DTO.OllamaGenerateResponse;
import com.nsure.LLM_OLLAMA.Rag.RagPromptBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.function.Consumer;

@Service
public class OllamaChatService {

    @Autowired
    private OllamaClient ollamaClient;

    @Autowired
    private RagPromptBuilder ragPromptBuilder;

    public String chat(String message) throws IOException, InterruptedException {
        String prompt = message;
        OllamaGenerateResponse response = ollamaClient.generate(prompt, false);
        return response.getResponse();
    }

    public void chatWithStream(String message, Consumer<String> onReply) {
        ollamaClient.generateWithStream(message, onReply, false);
    }

    public OllamaEmbeddingResponse getEmbeddings(String userPrompt) {
        return ollamaClient.getEmbeddings(userPrompt);
    }


    public String chatWithContext(String prompt) throws IOException, InterruptedException {
        prompt = ragPromptBuilder.build(prompt);
        OllamaGenerateResponse response = ollamaClient.generate(prompt, true);
        return response.getResponse();
    }

    public void chatWithContextStream(String prompt, Consumer<String> onToken) throws IOException, InterruptedException {
        prompt = ragPromptBuilder.build(prompt);
        ollamaClient.generateWithStream(prompt, onToken, true);
    }

}
