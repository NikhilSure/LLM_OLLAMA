package com.nsure.LLM_OLLAMA.Client;

import com.nsure.LLM_OLLAMA.Config.QdrantProperties;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class QdrantClient {
    private QdrantProperties qdrantProperties;
    private final HttpClient httpClient;
    ObjectMapper mapper;

    public QdrantClient(QdrantProperties qdrantProperties) {
        this.qdrantProperties = qdrantProperties;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(
                        qdrantProperties.getConnection()
                ))
                .build();
        this.mapper = new ObjectMapper();
    }


    public boolean createCollection() {
        try {
            HttpRequest collectionCheck = HttpRequest
                    .newBuilder()
                    .uri(URI.create(qdrantProperties.getUrl() + "/collections" + qdrantProperties.getCollection()))
                    .GET()
                    .build();

            HttpResponse response = httpClient.send(collectionCheck, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200){
                System.out.println("Collection already exists:: " + qdrantProperties.getCollection());
                return true;
            }

            String body = """
            {
              "vectors": {
                "size": %d,
                "distance": "%s"
              }
            }
            """.formatted(qdrantProperties.getVectorSize(), qdrantProperties.getDistance());

            HttpRequest createCollection = HttpRequest
                    .newBuilder()
                    .uri(URI.create(qdrantProperties.getUrl() + "/collections" + qdrantProperties.getCollection()))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse response1 = httpClient.send(collectionCheck, HttpResponse.BodyHandlers.ofString());

            if (response1.statusCode() == 200) {
                System.out.println("Collection created:: " + qdrantProperties.getCollection());
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while accessing qdrant:: " + e.getMessage());
        }

        return false;
    }
}
