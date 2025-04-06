package com.contextcoach.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.contextcoach.model.JiraTicket;

@Repository
public interface JiraTicketRepository extends MongoRepository<JiraTicket, String> {
    // Find tickets by title containing the given text (case-insensitive)
    List<JiraTicket> findByTitleContainingIgnoreCase(String title);
    
    // Find tickets by requirement ID
    List<JiraTicket> findByRequirementId(String requirementId);
    
    // Find tickets by assigned developer ID
    List<JiraTicket> findByAssignedDeveloperId(String developerId);
    
    // Find tickets by ticket type
    List<JiraTicket> findByTicketType(String ticketType);
    
    // Find tickets by priority
    List<JiraTicket> findByPriority(String priority);
    
    // Find tickets by external ticket ID
    Optional<JiraTicket> findByExternalTicketId(String externalTicketId);
    
    // Find tickets with story points less than or equal to the given value
    List<JiraTicket> findByEstimatedStoryPointsLessThanEqual(Integer storyPoints);
}
