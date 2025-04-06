package com.contextcoach.cli.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FeatureClarifierTest {

    private FeatureClarifier featureClarifier;
    private LLMService mockLLMService;
    private VectorDB mockVectorDB;
    private final InputStream originalSystemIn = System.in;

    @BeforeEach
    void setUp() {
        mockLLMService = mock(LLMService.class);
        mockVectorDB = mock(VectorDB.class);
        featureClarifier = new FeatureClarifier(mockVectorDB, mockLLMService);
    }

    @AfterEach
    void tearDown() {
        // Restore original System.in
        System.setIn(originalSystemIn);
    }

    @Test
    void testClarifyFeature_NoAmbiguities_ReturnsOriginalDescription() {
        // Arrange
        String featureDescription = "Add user authentication feature";
        List<String> codeSnippets = Arrays.asList(
            "public class UserService { /* ... */ }",
            "public interface UserRepository { /* ... */ }"
        );
        
        when(mockVectorDB.search(anyString(), anyInt())).thenReturn(codeSnippets);
        when(mockLLMService.askLLM(anyString())).thenReturn("None");
        
        // Act
        String result = featureClarifier.clarifyFeature(featureDescription);
        
        // Assert
        assertEquals(featureDescription, result);
        
        // Verify vector DB was called
        verify(mockVectorDB).search(featureDescription, 5);
        
        // Verify LLM was called with a prompt containing the feature description
        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockLLMService).askLLM(promptCaptor.capture());
        
        String capturedPrompt = promptCaptor.getValue();
        assertTrue(capturedPrompt.contains(featureDescription));
        assertTrue(capturedPrompt.contains("Identify ambiguities"));
    }

    @Test
    void testClarifyFeature_WithAmbiguities_ReturnsClarifiedDescription() {
        // Arrange
        String featureDescription = "Add user authentication feature";
        String userInput = "Admin users\n";
        ByteArrayInputStream testIn = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(testIn);
        
        List<String> codeSnippets = Arrays.asList(
            "public class UserService { /* ... */ }",
            "public interface UserRepository { /* ... */ }"
        );
        
        when(mockVectorDB.search(anyString(), anyInt())).thenReturn(codeSnippets);
        // First call returns a question, second call returns "None"
        when(mockLLMService.askLLM(anyString()))
            .thenReturn("Which user role is this feature for?")
            .thenReturn("None");
        
        // Act
        String result = featureClarifier.clarifyFeature(featureDescription);
        
        // Assert
        String expectedClarifiedDesc = featureDescription + "\nClarification: Admin users";
        assertEquals(expectedClarifiedDesc, result);
        
        // Verify vector DB was called twice
        verify(mockVectorDB, times(2)).search(anyString(), anyInt());
        
        // Verify LLM was called twice
        verify(mockLLMService, times(2)).askLLM(anyString());
        
        // Verify second LLM call contains the clarification
        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockLLMService, times(2)).askLLM(promptCaptor.capture());
        
        List<String> capturedPrompts = promptCaptor.getAllValues();
        assertTrue(capturedPrompts.get(1).contains("Clarification: Admin users"));
    }

    @Test
    void testClarifyFeature_WithMultipleAmbiguities_ReturnsClarifiedDescription() {
        // Arrange
        String featureDescription = "Add user authentication feature";
        String userInput = "Admin users\nEmail and password\n";
        ByteArrayInputStream testIn = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(testIn);
        
        List<String> codeSnippets = Arrays.asList(
            "public class UserService { /* ... */ }",
            "public interface UserRepository { /* ... */ }"
        );
        
        when(mockVectorDB.search(anyString(), anyInt())).thenReturn(codeSnippets);
        // First call returns a question about user roles
        // Second call returns a question about authentication method
        // Third call returns "None"
        when(mockLLMService.askLLM(anyString()))
            .thenReturn("Which user role is this feature for?")
            .thenReturn("What authentication method should be used?")
            .thenReturn("None");
        
        // Act
        String result = featureClarifier.clarifyFeature(featureDescription);
        
        // Assert
        String expectedClarifiedDesc = featureDescription + 
                                      "\nClarification: Admin users" +
                                      "\nClarification: Email and password";
        assertEquals(expectedClarifiedDesc, result);
        
        // Verify vector DB was called three times
        verify(mockVectorDB, times(3)).search(anyString(), anyInt());
        
        // Verify LLM was called three times
        verify(mockLLMService, times(3)).askLLM(anyString());
        
        // Verify third LLM call contains both clarifications
        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockLLMService, times(3)).askLLM(promptCaptor.capture());
        
        List<String> capturedPrompts = promptCaptor.getAllValues();
        assertTrue(capturedPrompts.get(2).contains("Clarification: Admin users"));
        assertTrue(capturedPrompts.get(2).contains("Clarification: Email and password"));
    }

    @Test
    void testClarifyFeature_WithEmptyUserInput_StopsAskingQuestions() {
        // Arrange
        String featureDescription = "Add user authentication feature";
        String userInput = "\n"; // Empty input
        ByteArrayInputStream testIn = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(testIn);
        
        List<String> codeSnippets = Arrays.asList(
            "public class UserService { /* ... */ }",
            "public interface UserRepository { /* ... */ }"
        );
        
        when(mockVectorDB.search(anyString(), anyInt())).thenReturn(codeSnippets);
        when(mockLLMService.askLLM(anyString())).thenReturn("Which user role is this feature for?");
        
        // Act
        String result = featureClarifier.clarifyFeature(featureDescription);
        
        // Assert
        assertEquals(featureDescription, result);
        
        // Verify LLM was called only once
        verify(mockLLMService, times(1)).askLLM(anyString());
    }
}
