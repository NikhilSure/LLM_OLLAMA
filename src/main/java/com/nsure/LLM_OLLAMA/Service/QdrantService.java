package com.nsure.LLM_OLLAMA.Service;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.nsure.LLM_OLLAMA.Client.OllamaClient;
import com.nsure.LLM_OLLAMA.Client.QdrantClient;
import com.nsure.LLM_OLLAMA.DTO.QdrantPoint;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.awt.*;
import java.util.*;
import java.util.List;

@Service
public class QdrantService {
    @Autowired
    private QdrantClient qdrantClient;

    @Autowired
    private OllamaClient ollamaClient;

    @PostConstruct
    public void inintQdrantCollection(){
         boolean status = qdrantClient.createCollection();
        if (!status) {
            throw new RuntimeException("Qdrant service issue");
        }
    }

    public int textIngestion(String information, String source) {
        List<String> chunks = utilService.chunk(information);
        int chunkIndex = 0;

//        List<QdrantClient>
          List<QdrantPoint> points = new ArrayList<>();

        for (String chunk: chunks) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("text", chunk);
            payload.put("source", source);
            payload.put("chunkIndex", chunkIndex++);
            List<Double> vectors = ollamaClient.getEmbeddings(chunk).getEmbedding();
            points.add(new QdrantPoint(generatePointId(), vectors, payload));
        }

        boolean status = qdrantClient.upsert(points);

        if (!status) {
            System.out.println("issue while ingestion of chunks---------------");
            throw new RuntimeException("issue while inserting data");
        }

        System.out.println("status of the insetion of the chunk::::: " + status);
        return chunks.size();
    }

    public List<String> getRelatedContext(String text, String source) {
        List<Double> vector = ollamaClient.getEmbeddings(text).getEmbedding();
        return qdrantClient.search(vector, source, 4);
    }


    private String generatePointId() {
        return UUID.randomUUID().toString();
    }
}
