package com.jvpereira.doclens.dto;

import com.jvpereira.doclens.model.template.TemplateStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateRequestDTO {
    private String tag;
    private String name;
    private String description;
    private String propHints;
    private TemplateStatus status;
    @Builder.Default
    private java.util.List<FieldRequestDTO> fields = new java.util.ArrayList<>();
}
