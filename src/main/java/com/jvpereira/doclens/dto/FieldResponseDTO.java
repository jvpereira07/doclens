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
public class FieldResponseDTO {
    private UUID id;
    private String code;
    private String name;
    private String description;
    private Boolean required;
    private FieldType type;
    private UUID templateId;
}
