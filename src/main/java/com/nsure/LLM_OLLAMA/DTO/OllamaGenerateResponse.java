package com.nsure.LLM_OLLAMA.DTO;

import lombok.Data;

@Data
public class OllamaGenerateResponse {
    private String response;
    private Boolean done;
}
