package com.nsure.LLM_OLLAMA.Controller;

import com.nsure.LLM_OLLAMA.DTO.ChatRequest;
import com.nsure.LLM_OLLAMA.DTO.ChatResponse;
import com.nsure.LLM_OLLAMA.Service.OllamaChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class OllamaChatController {

    @Autowired
    private OllamaChatService ollamaChatService;

    @PostMapping("/chat" )
    public ChatResponse chat(@RequestBody ChatRequest request) {
        ChatResponse response = new ChatResponse();
        response.setReply(ollamaChatService.chat(request.getMessage()));

        return response;
    }
}
