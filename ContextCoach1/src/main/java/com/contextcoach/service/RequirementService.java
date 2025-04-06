package com.contextcoach.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.contextcoach.exception.ResourceNotFoundException;
import com.contextcoach.exception.ServiceException;
import com.contextcoach.model.AmbiguityDetectionResult;
import com.contextcoach.model.ImplementationPlan;
import com.contextcoach.model.Requirement;
import com.contextcoach.model.ScopeEstimationResult;
import com.contextcoach.repository.AmbiguityDetectionResultRepository;
import com.contextcoach.repository.DeveloperProfileRepository;
import com.contextcoach.repository.ImplementationPlanRepository;
import com.contextcoach.repository.RequirementRepository;
import com.contextcoach.repository.ScopeEstimationResultRepository;

@Service
public class RequirementService {
    
    private static final Logger logger = LoggerFactory.getLogger(RequirementService.class);

    private final RequirementRepository requirementRepository;
    private final AmbiguityDetectionResultRepository ambiguityResultRepository;
    private final ScopeEstimationResultRepository scopeResultRepository;
    private final ImplementationPlanRepository implementationPlanRepository;
    private final DeveloperProfileRepository developerProfileRepository;
    private final FileService fileService;
    private final RabbitHoleService rabbitHoleService;

    public RequirementService(
            RequirementRepository requirementRepository,
            AmbiguityDetectionResultRepository ambiguityResultRepository,
            ScopeEstimationResultRepository scopeResultRepository,
            ImplementationPlanRepository implementationPlanRepository,
            DeveloperProfileRepository developerProfileRepository,
            FileService fileService,
            RabbitHoleService rabbitHoleService) {
        this.requirementRepository = requirementRepository;
        this.ambiguityResultRepository = ambiguityResultRepository;
        this.scopeResultRepository = scopeResultRepository;
        this.implementationPlanRepository = implementationPlanRepository;
        this.developerProfileRepository = developerProfileRepository;
        this.fileService = fileService;
        this.rabbitHoleService = rabbitHoleService;
    }

