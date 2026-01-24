package com.nsure.LLM_OLLAMA.Controller;

import com.nsure.LLM_OLLAMA.DTO.ChatRequest;
import com.nsure.LLM_OLLAMA.DTO.ChatResponse;
import com.nsure.LLM_OLLAMA.Service.OllamaChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
public class OllamaChatController {

    @Autowired
    private OllamaChatService ollamaChatService;

        @PostMapping("/chat" )
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


    @PostMapping("/StreamChat" )
    public SseEmitter StreamChat(@RequestBody ChatRequest request) {
        SseEmitter emitter = new SseEmitter(0L);

        if (request.getMessage().isBlank() || request.getMessage().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Message must not be empty"
            );
        }

        try {
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



}
