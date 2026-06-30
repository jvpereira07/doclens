package com.jvpereira.doclens.mapper;

import com.jvpereira.doclens.dto.FieldRequestDTO;
import com.jvpereira.doclens.dto.FieldResponseDTO;
import com.jvpereira.doclens.model.field.Field;
import com.jvpereira.doclens.model.template.Template;
import org.springframework.stereotype.Component;

@Component
public class FieldMapper {

    public FieldResponseDTO toResponse(Field field) {
        if (field == null) {
            return null;
        }
        return FieldResponseDTO.builder()
                .id(field.getId())
                .code(field.getCode())
                .name(field.getName())
                .description(field.getDescription())
                .required(field.getRequired())
                .type(field.getType())
                .templateId(field.getTemplate() != null ? field.getTemplate().getId() : null)
                .build();
    }

    public Field toEntity(FieldRequestDTO request, Template template) {
        if (request == null) {
            return null;
        }
        Field field = new Field();
        updateEntityFromRequest(request, field, template);
        return field;
    }

    public void updateEntityFromRequest(FieldRequestDTO request, Field field, Template template) {
        if (request == null || field == null) {
            return;
        }
        field.setCode(request.getCode());
        field.setName(request.getName());
        field.setDescription(request.getDescription());
        field.setRequired(request.getRequired());
        field.setType(request.getType());
        field.setTemplate(template);
    }
}
