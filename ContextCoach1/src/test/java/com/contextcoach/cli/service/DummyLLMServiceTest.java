package com.contextcoach.cli.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DummyLLMServiceTest {

    private DummyLLMService llmService;

    @BeforeEach
    void setUp() {
        llmService = new DummyLLMService();
    }

    @Test
    void testAskLLM_WithAmbiguityPrompt_ReturnsQuestion() {
        // Arrange
        String prompt = "Identify ambiguities in the following feature request:\"Add user authentication\"";
        
        // Act
        String response = llmService.askLLM(prompt);
        
        // Assert
        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertEquals("Which user role is this feature for?", response);
    }

    @Test
    void testAskLLM_WithAmbiguityPromptAfterClarification_ReturnsNone() {
        // Arrange
        String prompt = "Identify ambiguities in the following feature request:\"Add user authentication\nClarification: Admin users\"";
        
        // Act
        String response = llmService.askLLM(prompt);
        
        // Assert
        assertNotNull(response);
        assertEquals("None", response);
    }

    @Test
    void testAskLLM_WithComplexityAnalysisPrompt_ReturnsJsonResponse() {
        // Arrange
        String prompt = "Analyze the following feature request and the given code context:\"Add user authentication\"";
        
        // Act
        String response = llmService.askLLM(prompt);
        
        // Assert
        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertTrue(response.contains("complexity"));
        assertTrue(response.contains("storyPoints"));
        assertTrue(response.contains("affectedModules"));
        assertTrue(response.contains("subtasks"));
        assertTrue(response.contains("refactors"));
        assertTrue(response.contains("risks"));
    }

    @Test
    void testAskLLM_WithUnknownPrompt_ReturnsDefaultResponse() {
        // Arrange
        String prompt = "This is an unknown prompt";
        
        // Act
        String response = llmService.askLLM(prompt);
        
        // Assert
        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertEquals("I don't have a specific response for this prompt.", response);
    }
}
