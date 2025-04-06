package com.contextcoach.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.contextcoach.model.ScopeEstimationResult;

@Repository
public interface ScopeEstimationResultRepository extends MongoRepository<ScopeEstimationResult, String> {
    // Find all scope estimation results for a specific requirement
    List<ScopeEstimationResult> findByRequirementId(String requirementId);
    
    // Find results with estimated hours less than or equal to the given value
    List<ScopeEstimationResult> findByEstimatedHoursLessThanEqual(Double hours);
    
    // Find results with a specific complexity level
    List<ScopeEstimationResult> findByComplexityLevel(String complexityLevel);
    
    // Find results with confidence level greater than or equal to the given value
    List<ScopeEstimationResult> findByConfidenceLevelGreaterThanEqual(Double confidenceLevel);
}
