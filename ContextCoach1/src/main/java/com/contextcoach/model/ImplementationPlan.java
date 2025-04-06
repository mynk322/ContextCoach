package com.contextcoach.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "implementation_plans")
public class ImplementationPlan {
    @Id
    private String id;

    @DBRef
    private Requirement requirement;

    private String summary;

    private List<String> implementationSteps;

    private String technicalApproach;

    private String dependencies;

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

    public Requirement getRequirement() {
        return requirement;
    }

    public void setRequirement(Requirement requirement) {
        this.requirement = requirement;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getImplementationSteps() {
        return implementationSteps;
    }

    public void setImplementationSteps(List<String> implementationSteps) {
        this.implementationSteps = implementationSteps;
    }

    public String getTechnicalApproach() {
        return technicalApproach;
    }

    public void setTechnicalApproach(String technicalApproach) {
        this.technicalApproach = technicalApproach;
    }

    public String getDependencies() {
        return dependencies;
    }

    public void setDependencies(String dependencies) {
        this.dependencies = dependencies;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
