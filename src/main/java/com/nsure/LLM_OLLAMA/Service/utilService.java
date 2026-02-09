package com.nsure.LLM_OLLAMA.Service;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class utilService {
    private static final int CHUNK_SIZE = 250;
    private static final int OVERLAP = 40;

    public static List<String> chunk(String text) {
        List<String> chunks = new ArrayList<>();

        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + CHUNK_SIZE, text.length());

            // snap end to last sentence boundary
            end = findSentenceBoundary(text, start, end);

            String chunk = text.substring(start, end).trim();
            if (!chunk.isEmpty()) {
                chunks.add(chunk);
            }

            start = Math.max(end - OVERLAP, 0);
        }

        return chunks;
    }

    private static int findSentenceBoundary(String text, int start, int end) {
        for (int i = end - 1; i > start; i--) {
            char c = text.charAt(i);
            if (c == '.' || c == '!' || c == '?') {
                return i + 1;
            }
        }
        return end; // fallback
    }
}
