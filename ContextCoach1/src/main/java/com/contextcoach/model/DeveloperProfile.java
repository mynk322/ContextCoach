package com.contextcoach.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "developer_profiles")
public class DeveloperProfile {
    @Id
    private String id;

    private String name;

    private String experienceLevel;

    private Double productivityFactor;

    private List<String> skills;

    private Double preferredWorkHoursPerDay;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public Double getProductivityFactor() {
        return productivityFactor;
    }

    public void setProductivityFactor(Double productivityFactor) {
        this.productivityFactor = productivityFactor;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public Double getPreferredWorkHoursPerDay() {
        return preferredWorkHoursPerDay;
    }

    public void setPreferredWorkHoursPerDay(Double preferredWorkHoursPerDay) {
        this.preferredWorkHoursPerDay = preferredWorkHoursPerDay;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
