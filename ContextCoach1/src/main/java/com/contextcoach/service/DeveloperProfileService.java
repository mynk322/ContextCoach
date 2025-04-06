package com.contextcoach.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.contextcoach.exception.ResourceNotFoundException;
import com.contextcoach.exception.ServiceException;
import com.contextcoach.model.DeveloperProfile;
import com.contextcoach.repository.DeveloperProfileRepository;

@Service
public class DeveloperProfileService {
    
    private static final Logger logger = LoggerFactory.getLogger(DeveloperProfileService.class);

    private final DeveloperProfileRepository developerProfileRepository;

    public DeveloperProfileService(DeveloperProfileRepository developerProfileRepository) {
        this.developerProfileRepository = developerProfileRepository;
        logger.info("DeveloperProfileService initialized");
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
     * @throws ServiceException if there's an error creating the profile
     */
    public DeveloperProfile createDeveloperProfile(
            String name,
            String experienceLevel,
            Double productivityFactor,
            List<String> skills,
            Double preferredWorkHoursPerDay) {
        
        logger.info("Creating developer profile for: {}", name);
        
        try {
            // Validate inputs
            if (name == null || name.trim().isEmpty()) {
                logger.error("Developer name cannot be null or empty");
                throw new IllegalArgumentException("Developer name cannot be null or empty");
            }
            
            if (experienceLevel == null || experienceLevel.trim().isEmpty()) {
                logger.error("Experience level cannot be null or empty");
                throw new IllegalArgumentException("Experience level cannot be null or empty");
            }
            
            if (productivityFactor == null || productivityFactor <= 0) {
                logger.error("Productivity factor must be a positive number");
                throw new IllegalArgumentException("Productivity factor must be a positive number");
            }
            
            if (preferredWorkHoursPerDay == null || preferredWorkHoursPerDay <= 0) {
                logger.error("Preferred work hours per day must be a positive number");
                throw new IllegalArgumentException("Preferred work hours per day must be a positive number");
            }
            
            // Create the profile
            logger.debug("Creating developer profile with experience level: {}", experienceLevel);
            DeveloperProfile profile = new DeveloperProfile();
            profile.setName(name);
            profile.setExperienceLevel(experienceLevel);
            profile.setProductivityFactor(productivityFactor);
            profile.setSkills(skills);
            profile.setPreferredWorkHoursPerDay(preferredWorkHoursPerDay);
            
            // Save the profile
            DeveloperProfile savedProfile = developerProfileRepository.save(profile);
            logger.info("Successfully created developer profile with ID: {}", savedProfile.getId());
            return savedProfile;
        } catch (IllegalArgumentException e) {
            // Re-throw these as they're already logged
            throw e;
        } catch (Exception e) {
            logger.error("Error creating developer profile for: {}", name, e);
            throw new ServiceException("Error creating developer profile: " + e.getMessage(), e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gets a developer profile by ID
     * 
     * @param id The ID of the developer profile
     * @return The developer profile, if found
     * @throws ServiceException if there's an error retrieving the profile
     */
    public Optional<DeveloperProfile> getDeveloperProfileById(String id) {
        logger.debug("Getting developer profile by ID: {}", id);
        try {
            Optional<DeveloperProfile> profile = developerProfileRepository.findById(id);
            if (profile.isPresent()) {
                logger.debug("Found developer profile with ID: {}", id);
            } else {
                logger.debug("No developer profile found with ID: {}", id);
            }
            return profile;
        } catch (Exception e) {
            logger.error("Error getting developer profile by ID: {}", id, e);
            throw new ServiceException("Error getting developer profile: " + e.getMessage(), e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gets all developer profiles
     * 
     * @return All developer profiles
     * @throws ServiceException if there's an error retrieving the profiles
     */
    public List<DeveloperProfile> getAllDeveloperProfiles() {
        logger.debug("Getting all developer profiles");
        try {
            List<DeveloperProfile> profiles = developerProfileRepository.findAll();
            logger.debug("Found {} developer profiles", profiles.size());
            return profiles;
        } catch (Exception e) {
            logger.error("Error getting all developer profiles", e);
            throw new ServiceException("Error getting all developer profiles: " + e.getMessage(), e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gets developer profiles by name
     * 
     * @param name The name to search for
     * @return The matching developer profiles
     * @throws ServiceException if there's an error retrieving the profiles
     */
    public List<DeveloperProfile> getDeveloperProfilesByName(String name) {
        logger.debug("Getting developer profiles by name: {}", name);
        try {
            if (name == null || name.trim().isEmpty()) {
                logger.error("Name cannot be null or empty");
                throw new IllegalArgumentException("Name cannot be null or empty");
            }
            
            List<DeveloperProfile> profiles = developerProfileRepository.findByNameContainingIgnoreCase(name);
            logger.debug("Found {} developer profiles matching name: {}", profiles.size(), name);
            return profiles;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error getting developer profiles by name: {}", name, e);
            throw new ServiceException("Error getting developer profiles by name: " + e.getMessage(), e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gets developer profiles by experience level
     * 
     * @param experienceLevel The experience level to search for
     * @return The matching developer profiles
     * @throws ServiceException if there's an error retrieving the profiles
     */
    public List<DeveloperProfile> getDeveloperProfilesByExperienceLevel(String experienceLevel) {
        logger.debug("Getting developer profiles by experience level: {}", experienceLevel);
        try {
            if (experienceLevel == null || experienceLevel.trim().isEmpty()) {
                logger.error("Experience level cannot be null or empty");
                throw new IllegalArgumentException("Experience level cannot be null or empty");
            }
            
            List<DeveloperProfile> profiles = developerProfileRepository.findByExperienceLevel(experienceLevel);
            logger.debug("Found {} developer profiles with experience level: {}", profiles.size(), experienceLevel);
            return profiles;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error getting developer profiles by experience level: {}", experienceLevel, e);
            throw new ServiceException("Error getting developer profiles by experience level: " + e.getMessage(), e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gets developer profiles with a specific skill
     * 
     * @param skill The skill to search for
     * @return The matching developer profiles
     * @throws ServiceException if there's an error retrieving the profiles
     */
    public List<DeveloperProfile> getDeveloperProfilesBySkill(String skill) {
        logger.debug("Getting developer profiles by skill: {}", skill);
        try {
            if (skill == null || skill.trim().isEmpty()) {
                logger.error("Skill cannot be null or empty");
                throw new IllegalArgumentException("Skill cannot be null or empty");
            }
            
            List<DeveloperProfile> profiles = developerProfileRepository.findBySkillsContaining(skill);
            logger.debug("Found {} developer profiles with skill: {}", profiles.size(), skill);
            return profiles;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error getting developer profiles by skill: {}", skill, e);
            throw new ServiceException("Error getting developer profiles by skill: " + e.getMessage(), e, HttpStatus.INTERNAL_SERVER_ERROR);
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
     * @throws ResourceNotFoundException if the profile is not found
     * @throws ServiceException if there's an error updating the profile
     */
    public DeveloperProfile updateDeveloperProfile(
            String id,
            String name,
            String experienceLevel,
            Double productivityFactor,
            List<String> skills,
            Double preferredWorkHoursPerDay) {
        
        logger.info("Updating developer profile with ID: {}", id);
        
        try {
            // Validate inputs
            if (id == null) {
                logger.error("Developer profile ID cannot be null");
                throw new IllegalArgumentException("Developer profile ID cannot be null");
            }
            
            if (name == null || name.trim().isEmpty()) {
                logger.error("Developer name cannot be null or empty");
                throw new IllegalArgumentException("Developer name cannot be null or empty");
            }
            
            if (experienceLevel == null || experienceLevel.trim().isEmpty()) {
                logger.error("Experience level cannot be null or empty");
                throw new IllegalArgumentException("Experience level cannot be null or empty");
            }
            
            if (productivityFactor == null || productivityFactor <= 0) {
                logger.error("Productivity factor must be a positive number");
                throw new IllegalArgumentException("Productivity factor must be a positive number");
            }
            
            if (preferredWorkHoursPerDay == null || preferredWorkHoursPerDay <= 0) {
                logger.error("Preferred work hours per day must be a positive number");
                throw new IllegalArgumentException("Preferred work hours per day must be a positive number");
            }
            
            // Find the profile
            logger.debug("Finding developer profile with ID: {}", id);
            DeveloperProfile profile = developerProfileRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.error("Developer profile not found with ID: {}", id);
                        return new ResourceNotFoundException("Developer profile not found with ID: " + id);
                    });
            
            // Update the profile
            logger.debug("Updating developer profile fields");
            profile.setName(name);
            profile.setExperienceLevel(experienceLevel);
            profile.setProductivityFactor(productivityFactor);
            profile.setSkills(skills);
            profile.setPreferredWorkHoursPerDay(preferredWorkHoursPerDay);
            
            // Save the profile
            DeveloperProfile updatedProfile = developerProfileRepository.save(profile);
            logger.info("Successfully updated developer profile with ID: {}", id);
            return updatedProfile;
        } catch (ResourceNotFoundException | IllegalArgumentException e) {
            // Re-throw these as they're already logged
            throw e;
        } catch (Exception e) {
            logger.error("Error updating developer profile with ID: {}", id, e);
            throw new ServiceException("Error updating developer profile: " + e.getMessage(), e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a developer profile
     * 
     * @param id The ID of the developer profile to delete
     * @throws ServiceException if there's an error deleting the profile
     */
    public void deleteDeveloperProfile(String id) {
        logger.info("Deleting developer profile with ID: {}", id);
        try {
            if (id == null) {
                logger.error("Developer profile ID cannot be null");
                throw new IllegalArgumentException("Developer profile ID cannot be null");
            }
            
            // Check if the profile exists
            if (!developerProfileRepository.existsById(id)) {
                logger.warn("Developer profile not found with ID: {}", id);
                throw new ResourceNotFoundException("Developer profile not found with ID: " + id);
            }
            
            // Delete the profile
            logger.debug("Deleting developer profile with ID: {}", id);
            developerProfileRepository.deleteById(id);
            logger.info("Successfully deleted developer profile with ID: {}", id);
        } catch (ResourceNotFoundException | IllegalArgumentException e) {
            // Re-throw these as they're already logged
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting developer profile with ID: {}", id, e);
            throw new ServiceException("Error deleting developer profile: " + e.getMessage(), e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
