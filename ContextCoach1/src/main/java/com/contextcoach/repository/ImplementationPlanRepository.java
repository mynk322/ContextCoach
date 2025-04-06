package com.contextcoach.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.contextcoach.model.ImplementationPlan;

@Repository
public interface ImplementationPlanRepository extends MongoRepository<ImplementationPlan, String> {
    // Find all implementation plans for a specific requirement
    List<ImplementationPlan> findByRequirementId(String requirementId);
    
    // Find plans containing a specific technical approach (case-insensitive)
    List<ImplementationPlan> findByTechnicalApproachContainingIgnoreCase(String technicalApproach);
    
    // Find plans containing specific dependencies (case-insensitive)
    List<ImplementationPlan> findByDependenciesContainingIgnoreCase(String dependencies);
}
