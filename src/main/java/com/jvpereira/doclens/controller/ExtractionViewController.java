package com.jvpereira.doclens.controller;

import com.jvpereira.doclens.dto.extraction.ExtractionRequestDTO;
import com.jvpereira.doclens.service.ExtractionRequestService;
import com.jvpereira.doclens.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Controller
@RequestMapping("/extraction")
@RequiredArgsConstructor
public class ExtractionViewController {

    private final ExtractionRequestService extractionRequestService;
    private final TemplateService templateService;

    @GetMapping
    public String extractionPage(Model model) {
        model.addAttribute("templates", templateService.findAll());
        model.addAttribute("extractions", extractionRequestService.getAllExtractions());
        return "extraction/extract";
    }

    @PostMapping("/run")
    @ResponseBody
    public ResponseEntity<?> runExtraction(@RequestParam("templateCode") String templateCode,
                                           @RequestParam("file") MultipartFile file) {
        try {
            String jsonResult = extractionRequestService.extract(templateCode, file);
            return ResponseEntity.ok(Map.of("success", true, "data", jsonResult));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @GetMapping("/files/{id}")
    public ResponseEntity<byte[]> viewFile(@PathVariable("id") java.util.UUID id) {
        try {
            var fileMetadata = extractionRequestService.getFileMetadata(id);
            byte[] fileBytes = extractionRequestService.downloadFile(id);
            return ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.parseMediaType(fileMetadata.getType()))
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileMetadata.getName() + "\"")
                    .body(fileBytes);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/results/{requestId}")
    @ResponseBody
    public ResponseEntity<?> getResult(@PathVariable("requestId") java.util.UUID requestId) {
        return extractionRequestService.getExtractionResultByRequestId(requestId)
                .map(res -> ResponseEntity.ok(Map.of("success", true, "data", res.getData())))
                .orElse(ResponseEntity.notFound().build());
    }
}
