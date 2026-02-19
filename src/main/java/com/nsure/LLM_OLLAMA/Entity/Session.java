package com.nsure.LLM_OLLAMA.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
public class Session {

    @Id
    UUID chatSessionId;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    void preInsert() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
