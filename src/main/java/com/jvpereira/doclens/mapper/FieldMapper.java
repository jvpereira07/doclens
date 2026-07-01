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
        var builder = FieldResponseDTO.builder()
                .id(field.getId())
                .code(field.getCode())
                .name(field.getName())
                .description(field.getDescription())
                .required(field.getRequired())
                .type(field.getType())
                .templateId(field.getTemplate() != null ? field.getTemplate().getId() : null)
                .parentFieldId(field.getParentField() != null ? field.getParentField().getId() : null)
                .parentFieldCode(field.getParentField() != null ? field.getParentField().getCode() : null);

        if (field.getSubFields() != null) {
            builder.subFields(field.getSubFields().stream()
                    .map(this::toResponse)
                    .collect(java.util.stream.Collectors.toList()));
        }
        return builder.build();
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

        if (request.getSubFields() != null) {
            java.util.Map<String, FieldRequestDTO> incomingSubs = request.getSubFields().stream()
                    .filter(sub -> sub.getCode() != null && !sub.getCode().trim().isEmpty())
                    .collect(java.util.stream.Collectors.toMap(FieldRequestDTO::getCode, f -> f, (f1, f2) -> f1));

            if (field.getSubFields() == null) {
                field.setSubFields(new java.util.HashSet<>());
            }

            // Remove subfields not in request
            field.getSubFields().removeIf(sub -> !incomingSubs.containsKey(sub.getCode()));

            // Update existing or add new ones
            for (var entry : incomingSubs.entrySet()) {
                String code = entry.getKey();
                FieldRequestDTO subReq = entry.getValue();

                Field existingSub = field.getSubFields().stream()
                        .filter(sub -> sub.getCode().equals(code))
                        .findFirst()
                        .orElse(null);

                if (existingSub != null) {
                    updateEntityFromRequest(subReq, existingSub, template);
                } else {
                    Field newSub = toEntity(subReq, template);
                    newSub.setParentField(field);
                    field.getSubFields().add(newSub);
                }
            }
        } else {
            if (field.getSubFields() != null) {
                field.getSubFields().clear();
            } else {
                field.setSubFields(new java.util.HashSet<>());
            }
        }
    }
}
