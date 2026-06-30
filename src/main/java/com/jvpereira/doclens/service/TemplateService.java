package com.jvpereira.doclens.service;

import com.jvpereira.doclens.dto.TemplateRequestDTO;
import com.jvpereira.doclens.dto.TemplateResponseDTO;
import com.jvpereira.doclens.exception.ResourceNotFoundException;
import com.jvpereira.doclens.mapper.TemplateMapper;
import com.jvpereira.doclens.model.template.Template;
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
public class TemplateService {

    private final TemplateRepository templateRepository;
    private final TemplateMapper templateMapper;

    @Transactional
    public TemplateResponseDTO create(TemplateRequestDTO request) {
        Template template = templateMapper.toEntity(request);
        Template savedTemplate = templateRepository.save(template);
        return templateMapper.toResponse(savedTemplate);
    }

    public TemplateResponseDTO findById(UUID id) {
        Template template = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found with id: " + id));
        return templateMapper.toResponse(template);
    }

    public List<TemplateResponseDTO> findAll() {
        return templateRepository.findAll().stream()
                .map(templateMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public TemplateResponseDTO update(UUID id, TemplateRequestDTO request) {
        Template template = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found with id: " + id));
        
        templateMapper.updateEntityFromRequest(request, template);
        Template updatedTemplate = templateRepository.save(template);
        return templateMapper.toResponse(updatedTemplate);
    }

    @Transactional
    public void delete(UUID id) {
        Template template = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found with id: " + id));
        templateRepository.delete(template);
    }
}
