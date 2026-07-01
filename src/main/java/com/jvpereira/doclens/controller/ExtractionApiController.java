package com.jvpereira.doclens.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jvpereira.doclens.service.ExtractionRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/extraction")
@RequiredArgsConstructor
public class ExtractionApiController {

    private final ExtractionRequestService extractionRequestService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> extract(@RequestParam("templateCode") String templateCode,
                                     @RequestParam("file") MultipartFile file) {
        try {
            String jsonResult = extractionRequestService.extract(templateCode, file);
            // Parse para Object para que o Spring retorne como JSON estruturado nativo,
            // evitando serializar metadados do JsonNode.
            return ResponseEntity.ok(objectMapper.readValue(jsonResult, Object.class));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> listAll() {
        return ResponseEntity.ok(extractionRequestService.getAllExtractions());
    }

    @GetMapping("/{id}/result")
    public ResponseEntity<?> getResult(@PathVariable("id") java.util.UUID id) {
        return extractionRequestService.getExtractionResultByRequestId(id)
                .map(res -> {
                    try {
                        return ResponseEntity.ok(objectMapper.readValue(res.getData(), Object.class));
                    } catch (Exception e) {
                        return ResponseEntity.ok(res.getData());
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/prompt")
    public ResponseEntity<?> getPrompt(@PathVariable("id") java.util.UUID id) {
        return extractionRequestService.getExtractionRequestById(id)
                .map(req -> ResponseEntity.ok(Map.of("prompt", req.getPromptUsed() != null ? req.getPromptUsed() : "")))
                .orElse(ResponseEntity.notFound().build());
    }
}
