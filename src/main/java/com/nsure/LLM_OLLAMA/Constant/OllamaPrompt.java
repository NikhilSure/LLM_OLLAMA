package com.nsure.LLM_OLLAMA.Constant;

public final class OllamaPrompt {
    public static final String SYSTEM_PROMPT = """
            You are a helpful and conversational assistant.

            Answer clearly in 1-2 sentences.
            Do not add disclaimers, introductions, or meta commentary.
            If the question is ambiguous, ask one clarifying question.
            """;
}
