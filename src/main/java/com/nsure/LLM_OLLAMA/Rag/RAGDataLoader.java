package com.nsure.LLM_OLLAMA.Rag;

import com.nsure.LLM_OLLAMA.Client.OllamaClient;
import com.nsure.LLM_OLLAMA.DTO.OllamaEmbeddingResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RAGDataLoader {

    @Autowired
    private OllamaClient ollamaClient;

    @Autowired
    private InMemoryVectorStore inMemoryVectorStore;

    private final List<String> documentData = List.of(
            "Company allows 20 days paid leave.",
            "Sick leave is separate from paid leave.",
            "Employees can work from home twice a week."
    );

    @PostConstruct
    public void fillInMemoryVectorDB() {
        System.out.println("before fillInMemoryVectorDB:::: " + System.currentTimeMillis());

        for (String line : documentData) {
            OllamaEmbeddingResponse embeddingResponse = ollamaClient.getEmbeddings(line);
            VectorDocument vectorDocument = new VectorDocument(line, embeddingResponse.getEmbedding());
            inMemoryVectorStore.addVectorDocument(vectorDocument);
        }

        System.out.println("after fillInMemoryVectorDB:::: " + System.currentTimeMillis());
    }
}
