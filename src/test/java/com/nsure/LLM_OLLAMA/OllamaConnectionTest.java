package com.nsure.LLM_OLLAMA;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OllamaConnectionTest {


    @Test
    void testOllamaConectiivity() throws IOException, InterruptedException {

        String requestBody = """
        {
          "model": "phi3:mini",
          "prompt": "when we can meet",
          "stream": false
        }
        """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:11434/api/generate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();


        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response  = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        assert (response.statusCode() == 200);
    }
}
