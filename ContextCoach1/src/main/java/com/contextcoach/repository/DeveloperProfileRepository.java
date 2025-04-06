package com.contextcoach.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.contextcoach.model.DeveloperProfile;

@Repository
public interface DeveloperProfileRepository extends MongoRepository<DeveloperProfile, String> {
    // Find developer by name (case-insensitive)
    List<DeveloperProfile> findByNameContainingIgnoreCase(String name);
    
    // Find developers by experience level
    List<DeveloperProfile> findByExperienceLevel(String experienceLevel);
    
    // Find developers with productivity factor greater than or equal to the given value
    List<DeveloperProfile> findByProductivityFactorGreaterThanEqual(Double productivityFactor);
    
    // Find developers with a specific skill
    List<DeveloperProfile> findBySkillsContaining(String skill);
}
