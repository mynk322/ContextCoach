package com.contextcoach.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "jira_tickets")
public class JiraTicket {
    @Id
    private String id;

    private String title;

    private String description;

    private String ticketType; // Bug, Feature, Task, etc.

    private String priority; // High, Medium, Low

    private Integer estimatedStoryPoints;

    @DBRef
    private Requirement requirement;

    @DBRef
    private DeveloperProfile assignedDeveloper;

    private String externalTicketId;

    private LocalDateTime createdAt;

    // Method called before saving the document to MongoDB
    public void preSave() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Integer getEstimatedStoryPoints() {
        return estimatedStoryPoints;
    }

    public void setEstimatedStoryPoints(Integer estimatedStoryPoints) {
        this.estimatedStoryPoints = estimatedStoryPoints;
    }

    public Requirement getRequirement() {
        return requirement;
    }

    public void setRequirement(Requirement requirement) {
        this.requirement = requirement;
    }

    public DeveloperProfile getAssignedDeveloper() {
        return assignedDeveloper;
    }

    public void setAssignedDeveloper(DeveloperProfile assignedDeveloper) {
        this.assignedDeveloper = assignedDeveloper;
    }

    public String getExternalTicketId() {
        return externalTicketId;
    }

    public void setExternalTicketId(String externalTicketId) {
        this.externalTicketId = externalTicketId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
