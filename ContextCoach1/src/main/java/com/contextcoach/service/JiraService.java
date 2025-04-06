package com.contextcoach.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.contextcoach.model.DeveloperProfile;
import com.contextcoach.model.JiraTicket;
import com.contextcoach.model.Requirement;
import com.contextcoach.repository.DeveloperProfileRepository;
import com.contextcoach.repository.JiraTicketRepository;
import com.contextcoach.repository.RequirementRepository;

@Service
public class JiraService {
    
    private static final Logger logger = LoggerFactory.getLogger(JiraService.class);

    private final JiraTicketRepository jiraTicketRepository;
    private final RequirementRepository requirementRepository;
    private final DeveloperProfileRepository developerProfileRepository;
    private final RequirementService requirementService;
    
    @Value("${jira.api.url:}")
    private String jiraApiUrl;
    
    @Value("${jira.api.username:}")
    private String jiraUsername;
    
    @Value("${jira.api.token:}")
    private String jiraToken;
    
    @Value("${jira.project.key:}")
    private String jiraProjectKey;

    public JiraService(
            JiraTicketRepository jiraTicketRepository,
            RequirementRepository requirementRepository,
            DeveloperProfileRepository developerProfileRepository,
            RequirementService requirementService) {
        this.jiraTicketRepository = jiraTicketRepository;
        this.requirementRepository = requirementRepository;
        this.developerProfileRepository = developerProfileRepository;
        this.requirementService = requirementService;
    }

    /**
     * Creates a Jira ticket from a requirement
     * 
     * @param requirementId The ID of the requirement
     * @param ticketType The type of ticket (Bug, Feature, Task, etc.)
     * @param priority The priority of the ticket (High, Medium, Low)
     * @param assignedDeveloperId The ID of the assigned developer (optional)
     * @return The created Jira ticket
     * @throws IllegalArgumentException if the requirement or developer is not found
     */
    public JiraTicket createJiraTicket(
            String requirementId,
            String ticketType,
            String priority,
            Optional<String> assignedDeveloperId) {
        
        logger.info("Creating Jira ticket for requirement ID: {}", requirementId);
        
        try {
            // Validate inputs
            if (requirementId == null) {
                logger.error("Requirement ID cannot be null");
                throw new IllegalArgumentException("Requirement ID cannot be null");
            }
            
            if (ticketType == null || ticketType.trim().isEmpty()) {
                logger.error("Ticket type cannot be null or empty");
                throw new IllegalArgumentException("Ticket type cannot be null or empty");
            }
            
            if (priority == null || priority.trim().isEmpty()) {
                logger.error("Priority cannot be null or empty");
                throw new IllegalArgumentException("Priority cannot be null or empty");
            }
            
            // Find the requirement
            logger.debug("Finding requirement with ID: {}", requirementId);
            Requirement requirement = requirementRepository.findById(requirementId)
                    .orElseThrow(() -> {
                        logger.error("Requirement not found with ID: {}", requirementId);
                        return new IllegalArgumentException("Requirement not found with ID: " + requirementId);
                    });
            
            // Create the ticket
            logger.debug("Creating Jira ticket for requirement: {}", requirement.getTitle());
            JiraTicket ticket = new JiraTicket();
            ticket.setTitle(requirement.getTitle());
            ticket.setDescription(generateTicketDescription(requirement));
            ticket.setTicketType(ticketType);
            ticket.setPriority(priority);
            ticket.setRequirement(requirement);
            
            // Set assigned developer if provided
            DeveloperProfile developer = null;
            if (assignedDeveloperId.isPresent()) {
                String developerId = assignedDeveloperId.get();
                logger.debug("Finding developer with ID: {}", developerId);
                developer = developerProfileRepository.findById(developerId)
                        .orElseThrow(() -> {
                            logger.error("Developer not found with ID: {}", developerId);
                            return new IllegalArgumentException("Developer not found with ID: " + developerId);
                        });
                ticket.setAssignedDeveloper(developer);
                logger.debug("Assigned ticket to developer: {}", developer.getName());
            } else {
                logger.debug("No developer assigned to the ticket");
            }
            
            // Set estimated story points based on complexity and developer profile
            if (requirement.getClarityScore() != null) {
                logger.debug("Calculating story points based on clarity score: {}", requirement.getClarityScore());
                int storyPoints;
                
                if (developer != null) {
                    // Use developer profile for story point calculation
                    logger.debug("Using developer profile for story point calculation");
                    Map<String, Object> storyPointsResult = requirementService.calculateStoryPointsWithDeveloper(
                            requirementId, developer.getId(), null);
                    storyPoints = ((Number) storyPointsResult.get("storyPoints")).intValue();
                    logger.debug("Calculated story points with developer profile: {}", storyPoints);
                } else {
                    // Use standard calculation without developer profile
                    storyPoints = calculateStoryPoints(requirement);
                    logger.debug("Calculated story points without developer profile: {}", storyPoints);
                }
                
                ticket.setEstimatedStoryPoints(storyPoints);
                logger.debug("Set story points to: {}", storyPoints);
            } else {
                logger.debug("No clarity score available, skipping story point calculation");
            }
            
            // Create the ticket in Jira if API credentials are provided
            if (!jiraApiUrl.isEmpty() && !jiraUsername.isEmpty() && !jiraToken.isEmpty()) {
                logger.debug("Creating ticket in external Jira system");
                String externalTicketId = createTicketInJira(ticket);
                ticket.setExternalTicketId(externalTicketId);
                logger.debug("Created external ticket with ID: {}", externalTicketId);
            } else {
                logger.debug("Skipping external Jira integration - API credentials not configured");
            }
            
            // Save the ticket
            logger.debug("Saving Jira ticket to database");
            JiraTicket savedTicket = jiraTicketRepository.save(ticket);
            logger.info("Successfully created Jira ticket with ID: {}", savedTicket.getId());
            return savedTicket;
        } catch (IllegalArgumentException e) {
            // Re-throw these as they're already logged
            throw e;
        } catch (Exception e) {
            logger.error("Error creating Jira ticket for requirement ID: {}", requirementId, e);
            throw new RuntimeException("Error creating Jira ticket: " + e.getMessage(), e);
        }
    }

