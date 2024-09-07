package com.rag.springaidemo.service;

import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RagService {
    private final VectorStore vectorStore;
    private final JdbcTemplate jdbcTemplate;

    public RagService(VectorStore vectorStore, JdbcTemplate jdbcTemplate) {
        this.vectorStore = vectorStore;
        this.jdbcTemplate = jdbcTemplate;
    }

    public String askLLM(String query) {
        SearchRequest searchRequest = SearchRequest.query(query).withTopK(3);
        List<Document> documentList = vectorStore.similaritySearch(searchRequest);

        String systemMessageTemplate = """
                Answer the following question based only in the provided CONTEXT
                If the answer is not found respond : "I don't know".
                CONTEXT :
                    {CONTEXT}
                """;
        Message systemMessage = new SystemPromptTemplate(systemMessageTemplate)
                .createMessage(Map.of("CONTEXT", documentList));
        UserMessage userMessage = new UserMessage(query);
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        OpenAiApi openAiApi = new OpenAiApi("xxx");
        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
                .withModel("gpt-4")
                .build();
        OpenAiChatClient openAiChatClient = new OpenAiChatClient(openAiApi, openAiChatOptions);
        ChatResponse response = openAiChatClient.call(prompt);
        String responseContent = response.getResult().getOutput().getContent();
        return responseContent;
    }

    public void textEmbedding(Resource[] pdfs) {
        jdbcTemplate.update("DELETE FROM vector_store");
        PdfDocumentReaderConfig pdfDocumentReaderConfig = PdfDocumentReaderConfig.defaultConfig();
        StringBuilder content = new StringBuilder();
        for (Resource pdf : pdfs) {
            PagePdfDocumentReader pdfDocumentReader = new PagePdfDocumentReader(pdf, pdfDocumentReaderConfig);
            List<Document> documentList = pdfDocumentReader.get();
            content.append(documentList.stream().map(Document::getContent).collect(Collectors.joining("\n")));

        }
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
        List<String> chunks = tokenTextSplitter.split(content.toString(), 1000);
        List<Document> chunksDocs = chunks.stream().map(Document::new).toList();
        vectorStore.accept(chunksDocs);
    }
}
