package com.nsure.LLM_OLLAMA.DTO;

import lombok.Data;

import java.util.List;

@Data
public class OllamaEmbeddingResponse {
    private List<Double> embedding;
}
