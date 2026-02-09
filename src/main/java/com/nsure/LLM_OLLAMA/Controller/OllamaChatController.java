package com.nsure.LLM_OLLAMA.Controller;

import com.nsure.LLM_OLLAMA.DTO.*;
import com.nsure.LLM_OLLAMA.Service.OllamaChatService;
import com.nsure.LLM_OLLAMA.Service.QdrantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class OllamaChatController {

    @Autowired
    private OllamaChatService ollamaChatService;

    @Autowired
    private QdrantService qdrantService;

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {

        if (request.getMessage().isBlank() || request.getMessage().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Message must not be empty"
            );
        }

        ChatResponse response = new ChatResponse();
        try {
            response.setReply(ollamaChatService.chat(request.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error while generating response"
            );
        }

        return response;
    }

    @PostMapping("/knowledgeChat")
    public ChatResponse chatWithContext(@RequestBody ChatRequest request) {

        if (request.getMessage().isBlank() || request.getMessage().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Message must not be empty"
            );
        }

        ChatResponse response = new ChatResponse();

        try {
            response.setReply(ollamaChatService.chatWithContext(request.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error while generating response"
            );
        }

        return response;
    }

    @PostMapping("/StreamChat")
    public SseEmitter StreamChat(@RequestBody ChatRequest request) {
        SseEmitter emitter = new SseEmitter(0L);

        if (request.getMessage().isBlank() || request.getMessage().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Message must not be empty"
            );
        }

        try {
            // thread will help us to run this emitter code independence of the controller thread
            new Thread(() -> {
                try {
                    ollamaChatService.chatWithStream(request.getMessage(), token -> {
                        try {
                            emitter.send(token);
                        } catch (IOException e) {
                            emitter.completeWithError(e);
                        }
                    });
                    emitter.complete();
                } catch (Exception e) {
                    emitter.completeWithError(e);
                }
            }).start();


            return emitter;

        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error while generating response"
            );
        }
    }

            @PostMapping("/StreamChatWithContext")
    public SseEmitter StreamChatWithContext(@RequestBody ChatRequest request) {
        SseEmitter emitter = new SseEmitter(0L);

        if (request.getMessage().isBlank() || request.getMessage().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Message must not be empty"
            );
        }

        try {
            /// thread will help us to run this emitter code independence of the controller thread
            new Thread(() -> {
                try {
                    ollamaChatService.chatWithContextStream(request.getMessage(), token -> {
                        try {
                            emitter.send(token);
                        } catch (IOException e) {
                            emitter.completeWithError(e);
                        }
                    });
                    emitter.complete();
                } catch (Exception e) {
                    emitter.completeWithError(e);
                }
            }).start();


            return emitter;

        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error while generating response"
            );
        }
    }


    @PostMapping("/embedVectors")
    public OllamaEmbeddingResponse embed(@RequestBody ChatRequest request) {

        if (request.getMessage().isBlank() || request.getMessage().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Message must not be empty"
            );
        }
        return ollamaChatService.getEmbeddings(request.getMessage());
    }


    @PostMapping("/qdrant/ingest")
    public QdrantIngestionResponse ingestTextToQdrant(@RequestBody QdrantIngestRequest request) {
        int num_chunks = qdrantService.textIngestion(request.getText(), request.getSource());

        QdrantIngestionResponse response = new QdrantIngestionResponse();
        response.setNum_chunks(num_chunks);
        response.setStatus("completed ingestion");
        return response;
    }


    @PostMapping("/qdrant/search")
    public List<String> getRealtedContext(@RequestBody QdrantIngestRequest request) {
         return qdrantService.getRelatedContext(request.getText(), request.getSource());
    }



}

