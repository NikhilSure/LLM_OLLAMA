package com.nsure.LLM_OLLAMA;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.nsure.LLM_OLLAMA.Config")
public class LlmOllamaApplication {

	public static void main(String[] args) {
		SpringApplication.run(LlmOllamaApplication.class, args);
	}

}
