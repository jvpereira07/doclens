package com.jvpereira.doclens.repository;

import com.jvpereira.doclens.model.extractionResult.ExtractionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExtractionResultRepository extends JpaRepository<ExtractionResult, UUID> {
    Optional<ExtractionResult> findByRequestId(UUID requestId);
}
