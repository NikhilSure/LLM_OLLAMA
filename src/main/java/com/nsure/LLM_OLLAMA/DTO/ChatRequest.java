package com.nsure.LLM_OLLAMA.DTO;

import lombok.Data;

@Data
public class ChatRequest {
    private String message;
    private String userId;
    private String chatSessionId;
}
