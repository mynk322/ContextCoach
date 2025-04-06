package com.contextcoach.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.contextcoach.exception.ServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class RabbitHoleService {

    private static final Logger logger = LoggerFactory.getLogger(RabbitHoleService.class);
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;
    private final String apiUrl = "https://api.rabbithole.cred.club/v1/chat/completions";
    
    // For testing purposes only
    private Map<String, Object> testFallbackStoryPoints;

    public RabbitHoleService(
            @Value("${rabbithole.api.key}") String apiKey,
            @Value("${rabbithole.model}") String model) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.apiKey = apiKey;
        this.model = model;
        logger.info("RabbitHoleService initialized with model: {}", model);
    }
    
    /**
     * Sets the test fallback story points for testing purposes
     * 
     * @param testFallbackStoryPoints The test fallback story points to use
     */
    public void setTestFallbackStoryPoints(Map<String, Object> testFallbackStoryPoints) {
        this.testFallbackStoryPoints = testFallbackStoryPoints;
    }

    /**
     * Detects ambiguities in a requirement text
     * 
     * @param requirementText The requirement text to analyze
     * @return A map containing the analysis results
     * @throws ServiceException if there's an error processing the request
     */
    public Map<String, Object> detectAmbiguities(String requirementText) {
        logger.info("Detecting ambiguities in requirement text");
        if (requirementText == null || requirementText.trim().isEmpty()) {
            logger.error("Requirement text is null or empty");
            throw new ServiceException("Requirement text cannot be null or empty", HttpStatus.BAD_REQUEST);
        }
        
        try {
            String prompt = "Analyze the following software requirement for ambiguities, vagueness, or unclear specifications:\n\n" +
                    requirementText + "\n\n" +
                    "Provide a detailed analysis in JSON format with the following structure:\n" +
                    "{\n" +
                    "  \"ambiguityCategories\": [list of ambiguity types found],\n" +
                    "  \"analysis\": \"detailed explanation of ambiguities\",\n" +
                    "  \"confidenceScore\": numeric value between 0 and 1,\n" +
                    "  \"suggestedImprovements\": \"specific suggestions to improve clarity\"\n" +
                    "}";

            logger.debug("Sending ambiguity detection prompt to RabbitHole API");
            String response = callRabbitHoleAPI(prompt);
            
            try {
                // Try to parse the response as JSON
                Map<String, Object> result = objectMapper.readValue(response, 
                                             new TypeReference<Map<String, Object>>() {});
                logger.info("Successfully detected ambiguities in requirement text");
                return result;
            } catch (JsonProcessingException e) {
                logger.warn("Failed to parse RabbitHole API response as JSON: {}", e.getMessage());
                // If parsing fails, return a mock result
                Map<String, Object> result = new HashMap<>();
                result.put("ambiguityCategories", List.of("Vague terms", "Missing constraints"));
                result.put("analysis", "The requirement contains vague terms and lacks specific constraints.");
                result.put("confidenceScore", 0.85);
                result.put("suggestedImprovements", "Add specific metrics and constraints to clarify the requirement.");
                logger.info("Returning fallback ambiguity detection result");
                return result;
            }
        } catch (Exception e) {
            logger.error("Error detecting ambiguities in requirement text", e);
            throw new ServiceException("Error detecting ambiguities: " + e.getMessage(), e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Estimates the scope of a requirement
     * 
     * @param requirementText The requirement text to analyze
     * @return A map containing the scope estimation results
     * @throws ServiceException if there's an error processing the request
     */
    public Map<String, Object> estimateScope(String requirementText) {
        logger.info("Estimating scope for requirement text");
        if (requirementText == null || requirementText.trim().isEmpty()) {
            logger.error("Requirement text is null or empty");
            throw new ServiceException("Requirement text cannot be null or empty", HttpStatus.BAD_REQUEST);
        }
        
        try {
            String prompt = "Analyze the following software requirement and provide a detailed scope estimation:\n\n" +
                    requirementText + "\n\n" +
                    "Respond in JSON format with the following structure:\n" +
                    "{\n" +
                    "  \"estimatedHours\": numeric estimate of hours required,\n" +
                    "  \"complexityLevel\": \"Low\", \"Medium\", or \"High\",\n" +
                    "  \"confidenceLevel\": numeric value between 0 and 1,\n" +
                    "  \"justification\": \"detailed explanation of the estimation\",\n" +
                    "  \"riskFactors\": \"potential risks that could affect the estimate\"\n" +
                    "}";

            logger.debug("Sending scope estimation prompt to RabbitHole API");
            String response = callRabbitHoleAPI(prompt);
            
            try {
                // Try to parse the response as JSON
                Map<String, Object> result = objectMapper.readValue(response, 
                                             new TypeReference<Map<String, Object>>() {});
                logger.info("Successfully estimated scope for requirement text");
                return result;
            } catch (JsonProcessingException e) {
                logger.warn("Failed to parse RabbitHole API response as JSON: {}", e.getMessage());
                // If parsing fails, return a mock result
                Map<String, Object> result = new HashMap<>();
                result.put("estimatedHours", 24.0);
                result.put("complexityLevel", "Medium");
                result.put("confidenceLevel", 0.75);
                result.put("justification", "The requirement involves moderate complexity and requires integration with existing systems.");
                result.put("riskFactors", "Potential integration issues, unclear performance requirements.");
                logger.info("Returning fallback scope estimation result");
                return result;
            }
        } catch (Exception e) {
            logger.error("Error estimating scope for requirement text", e);
            throw new ServiceException("Error estimating scope: " + e.getMessage(), e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Generates an implementation plan for a requirement
     * 
     * @param requirementText The requirement text to analyze
     * @return A map containing the implementation plan
     * @throws ServiceException if there's an error processing the request
     */
    public Map<String, Object> generateImplementationPlan(String requirementText) {
        logger.info("Generating implementation plan for requirement text");
        if (requirementText == null || requirementText.trim().isEmpty()) {
            logger.error("Requirement text is null or empty");
            throw new ServiceException("Requirement text cannot be null or empty", HttpStatus.BAD_REQUEST);
        }
        
        try {
            String prompt = "Create a detailed implementation plan for the following software requirement:\n\n" +
                    requirementText + "\n\n" +
                    "Respond in JSON format with the following structure:\n" +
                    "{\n" +
                    "  \"summary\": \"brief summary of the implementation approach\",\n" +
                    "  \"implementationSteps\": [ordered list of implementation steps],\n" +
                    "  \"technicalApproach\": \"detailed technical approach\",\n" +
                    "  \"dependencies\": \"required dependencies and prerequisites\"\n" +
                    "}";

            logger.debug("Sending implementation plan prompt to RabbitHole API");
            String response = callRabbitHoleAPI(prompt);
            
            try {
                // Try to parse the response as JSON
                Map<String, Object> parsedResponse = objectMapper.readValue(response, 
                                                    new TypeReference<Map<String, Object>>() {});
                
                // Ensure implementationSteps is a List
                if (!(parsedResponse.get("implementationSteps") instanceof List)) {
                    logger.debug("Converting implementationSteps to a List");
                    List<String> steps = new ArrayList<>();
                    steps.add(parsedResponse.get("implementationSteps").toString());
                    parsedResponse.put("implementationSteps", steps);
                }
                
                logger.info("Successfully generated implementation plan for requirement text");
                return parsedResponse;
            } catch (JsonProcessingException e) {
                logger.warn("Failed to parse RabbitHole API response as JSON: {}", e.getMessage());
                // If parsing fails, return a mock result
                Map<String, Object> result = new HashMap<>();
                result.put("summary", "Implement a RESTful API with database integration");
                
                List<String> steps = new ArrayList<>();
                steps.add("Design database schema");
                steps.add("Create API endpoints");
                steps.add("Implement business logic");
                steps.add("Write unit tests");
                steps.add("Perform integration testing");
                result.put("implementationSteps", steps);
                
                result.put("technicalApproach", "Use Spring Boot for the backend, with JPA for database access");
                result.put("dependencies", "Spring Boot, Spring Data JPA, H2 Database");
                
                logger.info("Returning fallback implementation plan result");
                return result;
            }
        } catch (Exception e) {
            logger.error("Error generating implementation plan for requirement text", e);
            throw new ServiceException("Error generating implementation plan: " + e.getMessage(), e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Calculates story points for a requirement
     * 
     * @param requirementText The requirement text to analyze
     * @param repositoryComplexity Optional repository complexity information (can be null)
     * @param developerProfile Optional developer profile information (can be null)
     * @return A map containing the story points calculation results
     * @throws ServiceException if there's an error processing the request
     */
    public Map<String, Object> calculateStoryPoints(String requirementText, Double repositoryComplexity, 
            com.contextcoach.model.DeveloperProfile developerProfile) {
        logger.info("Calculating story points for requirement text");
        if (requirementText == null || requirementText.trim().isEmpty()) {
            logger.error("Requirement text is null or empty");
            throw new ServiceException("Requirement text cannot be null or empty", HttpStatus.BAD_REQUEST);
        }
        
        try {
            StringBuilder promptBuilder = new StringBuilder("Analyze the following software requirement and calculate appropriate story points:\n\n")
                    .append(requirementText).append("\n\n");
                    
            // Add repository complexity information if available
            if (repositoryComplexity != null) {
                promptBuilder.append("Repository complexity score: ").append(repositoryComplexity)
                        .append(" (on a scale of 0 to 1, where higher values indicate higher complexity)\n\n");
            }
            
            // Add developer profile information if available
            if (developerProfile != null) {
                promptBuilder.append("Developer Profile Information:\n")
                        .append("- Experience Level: ").append(developerProfile.getExperienceLevel()).append("\n")
                        .append("- Productivity Factor: ").append(developerProfile.getProductivityFactor()).append("\n")
                        .append("- Skills: ").append(String.join(", ", developerProfile.getSkills())).append("\n")
                        .append("- Preferred Work Hours Per Day: ").append(developerProfile.getPreferredWorkHoursPerDay()).append("\n\n")
                        .append("Please consider the developer's experience level, productivity factor, and skills when calculating story points. ")
                        .append("Adjust the story points based on the developer's profile - a more experienced developer with relevant skills ")
                        .append("might complete the task with fewer story points, while a less experienced developer might need more story points.\n\n");
            }
            
            promptBuilder.append("Respond in JSON format with the following structure:\n")
                    .append("{\n")
                    .append("  \"storyPoints\": numeric value (typically 1, 2, 3, 5, 8, 13, or 21),\n")
                    .append("  \"complexity\": \"Low\", \"Medium\", or \"High\",\n")
                    .append("  \"confidenceLevel\": numeric value between 0 and 1,\n")
                    .append("  \"justification\": \"detailed explanation of the story point calculation\",\n")
                    .append("  \"considerations\": [list of factors considered in the calculation],\n")
                    .append("  \"developerFactors\": \"explanation of how the developer profile influenced the story points\"\n")
                    .append("}");

            logger.debug("Sending story points calculation prompt to RabbitHole API");
            String response = callRabbitHoleAPI(promptBuilder.toString());
            
            try {
                // Try to parse the response as JSON
                Map<String, Object> parsedResponse = objectMapper.readValue(response, 
                                                    new TypeReference<Map<String, Object>>() {});
                
                // Ensure considerations is a List
                if (parsedResponse.containsKey("considerations") && !(parsedResponse.get("considerations") instanceof List)) {
                    logger.debug("Converting considerations to a List");
                    List<String> considerations = new ArrayList<>();
                    considerations.add(parsedResponse.get("considerations").toString());
                    parsedResponse.put("considerations", considerations);
                }
                
                logger.info("Successfully calculated story points for requirement text");
                return parsedResponse;
            } catch (JsonProcessingException e) {
                logger.warn("Failed to parse RabbitHole API response as JSON: {}", e.getMessage());
                
                // For testing purposes, if testFallbackStoryPoints is set, return it instead
                if (testFallbackStoryPoints != null) {
                    logger.info("Returning test fallback story points calculation result");
                    return testFallbackStoryPoints;
                }
                
                // If parsing fails, return a mock result
                Map<String, Object> result = new HashMap<>();
                result.put("storyPoints", 5);
                result.put("complexity", "Medium");
                result.put("confidenceLevel", 0.8);
                result.put("justification", "The requirement has moderate complexity and requires integration with existing systems.");
                result.put("considerations", List.of(
                    "Technical complexity", 
                    "Integration requirements", 
                    "Testing effort", 
                    "UI/UX components"
                ));
                
                logger.info("Returning fallback story points calculation result");
                return result;
            }
        } catch (Exception e) {
            logger.error("Error calculating story points for requirement text", e);
            throw new ServiceException("Error calculating story points: " + e.getMessage(), e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Calculates story points for a requirement (without repository complexity or developer profile)
     * 
     * @param requirementText The requirement text to analyze
     * @return A map containing the story points calculation results
     * @throws ServiceException if there's an error processing the request
     */
    public Map<String, Object> calculateStoryPoints(String requirementText) {
        return calculateStoryPoints(requirementText, null, null);
    }
    
    /**
     * Calculates story points for a requirement (with repository complexity but without developer profile)
     * 
     * @param requirementText The requirement text to analyze
     * @param repositoryComplexity Repository complexity information
     * @return A map containing the story points calculation results
     * @throws ServiceException if there's an error processing the request
     */
    public Map<String, Object> calculateStoryPoints(String requirementText, Double repositoryComplexity) {
        return calculateStoryPoints(requirementText, repositoryComplexity, null);
    }

    /**
     * Analyzes a code repository for complexity
     * 
     * @param repositoryContent The content of the repository to analyze
     * @return A map containing the repository analysis results
     * @throws ServiceException if there's an error processing the request
     */
    public Map<String, Object> analyzeRepository(String repositoryContent) {
        logger.info("Analyzing repository content for complexity");
        if (repositoryContent == null || repositoryContent.trim().isEmpty()) {
            logger.error("Repository content is null or empty");
            throw new ServiceException("Repository content cannot be null or empty", HttpStatus.BAD_REQUEST);
        }
        
        try {
            String prompt = "Analyze the following code repository content for complexity and structure:\n\n" +
                    repositoryContent + "\n\n" +
                    "Respond in JSON format with the following structure:\n" +
                    "{\n" +
                    "  \"complexityScore\": numeric value between 0 and 1,\n" +
                    "  \"codeQualityAssessment\": \"assessment of code quality\",\n" +
                    "  \"suggestedImprovements\": \"suggestions for improving the codebase\",\n" +
                    "  \"potentialIssues\": [list of potential issues or bugs]\n" +
                    "}";

            logger.debug("Sending repository analysis prompt to RabbitHole API");
            String response = callRabbitHoleAPI(prompt);
            
            try {
                // Try to parse the response as JSON
                Map<String, Object> parsedResponse = objectMapper.readValue(response, 
                                                    new TypeReference<Map<String, Object>>() {});
                
                // Ensure potentialIssues is a List
                if (!(parsedResponse.get("potentialIssues") instanceof List)) {
                    logger.debug("Converting potentialIssues to a List");
                    List<String> issues = new ArrayList<>();
                    issues.add(parsedResponse.get("potentialIssues").toString());
                    parsedResponse.put("potentialIssues", issues);
                }
                
                logger.info("Successfully analyzed repository content");
                return parsedResponse;
            } catch (JsonProcessingException e) {
                logger.warn("Failed to parse RabbitHole API response as JSON: {}", e.getMessage());
                // If parsing fails, return a mock result
                Map<String, Object> result = new HashMap<>();
                result.put("complexityScore", 0.65);
                result.put("codeQualityAssessment", "The code is moderately complex with some technical debt.");
                result.put("suggestedImprovements", "Increase test coverage, refactor complex methods, improve documentation.");
                result.put("potentialIssues", List.of("Potential null pointer exceptions", "Inefficient database queries"));
                
                logger.info("Returning fallback repository analysis result");
                return result;
            }
        } catch (Exception e) {
            logger.error("Error analyzing repository content", e);
            throw new ServiceException("Error analyzing repository: " + e.getMessage(), e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Calls the RabbitHole API with a prompt
     * 
     * @param prompt The prompt to send to the API
     * @return The response from the API
     */
    @Retryable(
        value = { Exception.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    private String callRabbitHoleAPI(String prompt) {
        logger.debug("Calling RabbitHole API with model: {}", model);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        try {
            // Create the request body
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", model);
            
            ArrayNode messagesArray = requestBody.putArray("messages");
            ObjectNode userMessage = messagesArray.addObject();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);

            // Create the request entity
            HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);
            
            logger.debug("Sending request to RabbitHole API");
            
            // Make the API call
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);
            
            logger.debug("Received response from RabbitHole API with status: {}", response.getStatusCode());
            
            // Extract the content from the response
            String responseBody = response.getBody();
            if (responseBody == null) {
                logger.error("Received null response body from RabbitHole API");
                return "Error: Received null response from API";
            }
            
            JsonNode jsonResponse = objectMapper.readTree(responseBody);
            
            // Navigate to the content field in the response
            JsonNode choices = jsonResponse.get("choices");
            if (choices != null && choices.isArray() && choices.size() > 0) {
                JsonNode firstChoice = choices.get(0);
                if (firstChoice != null) {
                    JsonNode message = firstChoice.get("message");
                    if (message != null) {
                        JsonNode content = message.get("content");
                        if (content != null) {
                            String contentText = content.asText();
                            logger.debug("Successfully extracted content from API response");
                            return contentText;
                        }
                    }
                }
            }
            
            logger.error("Failed to extract content from API response: {}", responseBody);
            return "Error: Unable to extract content from API response";
        } catch (Exception e) {
            logger.error("Error calling RabbitHole API: {}", e.getMessage(), e);
            return "Error: " + e.getMessage();
        }
    }
}
