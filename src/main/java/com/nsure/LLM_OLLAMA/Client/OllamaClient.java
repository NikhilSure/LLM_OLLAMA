package com.nsure.LLM_OLLAMA.Client;

import com.nsure.LLM_OLLAMA.DTO.OllamaGenerateResponse;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;


@Component
public class OllamaClient {

    private static final String OLLAMA_URL =
            "http://localhost:11434/api/generate";
    private static final String MODEL = "phi3:mini";
    private final HttpClient httpClient;
    ObjectMapper mapper;

    public OllamaClient() {
        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        mapper = new ObjectMapper();
    }

    public OllamaGenerateResponse generate(String prompt) {
        String body = """
        {
          "model": "%s",
          "prompt": "%s",
          "stream": false
        }
        """.formatted(MODEL, escape(prompt));

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OLLAMA_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();


            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return mapper.readValue(response.body(), OllamaGenerateResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to generate ollama response");
        }


    }

    private String escape(String text) {
        return text.replace("\"", "\\\"");
    }

}
