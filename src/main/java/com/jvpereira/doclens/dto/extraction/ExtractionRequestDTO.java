package com.jvpereira.doclens.dto.extraction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExtractionRequestDTO {
    private String templateCode;
    private String document;
}
