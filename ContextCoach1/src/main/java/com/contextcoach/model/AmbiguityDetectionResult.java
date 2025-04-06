package com.contextcoach.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ambiguity_detection_results")
public class AmbiguityDetectionResult {
    @Id
    private String id;

    @DBRef
    private Requirement requirement;

    private List<String> ambiguityCategories;

    private String analysis;

    private Double confidenceScore;

    private String suggestedImprovements;

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

    public List<String> getAmbiguityCategories() {
        return ambiguityCategories;
    }

    public void setAmbiguityCategories(List<String> ambiguityCategories) {
        this.ambiguityCategories = ambiguityCategories;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public String getSuggestedImprovements() {
        return suggestedImprovements;
    }

    public void setSuggestedImprovements(String suggestedImprovements) {
        this.suggestedImprovements = suggestedImprovements;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
