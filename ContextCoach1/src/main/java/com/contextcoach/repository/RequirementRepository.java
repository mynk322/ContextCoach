package com.contextcoach.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.contextcoach.model.Requirement;

@Repository
public interface RequirementRepository extends MongoRepository<Requirement, String> {
    // Find requirements by title containing the given text (case-insensitive)
    java.util.List<Requirement> findByTitleContainingIgnoreCase(String title);
    
    // Find requirements by source type
    java.util.List<Requirement> findBySourceType(String sourceType);
    
    // Find requirements with clarity score greater than or equal to the given value
    java.util.List<Requirement> findByClarityScoreGreaterThanEqual(Double clarityScore);
}
