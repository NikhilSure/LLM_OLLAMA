package com.nsure.LLM_OLLAMA.Config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ollama")
public class PlatformProperties {
    private String url;
    private String model;
    private String embeddingModel;
    private Options options = new Options();
    private TimeOut timeout = new TimeOut();

    @Data
    public static class Options {
        private int maxTokens;
        private double temperature;
    }

    @Data
    public static class TimeOut {
        private int connectSeconds;
    }
}


