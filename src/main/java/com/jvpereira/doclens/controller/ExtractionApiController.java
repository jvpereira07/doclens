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
            // Convém fazer o parse para JsonNode para que o Spring retorne como JSON estruturado nativo,
            // em vez de retornar como uma string escapada.
            return ResponseEntity.ok(objectMapper.readTree(jsonResult));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}
