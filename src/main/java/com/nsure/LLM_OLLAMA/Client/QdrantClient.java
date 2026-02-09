package com.nsure.LLM_OLLAMA.Client;

import com.nsure.LLM_OLLAMA.Config.QdrantProperties;
import com.nsure.LLM_OLLAMA.DTO.QdrantPoint;
import com.nsure.LLM_OLLAMA.DTO.QdrantSearchResponse;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

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
                    .uri(URI.create(qdrantProperties.getUrl() + "/collections/" + qdrantProperties.getCollection()))
                    .GET()
                    .build();

            HttpResponse response = httpClient.send(collectionCheck, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
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
                    .uri(URI.create(qdrantProperties.getUrl() + "/collections/" + qdrantProperties.getCollection()))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse response1 = httpClient.send(createCollection, HttpResponse.BodyHandlers.ofString());

            if (response1.statusCode() == 200 || response1.statusCode() == 201) {
                System.out.println("Collection created:: " + qdrantProperties.getCollection());
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while accessing qdrant:: " + e.getMessage());
        }

        return false;
    }

    public boolean upsert(List<QdrantPoint> points) {
        try {
            String body = mapper.writeValueAsString(
                    Map.of(
                            "points",
                            points.stream()
                                    .map(p -> Map.of(
                                            "id", p.getId(),
                                            "vector", p.getVector(),
                                            "payload", p.getPayload()
                                    ))
                                    .toList()
                    )
            );

            System.out.println("QdrantClient::upsert body = " + body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            qdrantProperties.getUrl()
                                    + "/collections/"
                                    + qdrantProperties.getCollection()
                                    + "/points"
                    ))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Qdrant upsert status = " + response.statusCode());
            System.out.println("Qdrant upsert response = " + response.body());

            if (response.statusCode() != 200) {
                throw new RuntimeException(
                        "Qdrant upsert failed: " + response.statusCode() + " -> " + response.body()
                );
            }

            return true;

        } catch (Exception e) {
            throw new RuntimeException("Failed to upsert vector into Qdrant", e);
        }
    }


    public List<String> search(List<Double> vectors, String source, int limit) {
        String body = """
                {
                  "vector": %s,
                  "limit": %s,
                  "with_payload": true,
                  "filter": {
                    "must": [
                      {
                        "key": "source",
                        "match": {
                          "value": "%s"
                        }
                      }
                    ]
                  }
                }
                """.formatted(vectors.toString(), limit, source);

        System.out.println("QdrantClient:: Search :: API Payload::::" + body);

        try {
            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(URI.create(qdrantProperties.getUrl() + "/collections/" + qdrantProperties.getCollection() + "/points/search"))
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(body))
                            .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());


            System.out.println("QdrantClient:: Search:: api response:: " + response);

            if (response.statusCode() == 200) {
                QdrantSearchResponse searchResponse = mapper.readValue(response.body(), QdrantSearchResponse.class);

                // Extract all 'text' fields from payload
                return searchResponse.result.stream()
                        .map(r -> r.payload.getOrDefault("text", "").toString())
                        .toList();

            } else {
                System.err.println("Qdrant Search Failed: " + response.body());
                return List.of();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("QdrantClient :: issue while searching " + e.getMessage());
        }
    }

}
