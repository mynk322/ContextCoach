package com.contextcoach.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.contextcoach.model.AmbiguityDetectionResult;

@Repository
public interface AmbiguityDetectionResultRepository extends MongoRepository<AmbiguityDetectionResult, String> {
    // Find all ambiguity detection results for a specific requirement
    List<AmbiguityDetectionResult> findByRequirementId(String requirementId);
    
    // Find results with confidence score greater than or equal to the given value
    List<AmbiguityDetectionResult> findByConfidenceScoreGreaterThanEqual(Double confidenceScore);
    
    // Find results containing a specific ambiguity category
    List<AmbiguityDetectionResult> findByAmbiguityCategoriesContaining(String category);
}
