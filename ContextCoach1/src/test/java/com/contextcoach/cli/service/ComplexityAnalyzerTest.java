package com.contextcoach.cli.service;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ComplexityAnalyzerTest {

    private ComplexityAnalyzer complexityAnalyzer;
    private LLMService mockLLMService;
    private VectorDB mockVectorDB;

    @BeforeEach
    void setUp() {
        mockLLMService = mock(LLMService.class);
        mockVectorDB = mock(VectorDB.class);
        complexityAnalyzer = new ComplexityAnalyzer(mockVectorDB, mockLLMService);
    }

    @Test
    void testAnalyzeFeature_ReturnsLLMResponse() {
        // Arrange
        String featureDescription = "Add user authentication feature";
        List<String> codeSnippets = Arrays.asList(
            "public class UserService { /* ... */ }",
            "public interface UserRepository { /* ... */ }"
        );
        String expectedJson = "{\n" +
                              "  \"complexity\": \"Medium\",\n" +
                              "  \"storyPoints\": 5\n" +
                              "}";
        
        when(mockVectorDB.search(anyString(), anyInt())).thenReturn(codeSnippets);
        when(mockLLMService.askLLM(anyString())).thenReturn(expectedJson);
        
        // Act
        String result = complexityAnalyzer.analyzeFeature(featureDescription);
        
        // Assert
        assertNotNull(result);
        assertEquals(expectedJson, result);
        
        // Verify vector DB was called with correct parameters
        verify(mockVectorDB).search(featureDescription, 5);
        
        // Verify LLM was called with a prompt containing the feature description and code snippets
        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockLLMService).askLLM(promptCaptor.capture());
        
        String capturedPrompt = promptCaptor.getValue();
        assertTrue(capturedPrompt.contains(featureDescription));
        for (String snippet : codeSnippets) {
            assertTrue(capturedPrompt.contains(snippet));
        }
        assertTrue(capturedPrompt.contains("complexity"));
        assertTrue(capturedPrompt.contains("storyPoints"));
        assertTrue(capturedPrompt.contains("affectedModules"));
        assertTrue(capturedPrompt.contains("subtasks"));
        assertTrue(capturedPrompt.contains("refactors"));
        assertTrue(capturedPrompt.contains("risks"));
    }
}
