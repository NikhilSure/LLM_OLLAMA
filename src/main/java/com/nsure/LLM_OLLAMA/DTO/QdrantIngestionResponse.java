package com.nsure.LLM_OLLAMA.DTO;

import lombok.Data;

@Data
public class QdrantIngestionResponse {
    private int num_chunks;
    private String status;
}
