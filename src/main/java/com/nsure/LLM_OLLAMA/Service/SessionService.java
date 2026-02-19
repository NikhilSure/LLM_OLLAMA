package com.nsure.LLM_OLLAMA.Service;

import com.nsure.LLM_OLLAMA.Entity.Session;
import com.nsure.LLM_OLLAMA.Repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class SessionService {
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    OllamaChatService ollamaChatService;

    @Transactional
    public Session getorCreateSessionById(UUID chatSessionId, String message) throws Exception {
        Optional<Session> session = sessionRepository.findById(chatSessionId);
        if (session.isPresent()) {
                return session.get();
        } else {
            // creation of session
            String title = ollamaChatService.generateTitle(message);

            return addTitle(title, chatSessionId);
        }
    }

    @Transactional
    public Session addTitle(String title, UUID chatSessionId) {
        Session s = new Session();
        s.setTitle(title);
        s.setChatSessionId(chatSessionId);
        return sessionRepository.save(s);
    }

    @Transactional
    public void delete(UUID chatSessionId) {
        sessionRepository.deleteById(chatSessionId);
    }
}
