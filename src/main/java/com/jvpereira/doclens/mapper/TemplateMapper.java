package com.jvpereira.doclens.mapper;

import com.jvpereira.doclens.dto.FieldResponseDTO;
import com.jvpereira.doclens.dto.TemplateRequestDTO;
import com.jvpereira.doclens.dto.TemplateResponseDTO;
import com.jvpereira.doclens.model.field.Field;
import com.jvpereira.doclens.model.template.Template;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TemplateMapper {

    private final FieldMapper fieldMapper;

    public TemplateResponseDTO toResponse(Template template) {
        if (template == null) {
            return null;
        }

        Set<FieldResponseDTO> fieldDTOs = null;
        if (template.getFields() != null) {
            fieldDTOs = template.getFields().stream()
                    .map(fieldMapper::toResponse)
                    .collect(Collectors.toSet());
        }

        return TemplateResponseDTO.builder()
                .id(template.getId())
                .tag(template.getTag())
                .name(template.getName())
                .description(template.getDescription())
                .propHints(template.getPropHints())
                .status(template.getStatus())
                .fields(fieldDTOs)
                .build();
    }

    public Template toEntity(TemplateRequestDTO request) {
        if (request == null) {
            return null;
        }
        Template template = new Template();
        updateEntityFromRequest(request, template);
        return template;
    }

    public void updateEntityFromRequest(TemplateRequestDTO request, Template template) {
        if (request == null || template == null) {
            return;
        }
        template.setTag(request.getTag());
        template.setName(request.getName());
        template.setDescription(request.getDescription());
        template.setPropHints(request.getPropHints());
        template.setStatus(request.getStatus());

        if (request.getFields() != null) {
            java.util.Map<String, com.jvpereira.doclens.dto.FieldRequestDTO> incomingFields = request.getFields().stream()
                    .filter(fReq -> fReq.getCode() != null && !fReq.getCode().trim().isEmpty())
                    .collect(Collectors.toMap(com.jvpereira.doclens.dto.FieldRequestDTO::getCode, f -> f, (f1, f2) -> f1));

            if (template.getFields() == null) {
                template.setFields(new java.util.HashSet<>());
            }

            // Remove existing fields that are not in the new request
            template.getFields().removeIf(existingField -> !incomingFields.containsKey(existingField.getCode()));

            // Pass 1: Update existing or insert new fields flatly
            for (var entry : incomingFields.entrySet()) {
                String code = entry.getKey();
                var fReq = entry.getValue();

                Field existing = template.getFields().stream()
                        .filter(f -> f.getCode().equals(code))
                        .findFirst()
                        .orElse(null);

                if (existing != null) {
                    fieldMapper.updateEntityFromRequest(fReq, existing, template);
                } else {
                    Field newField = fieldMapper.toEntity(fReq, template);
                    template.getFields().add(newField);
                }
            }

            // Pass 2: Set parentField relationships using parentFieldCode
            for (var entry : incomingFields.entrySet()) {
                String code = entry.getKey();
                var fReq = entry.getValue();

                Field field = template.getFields().stream()
                        .filter(f -> f.getCode().equals(code))
                        .findFirst()
                        .orElse(null);

                if (field != null) {
                    if (fReq.getParentFieldCode() != null && !fReq.getParentFieldCode().trim().isEmpty()) {
                        Field parent = template.getFields().stream()
                                .filter(f -> f.getCode().equals(fReq.getParentFieldCode()))
                                .findFirst()
                                .orElse(null);
                        field.setParentField(parent);
                    } else {
                        field.setParentField(null);
                    }
                }
            }
        } else {
            if (template.getFields() != null) {
                template.getFields().clear();
            } else {
                template.setFields(new java.util.HashSet<>());
            }
        }
    }
}
