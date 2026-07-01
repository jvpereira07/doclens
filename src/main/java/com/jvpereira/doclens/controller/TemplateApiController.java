package com.jvpereira.doclens.controller;

import com.jvpereira.doclens.dto.TemplateRequestDTO;
import com.jvpereira.doclens.dto.TemplateResponseDTO;
import com.jvpereira.doclens.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
public class TemplateApiController {

    private final TemplateService templateService;

    @PostMapping
    public ResponseEntity<TemplateResponseDTO> create(@RequestBody TemplateRequestDTO requestDTO) {
        TemplateResponseDTO response = templateService.create(requestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TemplateResponseDTO>> findAll() {
        return ResponseEntity.ok(templateService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TemplateResponseDTO> findById(@PathVariable("id") java.util.UUID id) {
        return ResponseEntity.ok(templateService.findById(id));
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<TemplateResponseDTO> findByTag(@PathVariable("tag") String tag) {
        return ResponseEntity.ok(templateService.findByTag(tag));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TemplateResponseDTO> update(@PathVariable("id") java.util.UUID id, 
                                                      @RequestBody TemplateRequestDTO requestDTO) {
        return ResponseEntity.ok(templateService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") java.util.UUID id) {
        templateService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
