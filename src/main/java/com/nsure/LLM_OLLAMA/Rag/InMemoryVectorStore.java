    package com.nsure.LLM_OLLAMA.Rag;

    import com.nsure.LLM_OLLAMA.Config.PlatformProperties;
    import jakarta.annotation.PostConstruct;
    import org.springframework.stereotype.Component;
    import tools.jackson.core.type.TypeReference;
    import tools.jackson.databind.ObjectMapper;

    import java.io.IOException;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.util.ArrayList;
    import java.util.Comparator;
    import java.util.List;

    @Component
    public class InMemoryVectorStore {

        private final ObjectMapper mapper;
        private final Path storePath;
        private final List<VectorDocument> vectorDocuments = new ArrayList<>();
        private final PlatformProperties platformProperties;

        InMemoryVectorStore(PlatformProperties platformProperties) throws IOException {
            mapper = new ObjectMapper();
            this.platformProperties = platformProperties;
            Path basedir = Path.of(platformProperties.getVectorJsonPath());
            Files.createDirectories(basedir);
           storePath = basedir.resolve("vectorsore.json");
        }

        @PostConstruct
        public void loadVectorStoreFile() throws IOException {
            if (!Files.exists(storePath)) {
                System.out.println("Vector store file not found, creating :: " + storePath);
                Files.writeString(storePath, "[]");
            }

            try {
                List<VectorDocument> loaded =
                        mapper.readValue(
                                storePath.toFile(),
                                new TypeReference<List<VectorDocument>>() {}
                        );

                vectorDocuments.addAll(loaded);

                System.out.printf("Loaded %d docs", loaded.size());
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to load vectors from JSON file", e);
            }
        }


        public void addVectorDocument(VectorDocument vectorDocument) {
            vectorDocuments.add(vectorDocument);

            try {
                mapper.writerWithDefaultPrettyPrinter()
                        .writeValue(storePath.toFile(), vectorDocuments);
            } catch (Exception e) {
                throw new RuntimeException("Failed to persist vector store", e);
            }
        }


        public List<VectorDocument> getRelatedKDocuments(
                List<Double> queryEmbedding,
                int topK
        ) {
            return vectorDocuments.stream()
                    .sorted(
                            Comparator.comparingDouble(
                                    (VectorDocument doc) -> CosineSimilarity.similarity(
                                            queryEmbedding,
                                            doc.getEmbeddings()
                                    )
                            ).reversed()
                    )
                    .limit(topK)
                    .toList();
        }
    }