    /**
     * Gets a Jira ticket by ID
     * 
     * @param id The ID of the ticket
     * @return The ticket, if found
     */
    public Optional<JiraTicket> getJiraTicketById(String id) {
        logger.debug("Getting Jira ticket by ID: {}", id);
        try {
            Optional<JiraTicket> ticket = jiraTicketRepository.findById(id);
            if (ticket.isPresent()) {
                logger.debug("Found Jira ticket with ID: {}", id);
            } else {
                logger.debug("No Jira ticket found with ID: {}", id);
            }
            return ticket;
        } catch (Exception e) {
            logger.error("Error getting Jira ticket by ID: {}", id, e);
            throw new RuntimeException("Error getting Jira ticket: " + e.getMessage(), e);
        }
    }

    /**
     * Gets all Jira tickets
     * 
     * @return All tickets
     */
    public List<JiraTicket> getAllJiraTickets() {
        logger.debug("Getting all Jira tickets");
        try {
            List<JiraTicket> tickets = jiraTicketRepository.findAll();
            logger.debug("Found {} Jira tickets", tickets.size());
            return tickets;
        } catch (Exception e) {
            logger.error("Error getting all Jira tickets", e);
            throw new RuntimeException("Error getting all Jira tickets: " + e.getMessage(), e);
        }
    }

    /**
     * Gets all Jira tickets for a requirement
     * 
     * @param requirementId The ID of the requirement
     * @return All tickets for the requirement
     */
    public List<JiraTicket> getJiraTicketsForRequirement(String requirementId) {
        logger.debug("Getting Jira tickets for requirement ID: {}", requirementId);
        try {
            if (requirementId == null) {
                logger.error("Requirement ID cannot be null");
                throw new IllegalArgumentException("Requirement ID cannot be null");
            }
            
            List<JiraTicket> tickets = jiraTicketRepository.findByRequirementId(requirementId);
            logger.debug("Found {} Jira tickets for requirement ID: {}", tickets.size(), requirementId);
            return tickets;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error getting Jira tickets for requirement ID: {}", requirementId, e);
            throw new RuntimeException("Error getting Jira tickets for requirement: " + e.getMessage(), e);
        }
    }

