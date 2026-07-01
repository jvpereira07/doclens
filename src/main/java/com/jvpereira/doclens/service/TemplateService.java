package com.jvpereira.doclens.service;

import com.jvpereira.doclens.dto.TemplateRequestDTO;
import com.jvpereira.doclens.dto.TemplateResponseDTO;
import com.jvpereira.doclens.exception.ResourceNotFoundException;
import com.jvpereira.doclens.mapper.TemplateMapper;
import com.jvpereira.doclens.model.field.Field;
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
        // Pass 1: Temporarily extract parentFieldCodes to avoid transient instance issues
        List<String> parentCodes = new java.util.ArrayList<>();
        if (request.getFields() != null) {
            for (var f : request.getFields()) {
                parentCodes.add(f.getParentFieldCode());
                f.setParentFieldCode(null);
            }
        }

        Template template = templateMapper.toEntity(request);
        Template savedTemplate = templateRepository.save(template);

        // Restore and apply parentField relationships in Pass 2
        if (request.getFields() != null) {
            for (int i = 0; i < request.getFields().size(); i++) {
                var fReq = request.getFields().get(i);
                String parentCode = parentCodes.get(i);
                fReq.setParentFieldCode(parentCode);

                if (parentCode != null && !parentCode.trim().isEmpty()) {
                    Field child = savedTemplate.getFields().stream()
                            .filter(f -> f.getCode().equals(fReq.getCode()))
                            .findFirst()
                            .orElse(null);
                    
                    Field parent = savedTemplate.getFields().stream()
                            .filter(f -> f.getCode().equals(parentCode))
                            .findFirst()
                            .orElse(null);

                    if (child != null && parent != null) {
                        child.setParentField(parent);
                    }
                }
            }
            savedTemplate = templateRepository.save(savedTemplate);
        }

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
        
        List<String> parentCodes = new java.util.ArrayList<>();
        if (request.getFields() != null) {
            for (var f : request.getFields()) {
                parentCodes.add(f.getParentFieldCode());
                f.setParentFieldCode(null);
            }
        }

        templateMapper.updateEntityFromRequest(request, template);
        Template updatedTemplate = templateRepository.save(template);

        if (request.getFields() != null) {
            for (int i = 0; i < request.getFields().size(); i++) {
                var fReq = request.getFields().get(i);
                String parentCode = parentCodes.get(i);
                fReq.setParentFieldCode(parentCode);

                if (parentCode != null && !parentCode.trim().isEmpty()) {
                    Field child = updatedTemplate.getFields().stream()
                            .filter(f -> f.getCode().equals(fReq.getCode()))
                            .findFirst()
                            .orElse(null);
                    
                    Field parent = updatedTemplate.getFields().stream()
                            .filter(f -> f.getCode().equals(parentCode))
                            .findFirst()
                            .orElse(null);

                    if (child != null && parent != null) {
                        child.setParentField(parent);
                    }
                }
            }
            updatedTemplate = templateRepository.save(updatedTemplate);
        }

        return templateMapper.toResponse(updatedTemplate);
    }

    @Transactional
    public void delete(UUID id) {
        Template template = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found with id: " + id));
        templateRepository.delete(template);
    }

    public TemplateResponseDTO findByTag(String tag) {
        Template template = templateRepository.findByTag(tag)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found with tag: " + tag));
        return templateMapper.toResponse(template);
    }
}
