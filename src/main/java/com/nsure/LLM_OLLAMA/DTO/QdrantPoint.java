package com.nsure.LLM_OLLAMA.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class QdrantPoint {
    private String id;
    private List<Double> vector;
    private Map<String, Object> payload;
}
