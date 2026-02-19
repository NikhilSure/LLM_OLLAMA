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


    public String generateTitle(String message) throws Exception {

        String prompt = """
            You generate short chat titles.

            Create a 3 to 6 word title for the user message below.

            Rules:
            - return ONLY the title
            - no punctuation
            - no quotes
            - no emojis
            - no extra words
            - lowercase only
            - summarize intent not details

            User message:
            %s

            Title:
            """.formatted(message).replace("\n", " ");

        OllamaGenerateResponse res = ollamaClient.generate(prompt, true);

        return sanitize(res.getResponse());
    }

    private String sanitize(String text){

        if(text == null) return "new chat";

        text = text.toLowerCase();
        text = text.replaceAll("[^a-z0-9 ]", "");
        text = text.replaceAll("\\s+", " ").trim();

        if(text.length() > 60)
            text = text.substring(0, 60);

        if(text.isBlank())
            return "new chat";

        return text;
    }
}
