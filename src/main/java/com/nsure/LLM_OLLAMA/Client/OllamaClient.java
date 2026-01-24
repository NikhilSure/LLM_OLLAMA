package com.nsure.LLM_OLLAMA.Client;

import com.nsure.LLM_OLLAMA.Config.PlatformProperties;
import com.nsure.LLM_OLLAMA.Constant.ApiEndpoints;
import com.nsure.LLM_OLLAMA.Constant.OllamaPrompt;
import com.nsure.LLM_OLLAMA.DTO.OllamaEmbeddingResponse;
import com.nsure.LLM_OLLAMA.DTO.OllamaGenerateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.function.Consumer;


@Component
public class OllamaClient {


    @Autowired
    private PlatformProperties platformProperties;
    private final HttpClient httpClient;
    ObjectMapper mapper;

    public OllamaClient(PlatformProperties platformProperties) {
        this.platformProperties = platformProperties;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(
                        platformProperties.getTimeout().getConnectSeconds()
                ))
                .build();
        this.mapper = new ObjectMapper();
    }

    public OllamaGenerateResponse generate(String prompt, boolean gotContext) throws InterruptedException, IOException {

        String cleanedPrompt;

        if (gotContext) {
            cleanedPrompt = prompt;
        } else {
            cleanedPrompt = buildPrompt(prompt);
        }

        String body = """
                {
                  "model": "%s",
                  "prompt": "%s",
                  "stream": false,
                  "options": {
                    "num_predict": %d,
                    "temperature": %f
                  }
                }

                """.formatted(platformProperties.getModel(), cleanedPrompt, platformProperties.getOptions().getMaxTokens(), platformProperties.getOptions().getTemperature());

        System.out.println(" OllamaClient: generate() :: " + body);

        int attempts = 3;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(platformProperties.getUrl() + ApiEndpoints.OLLAMA_GENERATE))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        for (int i = 1; i <= attempts; i++) {
            try {
                HttpResponse<String> response =
                        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    System.out.println("Response:: " + response.body());
                    return mapper.readValue(response.body(), OllamaGenerateResponse.class);
                }

                if (response.statusCode() == 400) {
                    throw new IllegalArgumentException("Bad request to Ollama");
                }

            } catch (Exception e) {
                if (i == attempts) throw e;
                Thread.sleep(500);
            }
        }

        throw new RuntimeException("Ollama request failed after retries");
    }

    public void generateWithStream(String message, Consumer<String> onToken, boolean gotContext) {


        String cleanedPrompt;

        if (gotContext) {
            cleanedPrompt = message;
        } else {
            cleanedPrompt = buildPrompt(message);
        }

        String body = """
    {
      "model": "%s",
      "prompt": "%s",
      "stream": true,
      "options": {
        "num_predict": %d,
        "temperature": %s
      }
    }
    """.formatted(
                platformProperties.getModel(),
                cleanedPrompt,
                platformProperties.getOptions().getMaxTokens(),
                platformProperties.getOptions().getTemperature()
        );

        System.out.println(" OllamaClient: generateWithStream() :: " + body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(platformProperties.getUrl() + ApiEndpoints.OLLAMA_GENERATE))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {
            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

            // this will continuously read generated tokens(response)
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(response.body()))) {

                String line;
                while ((line = br.readLine()) != null) {
                    OllamaGenerateResponse chunk =
                            mapper.readValue(line, OllamaGenerateResponse.class);

                    if (chunk.getResponse() != null) {
                        onToken.accept(chunk.getResponse());
                    }else if (chunk.getDone()) break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public OllamaEmbeddingResponse  getEmbeddings(String userPrompt) {
        String body = """
                {
                  "model": "%s",
                  "prompt": "%s"
                }
                """.formatted(
                platformProperties.getModel(),
                cleanPrompt(userPrompt));


        System.out.println(" OllamaClient: getEmbeddings() :: " + body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(platformProperties.getUrl() + ApiEndpoints.OLLAMA_EMBEDDING))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {
            // json string response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response  from " + response.body());

            // add checks for response
            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), OllamaEmbeddingResponse.class);
            }else if (response.statusCode() == 400) {
                throw new IllegalArgumentException("Bad request to Ollama");
            } else {
                throw new RuntimeException("Unable to get Embeddings from ollama");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("OLLAMA CLIENT failed to generate embeddings");
        }
    }

    private String cleanPrompt(String prompt) {
        return prompt.replace("\n", " ").replace("\"", "\\\"");
    }

    private String buildPrompt(String userMessage) {
        String prompt = OllamaPrompt.SYSTEM_PROMPT.replace("\n", " ") + "user question: " + userMessage;
        return prompt.replace("\"", "\\\"");
    }
}
