package com.rag.springaidemo.api;

import com.rag.springaidemo.service.RagService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RagController {
    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @GetMapping("/rag")
    public String rag(String query) {
        return ragService.askLLM(query);
    }
}
