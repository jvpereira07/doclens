package com.jvpereira.doclens.controller;

import com.jvpereira.doclens.dto.FieldRequestDTO;
import com.jvpereira.doclens.dto.FieldResponseDTO;
import com.jvpereira.doclens.service.FieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/fields")
@RequiredArgsConstructor
public class FieldApiController {

    private final FieldService fieldService;

    @PostMapping
    public ResponseEntity<FieldResponseDTO> create(@RequestBody FieldRequestDTO requestDTO) {
        FieldResponseDTO response = fieldService.create(requestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<FieldResponseDTO>> findAll() {
        return ResponseEntity.ok(fieldService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FieldResponseDTO> findById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(fieldService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FieldResponseDTO> update(@PathVariable("id") UUID id,
                                                   @RequestBody FieldRequestDTO requestDTO) {
        return ResponseEntity.ok(fieldService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        fieldService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
