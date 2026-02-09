package com.nsure.LLM_OLLAMA.DTO;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class QdrantSearchResponse {
    public List<Result> result;

    @Data
    public static class Result {
        public String id;
        public double score;
        public Map<String, Object> payload; // payload fields (e.g., text, source, etc.)
    }
}
