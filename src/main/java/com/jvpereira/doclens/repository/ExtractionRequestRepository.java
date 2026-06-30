package com.jvpereira.doclens.repository;

import com.jvpereira.doclens.model.extractionRequest.ExtractionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExtractionRequestRepository extends JpaRepository<ExtractionRequest, UUID> {
    List<ExtractionRequest> findAllByOrderByCreatedAtDesc();
}
