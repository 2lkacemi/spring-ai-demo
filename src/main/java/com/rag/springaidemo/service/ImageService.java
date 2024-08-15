package com.rag.springaidemo.service;

import org.springframework.ai.image.*;
import org.springframework.stereotype.Service;

@Service
public class ImageService {

    private final ImageClient imageClient;

    public ImageService(ImageClient imageClient) {
        this.imageClient = imageClient;
    }

    public ImageResponse generateImage(String prompt) {
        ImageOptions imageOptions = ImageOptionsBuilder.builder()
                .withN(1) //Number of images to be generated
                .withHeight(1024)
                .withWidth(1024)
                .build();

        return imageClient.call(new ImagePrompt(prompt, imageOptions));
    }
}
