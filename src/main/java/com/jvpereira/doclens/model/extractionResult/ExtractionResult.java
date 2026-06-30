package com.jvpereira.doclens.model.extractionResult;

import com.jvpereira.doclens.model.extractionRequest.ExtractionRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "extraction_results")
public class ExtractionResult {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    @JoinColumn(name= "request_id")
    private ExtractionRequest request;
    @Column(name = "process_time_ms")
    private int processTimeMs;
    @Column(name = "created_at")
    @CurrentTimestamp
    private Instant createdAt;
    @Column(name = "data", columnDefinition = "TEXT")
    private String data;
}
