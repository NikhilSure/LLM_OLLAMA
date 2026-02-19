package com.nsure.LLM_OLLAMA.Controller;

import com.nsure.LLM_OLLAMA.DTO.ConversationHistory;
import com.nsure.LLM_OLLAMA.Entity.ChatMessage;
import com.nsure.LLM_OLLAMA.Service.ChatMessageService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("chatMessage")
public class ChatMessageControllor {

    private final ChatMessageService chatMessageService;

    public ChatMessageControllor(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    @PostMapping("/getBySessionId")
    public List<ChatMessage> getBySessionId(@RequestBody Map<String, String> body) {
        UUID userId;
        UUID chatSessionId;
        int page;
        int size;

        try {
            String userIdStr = body.get("userId");
            String chatSessionIdStr = body.get("chatSessionId");
//            String pageStr = body.get("page");
//            String sizeStr = body.get("size");

            if (userIdStr == null || chatSessionIdStr == null) {
                throw new RuntimeException("Missing required UUID fields");
            }

            userId = UUID.fromString(userIdStr);
            chatSessionId = UUID.fromString(chatSessionIdStr);

//            page = pageStr != null ? Integer.parseInt(pageStr) : 0;
//            size = sizeStr != null ? Integer.parseInt(sizeStr) : 20;

            // for prototyping
            return chatMessageService.getChatMessages(chatSessionId, userId);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("error occured "+ e.getMessage());
        }


    }

    @DeleteMapping("deleteWithsession")
    public void deleteChatmessgae(@RequestBody Map<String, String> body) {
        UUID UserId = UUID.fromString(body.get("userId"));
        UUID chatSessionId = UUID.fromString(body.get("chatSessionId"));

        System.out.println("ChatMessageControllor :: deleteChatmessgae::  chatSessionId:" + chatSessionId +  " userId:: "+ UserId);

        if (UserId == null || chatSessionId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Message must not be empty"
            );
        }
        chatMessageService.deteleSession(chatSessionId, UserId);
    }

    @GetMapping("/conversationHistory/{userId}")
    public List<ConversationHistory> getConversationHistory(
            @PathVariable UUID userId) {
        return chatMessageService.getConversationHistory(userId);
    }
}
