package com.nsure.LLM_OLLAMA.Config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "qdrant")
public class QdrantProperties {
    private String url;
    private String collection;
    private String vectorSize;
    private String distance;
    private int connection;
}
