package com.nsure.LLM_OLLAMA.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int msgId;

    private UUID userId;
    private String role;

    @Column(columnDefinition = "TEXT")
    private String content;
    private String source;
    private Long ts;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatSessionId")
    private Session session;
}
