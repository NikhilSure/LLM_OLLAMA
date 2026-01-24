package com.nsure.LLM_OLLAMA.Rag;

import com.nsure.LLM_OLLAMA.Client.OllamaClient;
import com.nsure.LLM_OLLAMA.Constant.OllamaPrompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RagPromptBuilder {

    @Autowired
    private InMemoryVectorStore inMemoryVectorStore;

    @Autowired
    private OllamaClient ollamaClient;

    public String build(String UserPrompt,int topK ) {
        List<Double> embeddings = ollamaClient.getEmbeddings(UserPrompt).getEmbedding();

        String context = inMemoryVectorStore.getRelatedKDocuments(embeddings,2).stream()
                .map(doc -> "- " + doc.getText())
                .collect(Collectors.joining("\n"));

        return """
        %s

        Use ONLY the following context to answer.
        If the answer is not present, say "I don’t know".

        Context:
        %s

        User question:
        %s
        """.formatted(OllamaPrompt.SYSTEM_PROMPT, context, UserPrompt)
                .replace("\n", " ")
                .replace("\"", "\\\"");


    }
}
