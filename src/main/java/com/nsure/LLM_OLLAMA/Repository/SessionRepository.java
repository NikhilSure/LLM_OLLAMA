package com.nsure.LLM_OLLAMA.Repository;

import com.nsure.LLM_OLLAMA.Entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SessionRepository  extends JpaRepository<Session, UUID> {
}
