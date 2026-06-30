package com.jvpereira.doclens.service;

import com.jvpereira.doclens.dto.FieldRequestDTO;
import com.jvpereira.doclens.dto.FieldResponseDTO;
import com.jvpereira.doclens.exception.ResourceNotFoundException;
import com.jvpereira.doclens.mapper.FieldMapper;
import com.jvpereira.doclens.model.field.Field;
import com.jvpereira.doclens.model.template.Template;
import com.jvpereira.doclens.repository.FieldRepository;
import com.jvpereira.doclens.repository.TemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FieldService {

    private final FieldRepository fieldRepository;
    private final TemplateRepository templateRepository;
    private final FieldMapper fieldMapper;

    @Transactional
    public FieldResponseDTO create(FieldRequestDTO request) {
        Template template = null;
        if (request.getTemplateId() != null) {
            template = templateRepository.findById(request.getTemplateId())
                    .orElseThrow(() -> new ResourceNotFoundException("Template not found with id: " + request.getTemplateId()));
        }

        Field field = fieldMapper.toEntity(request, template);
        Field savedField = fieldRepository.save(field);
        return fieldMapper.toResponse(savedField);
    }

    public FieldResponseDTO findById(UUID id) {
        Field field = fieldRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Field not found with id: " + id));
        return fieldMapper.toResponse(field);
    }

    public List<FieldResponseDTO> findAll() {
        return fieldRepository.findAll().stream()
                .map(fieldMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public FieldResponseDTO update(UUID id, FieldRequestDTO request) {
        Field field = fieldRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Field not found with id: " + id));

        Template template = null;
        if (request.getTemplateId() != null) {
            template = templateRepository.findById(request.getTemplateId())
                    .orElseThrow(() -> new ResourceNotFoundException("Template not found with id: " + request.getTemplateId()));
        }

        fieldMapper.updateEntityFromRequest(request, field, template);
        Field updatedField = fieldRepository.save(field);
        return fieldMapper.toResponse(updatedField);
    }

    @Transactional
    public void delete(UUID id) {
        Field field = fieldRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Field not found with id: " + id));
        fieldRepository.delete(field);
    }
}
