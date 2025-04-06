package com.contextcoach.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.contextcoach.model.JiraTicket;
import com.contextcoach.service.JiraService;

@RestController
@RequestMapping("/api/jira")
public class JiraController {

    private static final Logger logger = LoggerFactory.getLogger(JiraController.class);
    private final JiraService jiraService;

    public JiraController(JiraService jiraService) {
        this.jiraService = jiraService;
        logger.info("JiraController initialized");
    }

    /**
     * Creates a Jira ticket from a requirement
     * 
     * @param requirementId The ID of the requirement
     * @param ticketType The type of ticket (Bug, Feature, Task, etc.)
     * @param priority The priority of the ticket (High, Medium, Low)
     * @param assignedDeveloperId The ID of the assigned developer (optional)
     * @return The created Jira ticket
     */
    @PostMapping("/tickets")
    public ResponseEntity<JiraTicket> createJiraTicket(
            @RequestParam("requirementId") String requirementId,
            @RequestParam("ticketType") String ticketType,
            @RequestParam("priority") String priority,
            @RequestParam(name = "assignedDeveloperId", required = false) String assignedDeveloperId) {
        logger.info("Creating Jira ticket for requirement ID: {}", requirementId);
        try {
            JiraTicket ticket = jiraService.createJiraTicket(
                    requirementId,
                    ticketType,
                    priority,
                    Optional.ofNullable(assignedDeveloperId));
            logger.info("Successfully created Jira ticket with ID: {}", ticket.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(ticket);
        } catch (IllegalArgumentException e) {
            logger.error("Error creating Jira ticket: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Unexpected error creating Jira ticket", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Gets all Jira tickets
     * 
     * @return All tickets
     */
    @GetMapping("/tickets")
    public ResponseEntity<List<JiraTicket>> getAllJiraTickets() {
        logger.info("Getting all Jira tickets");
        try {
            List<JiraTicket> tickets = jiraService.getAllJiraTickets();
            logger.debug("Found {} Jira tickets", tickets.size());
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            logger.error("Error getting all Jira tickets", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Gets a Jira ticket by ID
     * 
     * @param id The ID of the ticket
     * @return The ticket, if found
     */
    @GetMapping("/tickets/{id}")
    public ResponseEntity<JiraTicket> getJiraTicketById(@PathVariable String id) {
        logger.info("Getting Jira ticket by ID: {}", id);
        try {
            return jiraService.getJiraTicketById(id)
                    .map(ticket -> {
                        logger.debug("Found Jira ticket with ID: {}", id);
                        return ResponseEntity.ok(ticket);
                    })
                    .orElseGet(() -> {
                        logger.debug("No Jira ticket found with ID: {}", id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            logger.error("Error getting Jira ticket by ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Gets all Jira tickets for a requirement
     * 
     * @param requirementId The ID of the requirement
     * @return All tickets for the requirement
     */
    @GetMapping("/tickets/requirement/{requirementId}")
    public ResponseEntity<List<JiraTicket>> getJiraTicketsForRequirement(@PathVariable String requirementId) {
        logger.info("Getting Jira tickets for requirement ID: {}", requirementId);
        try {
            List<JiraTicket> tickets = jiraService.getJiraTicketsForRequirement(requirementId);
            logger.debug("Found {} Jira tickets for requirement ID: {}", tickets.size(), requirementId);
            return ResponseEntity.ok(tickets);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid requirement ID: {}", requirementId);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error getting Jira tickets for requirement ID: {}", requirementId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Gets all Jira tickets assigned to a developer
     * 
     * @param developerId The ID of the developer
     * @return All tickets assigned to the developer
     */
    @GetMapping("/tickets/developer/{developerId}")
    public ResponseEntity<List<JiraTicket>> getJiraTicketsForDeveloper(@PathVariable String developerId) {
        logger.info("Getting Jira tickets for developer ID: {}", developerId);
        try {
            List<JiraTicket> tickets = jiraService.getJiraTicketsForDeveloper(developerId);
            logger.debug("Found {} Jira tickets for developer ID: {}", tickets.size(), developerId);
            return ResponseEntity.ok(tickets);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid developer ID: {}", developerId);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error getting Jira tickets for developer ID: {}", developerId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
