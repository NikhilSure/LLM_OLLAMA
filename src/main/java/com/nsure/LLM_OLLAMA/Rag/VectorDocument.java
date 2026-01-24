package com.nsure.LLM_OLLAMA.Rag;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class VectorDocument {
    private String text;
    private List<Double> embeddings;
}
