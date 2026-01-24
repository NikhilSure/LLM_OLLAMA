package com.nsure.LLM_OLLAMA.Rag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class InMemoryVectorStore {

    private final List<VectorDocument> vectorDocuments = new ArrayList<>();

    public void addVectorDocument(VectorDocument vectorDocument) {
        vectorDocuments.add(vectorDocument);
    }

    public List<VectorDocument> getRelatedKDocuments(
            List<Double> queryEmbedding,
            int topK
    ) {
        return vectorDocuments.stream()
                .sorted(
                        Comparator.comparingDouble(
                                (VectorDocument doc) -> CosineSimilarity.similarity(
                                        queryEmbedding,
                                        doc.getEmbeddings()
                                )
                        ).reversed()
                )
                .limit(topK)
                .toList();
    }
}
