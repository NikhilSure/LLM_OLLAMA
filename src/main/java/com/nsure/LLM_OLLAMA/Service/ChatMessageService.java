package com.nsure.LLM_OLLAMA.Service;

import com.nsure.LLM_OLLAMA.DTO.ConversationHistory;
import com.nsure.LLM_OLLAMA.Entity.ChatMessage;
import com.nsure.LLM_OLLAMA.Entity.Session;
import com.nsure.LLM_OLLAMA.Repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    @Autowired
    SessionService sessionService;

    @Transactional
    public ChatMessage saveMessage(UUID chatSessionId,String content, String source, UUID userId, String role) throws Exception {
       Session s =  sessionService.getorCreateSessionById(chatSessionId, content);

        System.out.println("ChatMessageService:: saveMessage :: content" + content);
        ChatMessage message = ChatMessage.builder().content(content)
                        .role(role).ts(System.currentTimeMillis())
                .userId(userId)
                .session(s)
                .source(source)
                .ts(System.currentTimeMillis())
                .build();

        return chatMessageRepository.save(message);
    }

    public List<ChatMessage> getChatMessages(UUID chatSessionId, UUID userId, int pageNumber, int size) {
        Pageable pageable = PageRequest.of(pageNumber, size);

        return chatMessageRepository
                .findBySession_ChatSessionIdAndUserIdOrderByTsAsc(chatSessionId, userId, pageable)
                .getContent();
    }

    public List<ChatMessage> getChatMessages(UUID chatSessionId, UUID userId) {
        return chatMessageRepository
                .findBySession_ChatSessionIdAndUserIdOrderByTsAsc(chatSessionId, userId);
    }


    @Transactional
    public void deteleSession(UUID chatSessionId, UUID userID) {
        chatMessageRepository.deleteBySession_ChatSessionIdAndUserId(chatSessionId, userID);
    }

    @Transactional
    public List<ConversationHistory> getConversationHistory(UUID userID) {
       return chatMessageRepository.findRecentMessagesForAllSessions(userID).stream().map(m -> new ConversationHistory(m.getSession().getChatSessionId(), m.getContent(),m.getSession() .getTitle() ,m.getTs())).toList();
    }
}

