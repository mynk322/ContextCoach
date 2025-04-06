package com.contextcoach.config;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import com.contextcoach.model.DeveloperProfile;
import com.contextcoach.model.Requirement;
import com.contextcoach.repository.DeveloperProfileRepository;
import com.contextcoach.repository.RequirementRepository;

/**
 * Configuration class to initialize sample data in the database
 */
@Configuration
public class DataInitializerConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializerConfig.class);
    
    /**
     * Initializes sample data in the database when the application starts
     * Doesn't run in the "test" profile to avoid affecting tests
     */
    @Bean
    @Profile("!test")
    public CommandLineRunner initializeData(
            RequirementRepository requirementRepository,
            DeveloperProfileRepository developerProfileRepository,
            MongoTemplate mongoTemplate) {
        
        return args -> {
            logger.info("Initializing sample data");
            
            // Initialize sample developers if they don't exist
            long developerCount = mongoTemplate.count(new Query(), DeveloperProfile.class);
            if (developerCount == 0) {
                logger.info("Creating sample developer profiles");
                
                DeveloperProfile developer1 = new DeveloperProfile();
                developer1.setName("John Smith");
                developer1.setExperienceLevel("Senior");
                developer1.setProductivityFactor(1.2);
                developer1.setSkills(Arrays.asList("Java", "Spring Boot", "RESTful APIs", "Microservices"));
                developer1.setPreferredWorkHoursPerDay(8.0);
                
                DeveloperProfile developer2 = new DeveloperProfile();
                developer2.setName("Jane Doe");
                developer2.setExperienceLevel("Mid-level");
                developer2.setProductivityFactor(1.0);
                developer2.setSkills(Arrays.asList("Java", "JavaScript", "React", "REST"));
                developer2.setPreferredWorkHoursPerDay(7.5);
                
                DeveloperProfile developer3 = new DeveloperProfile();
                developer3.setName("Mike Johnson");
                developer3.setExperienceLevel("Junior");
                developer3.setProductivityFactor(0.8);
                developer3.setSkills(Arrays.asList("Java", "HTML", "CSS", "JavaScript"));
                developer3.setPreferredWorkHoursPerDay(8.0);
                
                List<DeveloperProfile> developers = Arrays.asList(developer1, developer2, developer3);
                mongoTemplate.insertAll(developers);
                logger.info("Saved {} sample developer profiles", developers.size());
            }
            
            // Initialize sample requirements if they don't exist
            long requirementCount = mongoTemplate.count(new Query(), Requirement.class);
            if (requirementCount == 0) {
                logger.info("Creating sample requirements");
                
                Requirement requirement1 = new Requirement();
                requirement1.setTitle("User Authentication System");
                requirement1.setContent("Implement a secure user authentication system with login, " +
                        "registration, and password reset functionality. The system should use " +
                        "JWT for authentication and support social login via Google and Facebook.");
                requirement1.setSourceType("TEXT");
                requirement1.setClarityScore(0.85);
                
                Requirement requirement2 = new Requirement();
                requirement2.setTitle("Reporting Dashboard");
                requirement2.setContent("Create a reporting dashboard that displays key metrics " +
                        "and allows users to generate custom reports. The dashboard should include " +
                        "charts, graphs, and export options for PDF and Excel.");
                requirement2.setSourceType("TEXT");
                requirement2.setClarityScore(0.75);
                
                Requirement requirement3 = new Requirement();
                requirement3.setTitle("Mobile App Integration");
                requirement3.setContent("The system must have API endpoints for mobile app integration. " +
                        "The integration should support push notifications, data synchronization, and " +
                        "offline mode functionality.");
                requirement3.setSourceType("TEXT");
                requirement3.setClarityScore(0.65);
                
                List<Requirement> requirements = Arrays.asList(requirement1, requirement2, requirement3);
                mongoTemplate.insertAll(requirements);
                logger.info("Saved {} sample requirements", requirements.size());
            }
            
            logger.info("Sample data initialization completed");
        };
    }
} 