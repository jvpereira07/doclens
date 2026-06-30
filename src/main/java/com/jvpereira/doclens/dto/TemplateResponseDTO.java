package com.jvpereira.doclens.dto;

import com.jvpereira.doclens.model.template.TemplateStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateResponseDTO {
    private UUID id;
    private String tag;
    private String name;
    private String description;
    private String propHints;
    private TemplateStatus status;
    private Set<FieldResponseDTO> fields;
}
