package com.contextcoach.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.contextcoach.model.DeveloperProfile;
import com.contextcoach.service.DeveloperProfileService;

@RestController
@RequestMapping("/api/developers")
public class DeveloperProfileController {

    private static final Logger logger = LoggerFactory.getLogger(DeveloperProfileController.class);
    private final DeveloperProfileService developerProfileService;

    public DeveloperProfileController(DeveloperProfileService developerProfileService) {
        this.developerProfileService = developerProfileService;
        logger.info("DeveloperProfileController initialized");
    }

    /**
     * Creates a new developer profile
     * 
     * @param name The name of the developer
     * @param experienceLevel The experience level of the developer
     * @param productivityFactor The productivity factor of the developer
     * @param skills The skills of the developer
     * @param preferredWorkHoursPerDay The preferred work hours per day of the developer
     * @return The created developer profile
     */
    @PostMapping
    public ResponseEntity<DeveloperProfile> createDeveloperProfile(
            @RequestParam("name") String name,
            @RequestParam("experienceLevel") String experienceLevel,
            @RequestParam("productivityFactor") Double productivityFactor,
            @RequestParam("skills") List<String> skills,
            @RequestParam("preferredWorkHoursPerDay") Double preferredWorkHoursPerDay) {
        logger.info("Creating developer profile for: {}", name);
        try {
            DeveloperProfile profile = developerProfileService.createDeveloperProfile(
                    name,
                    experienceLevel,
                    productivityFactor,
                    skills,
                    preferredWorkHoursPerDay);
            logger.info("Successfully created developer profile with ID: {}", profile.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(profile);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid input for developer profile creation: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error creating developer profile for: {}", name, e);
            throw e;
        }
    }

    /**
     * Gets all developer profiles
     * 
     * @return All developer profiles
     */
    @GetMapping
    public ResponseEntity<List<DeveloperProfile>> getAllDeveloperProfiles() {
        logger.info("Getting all developer profiles");
        try {
            List<DeveloperProfile> profiles = developerProfileService.getAllDeveloperProfiles();
            logger.debug("Found {} developer profiles", profiles.size());
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            logger.error("Error getting all developer profiles", e);
            throw e;
        }
    }

    /**
     * Gets a developer profile by ID
     * 
     * @param id The ID of the developer profile
     * @return The developer profile, if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<DeveloperProfile> getDeveloperProfileById(@PathVariable String id) {
        logger.info("Getting developer profile by ID: {}", id);
        try {
            return developerProfileService.getDeveloperProfileById(id)
                    .map(profile -> {
                        logger.debug("Found developer profile with ID: {}", id);
                        return ResponseEntity.ok(profile);
                    })
                    .orElseGet(() -> {
                        logger.debug("No developer profile found with ID: {}", id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            logger.error("Error getting developer profile by ID: {}", id, e);
            throw e;
        }
    }

    /**
     * Gets developer profiles by name
     * 
     * @param name The name to search for
     * @return The matching developer profiles
     */
    @GetMapping("/search/name")
    public ResponseEntity<List<DeveloperProfile>> getDeveloperProfilesByName(@RequestParam("name") String name) {
        logger.info("Getting developer profiles by name: {}", name);
        try {
            List<DeveloperProfile> profiles = developerProfileService.getDeveloperProfilesByName(name);
            logger.debug("Found {} developer profiles matching name: {}", profiles.size(), name);
            return ResponseEntity.ok(profiles);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid name parameter: {}", name);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error getting developer profiles by name: {}", name, e);
            throw e;
        }
    }

    /**
     * Gets developer profiles by experience level
     * 
     * @param experienceLevel The experience level to search for
     * @return The matching developer profiles
     */
    @GetMapping("/search/experience")
    public ResponseEntity<List<DeveloperProfile>> getDeveloperProfilesByExperienceLevel(
            @RequestParam("experienceLevel") String experienceLevel) {
        logger.info("Getting developer profiles by experience level: {}", experienceLevel);
        try {
            List<DeveloperProfile> profiles = developerProfileService.getDeveloperProfilesByExperienceLevel(experienceLevel);
            logger.debug("Found {} developer profiles with experience level: {}", profiles.size(), experienceLevel);
            return ResponseEntity.ok(profiles);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid experience level parameter: {}", experienceLevel);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error getting developer profiles by experience level: {}", experienceLevel, e);
            throw e;
        }
    }

    /**
     * Gets developer profiles with a specific skill
     * 
     * @param skill The skill to search for
     * @return The matching developer profiles
     */
    @GetMapping("/search/skill")
    public ResponseEntity<List<DeveloperProfile>> getDeveloperProfilesBySkill(@RequestParam("skill") String skill) {
        logger.info("Getting developer profiles by skill: {}", skill);
        try {
            List<DeveloperProfile> profiles = developerProfileService.getDeveloperProfilesBySkill(skill);
            logger.debug("Found {} developer profiles with skill: {}", profiles.size(), skill);
            return ResponseEntity.ok(profiles);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid skill parameter: {}", skill);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error getting developer profiles by skill: {}", skill, e);
            throw e;
        }
    }

    /**
     * Updates a developer profile
     * 
     * @param id The ID of the developer profile to update
     * @param name The new name of the developer
     * @param experienceLevel The new experience level of the developer
     * @param productivityFactor The new productivity factor of the developer
     * @param skills The new skills of the developer
     * @param preferredWorkHoursPerDay The new preferred work hours per day of the developer
     * @return The updated developer profile
     */
    @PutMapping("/{id}")
    public ResponseEntity<DeveloperProfile> updateDeveloperProfile(
            @PathVariable String id,
            @RequestParam("name") String name,
            @RequestParam("experienceLevel") String experienceLevel,
            @RequestParam("productivityFactor") Double productivityFactor,
            @RequestParam("skills") List<String> skills,
            @RequestParam("preferredWorkHoursPerDay") Double preferredWorkHoursPerDay) {
        logger.info("Updating developer profile with ID: {}", id);
        try {
            DeveloperProfile profile = developerProfileService.updateDeveloperProfile(
                    id,
                    name,
                    experienceLevel,
                    productivityFactor,
                    skills,
                    preferredWorkHoursPerDay);
            logger.info("Successfully updated developer profile with ID: {}", id);
            return ResponseEntity.ok(profile);
        } catch (IllegalArgumentException e) {
            logger.warn("Developer profile not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error updating developer profile with ID: {}", id, e);
            throw e;
        }
    }

    /**
     * Deletes a developer profile
     * 
     * @param id The ID of the developer profile to delete
     * @return No content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeveloperProfile(@PathVariable String id) {
        logger.info("Deleting developer profile with ID: {}", id);
        try {
            developerProfileService.deleteDeveloperProfile(id);
            logger.info("Successfully deleted developer profile with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.warn("Developer profile not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error deleting developer profile with ID: {}", id, e);
            throw e;
        }
    }
}
