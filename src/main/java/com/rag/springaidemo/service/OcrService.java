package com.rag.springaidemo.service;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.messages.Media;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.io.IOException;
import java.util.List;

@Service
public class OcrService {

    private final ChatClient chatClient;

    public OcrService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }


    public String readImage() throws IOException {
        Resource resource = new ClassPathResource("img/expenses.jpg");
        byte[] data = resource.getContentAsByteArray();
        String userMessageText = """
                 Analyze the image containing handwritten text and provide,\040
                 in JSON format, how the expenses are divided as written in the image""";
        UserMessage userMessage = new UserMessage(userMessageText, List.of(new Media(MimeTypeUtils.IMAGE_JPEG, data)));
        Prompt prompt = new Prompt(userMessage);
        return chatClient.call(prompt).getResult().getOutput().getContent();
    }
}