    /**
     * Gets all Jira tickets assigned to a developer
     * 
     * @param developerId The ID of the developer
     * @return All tickets assigned to the developer
     */
    public List<JiraTicket> getJiraTicketsForDeveloper(String developerId) {
        logger.debug("Getting Jira tickets for developer ID: {}", developerId);
        try {
            if (developerId == null) {
                logger.error("Developer ID cannot be null");
                throw new IllegalArgumentException("Developer ID cannot be null");
            }
            
            List<JiraTicket> tickets = jiraTicketRepository.findByAssignedDeveloperId(developerId);
            logger.debug("Found {} Jira tickets for developer ID: {}", tickets.size(), developerId);
            return tickets;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error getting Jira tickets for developer ID: {}", developerId, e);
            throw new RuntimeException("Error getting Jira tickets for developer: " + e.getMessage(), e);
        }
    }

    /**
     * Generates a description for a Jira ticket based on a requirement
     * 
     * @param requirement The requirement
     * @return The generated description
     */
    private String generateTicketDescription(Requirement requirement) {
        logger.trace("Generating ticket description for requirement: {}", requirement.getTitle());
        StringBuilder description = new StringBuilder();
        
        description.append("*Requirement:*\n");
        description.append(requirement.getContent()).append("\n\n");
        
        // Add clarity score if available
        if (requirement.getClarityScore() != null) {
            logger.trace("Adding clarity score to ticket description: {}", requirement.getClarityScore());
            description.append("*Clarity Score:* ").append(requirement.getClarityScore()).append("\n\n");
        }
        
        logger.trace("Generated ticket description with length: {}", description.length());
        return description.toString();
    }

    /**
     * Calculates story points based on requirement complexity
     * 
     * @param requirement The requirement
     * @return The calculated story points
     */
    private int calculateStoryPoints(Requirement requirement) {
        logger.trace("Calculating story points for requirement: {}", requirement.getTitle());
        // This is a simplified approach - in a real application, you would use a more sophisticated algorithm
        double clarityScore = requirement.getClarityScore();
        logger.trace("Using clarity score for story point calculation: {}", clarityScore);
        
        int storyPoints;
        // Lower clarity means higher complexity and more story points
        if (clarityScore < 0.3) {
            storyPoints = 13; // Very complex
            logger.trace("Assigned 13 story points (Very complex)");
        } else if (clarityScore < 0.5) {
            storyPoints = 8; // Complex
            logger.trace("Assigned 8 story points (Complex)");
        } else if (clarityScore < 0.7) {
            storyPoints = 5; // Moderate
            logger.trace("Assigned 5 story points (Moderate)");
        } else if (clarityScore < 0.9) {
            storyPoints = 3; // Simple
            logger.trace("Assigned 3 story points (Simple)");
        } else {
            storyPoints = 1; // Very simple
            logger.trace("Assigned 1 story point (Very simple)");
        }
        
        return storyPoints;
    }

    /**
     * Creates a ticket in Jira
     * 
     * @param ticket The ticket to create
     * @return The external ticket ID
     */
    private String createTicketInJira(JiraTicket ticket) {
        logger.debug("Creating ticket in external Jira system: {}", ticket.getTitle());
        try {
            // In a real application, you would use the Jira REST API to create the ticket
            // For simplicity, we're returning a mock ticket ID
            String mockId = "MOCK-" + System.currentTimeMillis();
            logger.debug("Created mock Jira ticket with ID: {}", mockId);
            return mockId;
        } catch (Exception e) {
            logger.error("Error creating ticket in external Jira system: {}", ticket.getTitle(), e);
            throw new RuntimeException("Error creating ticket in Jira: " + e.getMessage(), e);
        }
    }
}
