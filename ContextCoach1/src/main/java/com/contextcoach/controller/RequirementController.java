package com.contextcoach.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.contextcoach.model.AmbiguityDetectionResult;
import com.contextcoach.model.ImplementationPlan;
import com.contextcoach.model.Requirement;
import com.contextcoach.model.ScopeEstimationResult;
import com.contextcoach.service.RequirementService;

@RestController
@RequestMapping("/api/requirements")
public class RequirementController {

    private static final Logger logger = LoggerFactory.getLogger(RequirementController.class);
    private final RequirementService requirementService;

    public RequirementController(RequirementService requirementService) {
        this.requirementService = requirementService;
    }

    /**
     * Uploads a requirement file
     * 
     * @param file The requirement file to upload
     * @param title The title of the requirement
     * @return The created requirement
     */
    @PostMapping("/upload")
    public ResponseEntity<Requirement> uploadRequirement(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title) {
        logger.info("Uploading requirement file: {}", file.getOriginalFilename());
        try {
            Requirement requirement = requirementService.createRequirementFromFile(file, title);
            logger.info("Successfully created requirement with ID: {}", requirement.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(requirement);
        } catch (IOException e) {
            logger.error("Error uploading requirement file: {}", file.getOriginalFilename(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Creates a requirement from text
     * 
     * @param title The title of the requirement
     * @param content The content of the requirement
     * @return The created requirement
     */
    @PostMapping("/text")
    public ResponseEntity<Requirement> createRequirementFromText(
            @RequestBody Map<String, String> requestBody) {
        String title = requestBody.get("title");
        String content = requestBody.get("content");
        logger.info("Creating requirement from text with title: {}", title);
        try {
            Requirement requirement = requirementService.createRequirementFromText(title, content);
            logger.info("Successfully created requirement with ID: {}", requirement.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(requirement);
        } catch (Exception e) {
            logger.error("Error creating requirement from text with title: {}", title, e);
            throw e;
        }
    }

    /**
     * Gets all requirements
     * 
     * @return All requirements
     */
    @GetMapping
    public ResponseEntity<List<Requirement>> getAllRequirements() {
        logger.info("Getting all requirements");
        try {
            List<Requirement> requirements = requirementService.getAllRequirements();
            logger.debug("Found {} requirements", requirements.size());
            return ResponseEntity.ok(requirements);
        } catch (Exception e) {
            logger.error("Error getting all requirements", e);
            throw e;
        }
    }

    /**
     * Gets a requirement by ID
     * 
     * @param id The ID of the requirement
     * @return The requirement, if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Requirement> getRequirementById(@PathVariable String id) {
        logger.info("Getting requirement by ID: {}", id);
        try {
            return requirementService.getRequirementById(id)
                    .map(requirement -> {
                        logger.debug("Found requirement with ID: {}", id);
                        return ResponseEntity.ok(requirement);
                    })
                    .orElseGet(() -> {
                        logger.debug("No requirement found with ID: {}", id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            logger.error("Error getting requirement by ID: {}", id, e);
            throw e;
        }
    }

    /**
     * Analyzes a requirement for ambiguities
     * 
     * @param id The ID of the requirement to analyze
     * @return The ambiguity detection result
     */
    @PostMapping("/{id}/analyze")
    public ResponseEntity<AmbiguityDetectionResult> analyzeRequirement(@PathVariable String id) {
        logger.info("Analyzing requirement with ID: {}", id);
        try {
            AmbiguityDetectionResult result = requirementService.analyzeRequirement(id);
            logger.info("Successfully analyzed requirement with ID: {}", id);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            logger.warn("Requirement not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error analyzing requirement with ID: {}", id, e);
            throw e;
        }
    }

    /**
     * Estimates the scope of a requirement
     * 
     * @param id The ID of the requirement to estimate
     * @return The scope estimation result
     */
    @PostMapping("/{id}/estimate")
    public ResponseEntity<ScopeEstimationResult> estimateScope(@PathVariable String id) {
        logger.info("Estimating scope for requirement with ID: {}", id);
        try {
            ScopeEstimationResult result = requirementService.estimateScope(id);
            logger.info("Successfully estimated scope for requirement with ID: {}", id);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            logger.warn("Requirement not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error estimating scope for requirement with ID: {}", id, e);
            throw e;
        }
    }

    /**
     * Generates an implementation plan for a requirement
     * 
     * @param id The ID of the requirement to plan
     * @return The implementation plan
     */
    @PostMapping("/{id}/plan")
    public ResponseEntity<ImplementationPlan> generateImplementationPlan(@PathVariable String id) {
        logger.info("Generating implementation plan for requirement with ID: {}", id);
        try {
            ImplementationPlan plan = requirementService.generateImplementationPlan(id);
            logger.info("Successfully generated implementation plan for requirement with ID: {}", id);
            return ResponseEntity.ok(plan);
        } catch (IllegalArgumentException e) {
            logger.warn("Requirement not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error generating implementation plan for requirement with ID: {}", id, e);
            throw e;
        }
    }
    
    /**
     * Calculates story points for a requirement
     * 
     * @param id The ID of the requirement to calculate story points for
     * @param repositoryComplexity Optional repository complexity score (0-1 scale)
     * @return A map containing the story points calculation results
     */
    @PostMapping("/{id}/story-points")
    public ResponseEntity<Map<String, Object>> calculateStoryPoints(
            @PathVariable String id,
            @RequestParam(value = "repositoryComplexity", required = false) Double repositoryComplexity) {
        logger.info("Calculating story points for requirement with ID: {}, repository complexity: {}", id, repositoryComplexity);
        try {
            Map<String, Object> storyPoints = requirementService.calculateStoryPoints(id, repositoryComplexity);
            logger.info("Successfully calculated story points for requirement with ID: {}", id);
            return ResponseEntity.ok(storyPoints);
        } catch (IllegalArgumentException e) {
            logger.warn("Requirement not found with ID: {}", id);
            return ResponseEntity.notFound().build(); 
        } catch (Exception e) {
            logger.error("Error calculating story points for requirement with ID: {}", id, e);
            throw e;
        }
    }
    
    /**
     * Calculates story points for a requirement with a specific developer profile
     * 
     * @param id The ID of the requirement to calculate story points for
     * @param developerId The ID of the developer profile to use for calculation
     * @param repositoryComplexity Optional repository complexity score (0-1 scale)
     * @return A map containing the story points calculation results
     */
    @PostMapping("/{id}/story-points/developer/{developerId}")
    public ResponseEntity<Map<String, Object>> calculateStoryPointsWithDeveloper(
            @PathVariable String id,
            @PathVariable String developerId,
            @RequestParam(value = "repositoryComplexity", required = false) Double repositoryComplexity) {
        logger.info("Calculating story points for requirement with ID: {}, developer ID: {}, repository complexity: {}", 
                id, developerId, repositoryComplexity);
        try {
            Map<String, Object> storyPoints = requirementService.calculateStoryPointsWithDeveloper(id, developerId, repositoryComplexity);
            logger.info("Successfully calculated story points for requirement with ID: {} and developer ID: {}", id, developerId);
            return ResponseEntity.ok(storyPoints);
        } catch (IllegalArgumentException e) {
            logger.warn("Requirement or developer profile not found with IDs: {}, {}", id, developerId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error calculating story points for requirement with ID: {} and developer ID: {}", id, developerId, e);
            throw e;
        }
    }
}
