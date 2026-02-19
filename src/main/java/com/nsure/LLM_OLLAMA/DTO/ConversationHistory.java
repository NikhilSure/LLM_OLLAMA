package com.nsure.LLM_OLLAMA.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ConversationHistory {
    private UUID conservationId;
    private String recentMessage;
    private String title;
    private Long lastUpdatedTs;
}
