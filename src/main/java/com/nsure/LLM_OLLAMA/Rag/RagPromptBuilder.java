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

    public String build(String UserPrompt) {
        List<Double> embeddings = ollamaClient.getEmbeddings(UserPrompt).getEmbedding();

        String context = inMemoryVectorStore.getRelatedKDocuments(embeddings, 2).stream()
                .map(doc -> "- " + doc.getText())
                .collect(Collectors.joining("\n"));

        return """
                %s

                Answer the user naturally.
                Use the Context only when it clearly helps answer the question.
                If the Context is not relevant, answer using your general knowledge.

                Context:
                %s

                Question:
                %s
                """.formatted(OllamaPrompt.SYSTEM_PROMPT, context, UserPrompt)
                .replace("\n", " ")
                .replace("\"", "\\\"");


    }
}
