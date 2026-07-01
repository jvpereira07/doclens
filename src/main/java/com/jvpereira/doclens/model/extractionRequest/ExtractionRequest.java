package com.jvpereira.doclens.model.extractionRequest;

import com.jvpereira.doclens.model.file.File;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name= "extraction_requests")
public class ExtractionRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "prompt_used", columnDefinition = "TEXT")
    private String promptUsed;
    @Column(name= "raw_ai_response", columnDefinition = "TEXT")
    private String rawAiResponse;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ExtractionStatus status;
    @Column(name="created_at")
    private Instant createdAt;
    @OneToOne
    @JoinColumn(name = "file_id")
    private File file;
    @ManyToOne
    @JoinColumn(name = "template_id")
    private com.jvpereira.doclens.model.template.Template template;
}
