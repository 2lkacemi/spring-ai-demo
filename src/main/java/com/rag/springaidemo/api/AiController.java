package com.rag.springaidemo.api;

import com.rag.springaidemo.service.ChatService;
import com.rag.springaidemo.service.ImageService;
import com.rag.springaidemo.service.OcrService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.image.ImageResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class AiController {

    private final ChatService chatService;
    private final ImageService imageService;
    private final OcrService ocrService;

    public AiController(ChatService chatService, ImageService imageService, OcrService ocrService) {
        this.chatService = chatService;
        this.imageService = imageService;
        this.ocrService = ocrService;
    }

    @GetMapping("ask-ai")
    public String askAi(@RequestParam("prompt") String prompt){
        return chatService.queryAi(prompt);
    }

    @GetMapping("ask-guide")
    public String askGuide(@RequestParam("city") String city, @RequestParam("interest") String interest) {
        return chatService.getCityGuide(city, interest);
    }

    @GetMapping("generate-image")
    public void generateImage(HttpServletResponse response, @RequestParam("prompt") String prompt) throws IOException {
        ImageResponse imageResponse = imageService.generateImage(prompt);

        // Get URL of the generated image
        String imageUrl = imageResponse.getResult().getOutput().getUrl();

        // Send redirect to the image URL
        response.sendRedirect(imageUrl);
    }

    @GetMapping(value = "ocr", produces = MediaType.TEXT_PLAIN_VALUE)
    public String ocr() throws IOException {
        return ocrService.readImage();
    }

}
