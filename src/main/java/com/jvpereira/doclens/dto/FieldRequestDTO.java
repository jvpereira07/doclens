package com.jvpereira.doclens.dto;

import com.jvpereira.doclens.model.field.FieldType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldRequestDTO {
    private String code;
    private String name;
    private String description;
    private Boolean required;
    private FieldType type;
    private UUID templateId;
    private UUID parentFieldId;
    private String parentFieldCode;
    @Builder.Default
    private java.util.List<FieldRequestDTO> subFields = new java.util.ArrayList<>();
}