    /**
     * Creates a new requirement from a file
     * 
     * @param file The uploaded file
     * @param title The title of the requirement
     * @return The created requirement
     * @throws IOException If there's an error reading the file
     */
    public Requirement createRequirementFromFile(MultipartFile file, String title) throws IOException {
        logger.info("Creating requirement from file: {}", file.getOriginalFilename());
        try {
            String content = fileService.extractTextFromFile(file);
            String fileName = file.getOriginalFilename();
            String fileType = file.getContentType();
            String sourceType = determineSourceType(fileName, fileType);

            Requirement requirement = new Requirement();
            requirement.setTitle(title);
            requirement.setContent(content);
            requirement.setFileName(fileName);
            requirement.setFileType(fileType);
            requirement.setSourceType(sourceType);

            Requirement savedRequirement = requirementRepository.save(requirement);
            logger.info("Successfully created requirement with ID: {}", savedRequirement.getId());
            return savedRequirement;
        } catch (IOException e) {
            logger.error("Error extracting text from file: {}", file.getOriginalFilename(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error creating requirement from file", e);
            throw e;
        }
    }

    /**
     * Creates a new requirement from text
     * 
     * @param title The title of the requirement
     * @param content The content of the requirement
     * @return The created requirement
     */
    public Requirement createRequirementFromText(String title, String content) {
        logger.info("Creating requirement from text with title: {}", title);
        try {
            Requirement requirement = new Requirement();
            requirement.setTitle(title);
            requirement.setContent(content);
            requirement.setSourceType("TEXT");

            Requirement savedRequirement = requirementRepository.save(requirement);
            logger.info("Successfully created requirement with ID: {}", savedRequirement.getId());
            return savedRequirement;
        } catch (Exception e) {
            logger.error("Error creating requirement from text", e);
            throw e;
        }
    }

    /**
     * Gets a requirement by ID
     * 
     * @param id The ID of the requirement
     * @return The requirement, if found
     */
    public Optional<Requirement> getRequirementById(String id) {
        logger.debug("Getting requirement by ID: {}", id);
        try {
            Optional<Requirement> requirement = requirementRepository.findById(id);
            if (requirement.isPresent()) {
                logger.debug("Found requirement with ID: {}", id);
            } else {
                logger.debug("No requirement found with ID: {}", id);
            }
            return requirement;
        } catch (Exception e) {
            logger.error("Error getting requirement by ID: {}", id, e);
            throw e;
        }
    }

    /**
     * Gets all requirements
     * 
     * @return All requirements
     */
    public List<Requirement> getAllRequirements() {
        logger.debug("Getting all requirements");
        try {
            List<Requirement> requirements = requirementRepository.findAll();
            logger.debug("Found {} requirements", requirements.size());
            return requirements;
        } catch (Exception e) {
            logger.error("Error getting all requirements", e);
            throw e;
        }
    }

    /**
     * Analyzes a requirement for ambiguities
     * 
     * @param requirementId The ID of the requirement to analyze
     * @return The ambiguity detection result
     * @throws ResourceNotFoundException if the requirement is not found
     */
    public AmbiguityDetectionResult analyzeRequirement(String requirementId) {
        logger.info("Analyzing requirement with ID: {}", requirementId);
        try {
            Requirement requirement = requirementRepository.findById(requirementId)
                    .orElseThrow(() -> {
                        logger.error("Requirement not found with ID: {}", requirementId);
                        return new ResourceNotFoundException("Requirement not found with ID: " + requirementId);
                    });

            logger.debug("Detecting ambiguities in requirement content");
            Map<String, Object> analysisResult = rabbitHoleService.detectAmbiguities(requirement.getContent());

            AmbiguityDetectionResult result = new AmbiguityDetectionResult();
            result.setRequirement(requirement);
            
            @SuppressWarnings("unchecked")
            List<String> categories = (List<String>) analysisResult.get("ambiguityCategories");
            result.setAmbiguityCategories(categories);
            
            result.setAnalysis((String) analysisResult.get("analysis"));
            result.setConfidenceScore((Double) analysisResult.get("confidenceScore"));
            result.setSuggestedImprovements((String) analysisResult.get("suggestedImprovements"));

            AmbiguityDetectionResult savedResult = ambiguityResultRepository.save(result);
            logger.info("Successfully analyzed requirement with ID: {}", requirementId);
            return savedResult;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error analyzing requirement with ID: {}", requirementId, e);
            throw new ServiceException("Error analyzing requirement: " + e.getMessage(), e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Estimates the scope of a requirement
     * 
     * @param requirementId The ID of the requirement to estimate
     * @return The scope estimation result
     * @throws ResourceNotFoundException if the requirement is not found
     */
    public ScopeEstimationResult estimateScope(String requirementId) {
        logger.info("Estimating scope for requirement with ID: {}", requirementId);
        try {
            Requirement requirement = requirementRepository.findById(requirementId)
                    .orElseThrow(() -> {
                        logger.error("Requirement not found with ID: {}", requirementId);
                        return new ResourceNotFoundException("Requirement not found with ID: " + requirementId);
                    });

            logger.debug("Estimating scope for requirement content");
            Map<String, Object> estimationResult = rabbitHoleService.estimateScope(requirement.getContent());

            ScopeEstimationResult result = new ScopeEstimationResult();
            result.setRequirement(requirement);
            result.setEstimatedHours((Double) estimationResult.get("estimatedHours"));
            result.setComplexityLevel((String) estimationResult.get("complexityLevel"));
            result.setConfidenceLevel((Double) estimationResult.get("confidenceLevel"));
            result.setJustification((String) estimationResult.get("justification"));
            result.setRiskFactors((String) estimationResult.get("riskFactors"));

            ScopeEstimationResult savedResult = scopeResultRepository.save(result);
            logger.info("Successfully estimated scope for requirement with ID: {}", requirementId);
            return savedResult;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error estimating scope for requirement with ID: {}", requirementId, e);
            throw new ServiceException("Error estimating scope: " + e.getMessage(), e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Calculates story points for a requirement
     * 
     * @param requirementId The ID of the requirement to calculate story points for
     * @param repositoryComplexity Optional repository complexity score (can be null)
     * @return A map containing the story points calculation results
     * @throws ResourceNotFoundException if the requirement is not found
     */
    public Map<String, Object> calculateStoryPoints(String requirementId, Double repositoryComplexity) {
        logger.info("Calculating story points for requirement with ID: {}", requirementId);
        try {
            Requirement requirement = requirementRepository.findById(requirementId)
                    .orElseThrow(() -> {
                        logger.error("Requirement not found with ID: {}", requirementId);
                        return new ResourceNotFoundException("Requirement not found with ID: " + requirementId);
                    });

            logger.debug("Calculating story points for requirement content with repository complexity: {}", repositoryComplexity);
            Map<String, Object> storyPointsResult = rabbitHoleService.calculateStoryPoints(requirement.getContent(), repositoryComplexity, null);
            
            logger.info("Successfully calculated story points for requirement with ID: {}", requirementId);
            return storyPointsResult;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error calculating story points for requirement with ID: {}", requirementId, e);
            throw new ServiceException("Error calculating story points: " + e.getMessage(), e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Calculates story points for a requirement with developer profile
     * 
     * @param requirementId The ID of the requirement to calculate story points for
     * @param developerId The ID of the developer profile to use for calculation
     * @param repositoryComplexity Optional repository complexity score (can be null)
     * @return A map containing the story points calculation results
     * @throws ResourceNotFoundException if the requirement or developer profile is not found
     */
    public Map<String, Object> calculateStoryPointsWithDeveloper(String requirementId, String developerId, Double repositoryComplexity) {
        logger.info("Calculating story points for requirement with ID: {} and developer ID: {}", requirementId, developerId);
        try {
            // Get the requirement
            Requirement requirement = requirementRepository.findById(requirementId)
                    .orElseThrow(() -> {
                        logger.error("Requirement not found with ID: {}", requirementId);
                        return new ResourceNotFoundException("Requirement not found with ID: " + requirementId);
                    });
            
            // Get the developer profile
            com.contextcoach.model.DeveloperProfile developerProfile = developerProfileRepository.findById(developerId)
                    .orElseThrow(() -> {
                        logger.error("Developer profile not found with ID: {}", developerId);
                        return new ResourceNotFoundException("Developer profile not found with ID: " + developerId);
                    });

            logger.debug("Calculating story points for requirement content with repository complexity: {} and developer profile: {}", 
                    repositoryComplexity, developerProfile.getName());
            Map<String, Object> storyPointsResult = rabbitHoleService.calculateStoryPoints(
                    requirement.getContent(), repositoryComplexity, developerProfile);
            
            logger.info("Successfully calculated story points for requirement with ID: {} and developer ID: {}", requirementId, developerId);
            return storyPointsResult;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error calculating story points for requirement with ID: {} and developer ID: {}", requirementId, developerId, e);
            throw new ServiceException("Error calculating story points: " + e.getMessage(), e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Calculates story points for a requirement without repository complexity
     * 
     * @param requirementId The ID of the requirement to calculate story points for
     * @return A map containing the story points calculation results
     * @throws ResourceNotFoundException if the requirement is not found
     */
    public Map<String, Object> calculateStoryPoints(String requirementId) {
        return calculateStoryPoints(requirementId, null);
    }

    /**
     * Generates an implementation plan for a requirement
     * 
     * @param requirementId The ID of the requirement to plan
     * @return The implementation plan
     * @throws ResourceNotFoundException if the requirement is not found
     */
    public ImplementationPlan generateImplementationPlan(String requirementId) {
        logger.info("Generating implementation plan for requirement with ID: {}", requirementId);
        try {
            Requirement requirement = requirementRepository.findById(requirementId)
                    .orElseThrow(() -> {
                        logger.error("Requirement not found with ID: {}", requirementId);
                        return new ResourceNotFoundException("Requirement not found with ID: " + requirementId);
                    });

            logger.debug("Generating implementation plan for requirement content");
            Map<String, Object> planResult = rabbitHoleService.generateImplementationPlan(requirement.getContent());

            ImplementationPlan plan = new ImplementationPlan();
            plan.setRequirement(requirement);
            plan.setSummary((String) planResult.get("summary"));
            
            @SuppressWarnings("unchecked")
            List<String> steps = (List<String>) planResult.get("implementationSteps");
            plan.setImplementationSteps(steps);
            
            plan.setTechnicalApproach((String) planResult.get("technicalApproach"));
            plan.setDependencies((String) planResult.get("dependencies"));

            ImplementationPlan savedPlan = implementationPlanRepository.save(plan);
            logger.info("Successfully generated implementation plan for requirement with ID: {}", requirementId);
            return savedPlan;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error generating implementation plan for requirement with ID: {}", requirementId, e);
            throw new ServiceException("Error generating implementation plan: " + e.getMessage(), e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Determines the source type based on the file name and content type
     * 
     * @param fileName The name of the file
     * @param contentType The content type of the file
     * @return The source type
     */
    private String determineSourceType(String fileName, String contentType) {
        if (fileName == null || contentType == null) {
            return "UNKNOWN";
        }

        if (contentType.contains("pdf")) {
            return "PDF";
        } else if (contentType.contains("text") || fileName.endsWith(".txt")) {
            return "TEXT";
        } else if (contentType.contains("json") || fileName.endsWith(".json")) {
            return "JSON";
        } else if (contentType.contains("excel") || fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
            return "EXCEL";
        } else if (contentType.contains("word") || fileName.endsWith(".docx") || fileName.endsWith(".doc")) {
            return "WORD";
        } else {
            return "OTHER";
        }
    }
}
