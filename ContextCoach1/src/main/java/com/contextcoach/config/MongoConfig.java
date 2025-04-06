package com.contextcoach.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;

import com.contextcoach.model.AmbiguityDetectionResult;
import com.contextcoach.model.DeveloperProfile;
import com.contextcoach.model.ImplementationPlan;
import com.contextcoach.model.JiraTicket;
import com.contextcoach.model.Requirement;
import com.contextcoach.model.ScopeEstimationResult;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Override
    protected String getDatabaseName() {
        return "contextcoachdb";
    }

    @Override
    public MongoClient mongoClient() {
        return MongoClients.create("mongodb://localhost:27017");
    }

    @Override
    protected boolean autoIndexCreation() {
        return true;
    }
    
    /**
     * Automatically calls preSave on Requirement entities before they are saved to MongoDB
     */
    @Bean
    public BeforeConvertCallback<Requirement> requirementBeforeConvertCallback() {
        return (entity, collection) -> {
            entity.preSave();
            return entity;
        };
    }
    
    /**
     * Automatically calls preSave on DeveloperProfile entities before they are saved to MongoDB
     */
    @Bean
    public BeforeConvertCallback<DeveloperProfile> developerProfileBeforeConvertCallback() {
        return (entity, collection) -> {
            entity.preSave();
            return entity;
        };
    }
    
    /**
     * Automatically calls preSave on JiraTicket entities before they are saved to MongoDB
     */
    @Bean
    public BeforeConvertCallback<JiraTicket> jiraTicketBeforeConvertCallback() {
        return (entity, collection) -> {
            entity.preSave();
            return entity;
        };
    }
    
    /**
     * Automatically calls preSave on AmbiguityDetectionResult entities before they are saved to MongoDB
     */
    @Bean
    public BeforeConvertCallback<AmbiguityDetectionResult> ambiguityDetectionResultBeforeConvertCallback() {
        return (entity, collection) -> {
            entity.preSave();
            return entity;
        };
    }
    
    /**
     * Automatically calls preSave on ScopeEstimationResult entities before they are saved to MongoDB
     */
    @Bean
    public BeforeConvertCallback<ScopeEstimationResult> scopeEstimationResultBeforeConvertCallback() {
        return (entity, collection) -> {
            entity.preSave();
            return entity;
        };
    }
    
    /**
     * Automatically calls preSave on ImplementationPlan entities before they are saved to MongoDB
     */
    @Bean
    public BeforeConvertCallback<ImplementationPlan> implementationPlanBeforeConvertCallback() {
        return (entity, collection) -> {
            entity.preSave();
            return entity;
        };
    }
}
