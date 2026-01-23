# LLM_OLLAMA
Generic backend that will be used for exposing functionallity of ollama with model and vector db agnostic


src
├── main
│   ├── java
│   │   └── com
│   │       └── nsure
│   │           └── LLM_OLLAMA
│   │
│   │               ├── LlmOllamaApplication.java
│   │
│   │               ├── client
│   │               │   └── OllamaClient.java
│   │
│   │               ├── service
│   │               │   └── OllamaChatService.java
│   │
│   │               ├── controller
│   │               │   └── ChatController.java
│   │
│   │               ├── dto
│   │               │   ├── ChatRequest.java
│   │               │   ├── ChatResponse.java
│   │               │   └── OllamaGenerateResponse.java
│   │
│   │               ├── config
│   │               │   └── OllamaProperties.java
│   │
│   │               ├── constant
│   │               │   └── OllamaPrompt.java
│   │
│   │               └── startup
│   │                   └── OllamaWarmUp.java
│   │
│   └── resources
│       └── application.yml
│
└── test
    └── java
        └── com
            └── nsure
                └── LLM_OLLAMA
                    └── OllamaConnectionTest.java
