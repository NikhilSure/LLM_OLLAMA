package com.nsure.LLM_OLLAMA.Rag;

import java.util.List;

public class CosineSimilarity {

    public static Double similarity(List<Double> v1Embeddings, List<Double> v2Embeddings) {
        if (v1Embeddings.isEmpty() || v1Embeddings.size() != v2Embeddings.size()) {
            throw new RuntimeException("Illegal Vectors for comparision");
        }

        //cosine(A, B) = (A · B) / (|A| × |B|)
        Double dotProd = 0.;
        Double normV1 = 0.;
        Double normV2 = 0.;

        for(int i = 0; i < v1Embeddings.size(); i++) {
            dotProd += v1Embeddings.get(i) * v2Embeddings.get(i);
            normV1 += v1Embeddings.get(i) * v1Embeddings.get(i);
            normV2 += v2Embeddings.get(i) * v2Embeddings.get(i);
        }

        return dotProd / (normV1 * normV2);
    }
}
