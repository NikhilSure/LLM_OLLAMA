package com.nsure.LLM_OLLAMA.Repository;
import com.nsure.LLM_OLLAMA.Entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySession_ChatSessionIdOrderByTsAsc(UUID chatSessionId);

    List<ChatMessage> findTop20BySession_ChatSessionIdOrderByTsDesc(UUID chatSessionId);

    void deleteBySession_ChatSessionIdAndUserId(UUID chatSessionId, UUID userId);
    Page<ChatMessage> findBySession_ChatSessionIdAndUserIdOrderByTsAsc(
            UUID chatSessionId,
            UUID userId,
            Pageable pageable
    );

    List<ChatMessage> findBySession_ChatSessionIdAndUserIdOrderByTsAsc(
            UUID chatSessionId,
            UUID userId
    );


    @Query("""
    SELECT m
    FROM ChatMessage m
    WHERE m.userId = :userId and m.ts = (
        SELECT MAX(m2.ts)
        FROM ChatMessage m2
        WHERE m2.session.chatSessionId = m.session.chatSessionId
    )
    ORDER BY m.ts DESC
""")
  List<ChatMessage>  findRecentMessagesForAllSessions(UUID userId);
}
