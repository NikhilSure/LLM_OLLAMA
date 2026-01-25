package com.nsure.LLM_OLLAMA.DTO;

import lombok.Data;

@Data
public class QdrantIngestRequest {
    private String text;
    private String source;
}
