package com.contextcoach.cli.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Tests for the PythonVectorDB class.
 * These tests mock the API calls to the Python vector database.
 */
@ExtendWith(MockitoExtension.class)
public class PythonVectorDBTest {
    
    private PythonVectorDB vectorDB;
    
    @Mock
    private RestTemplate restTemplate;
    
    private final String apiUrl = "http://localhost:5000";
    
    @BeforeEach
    public void setUp() {
        // Create a test instance with a mocked RestTemplate
        vectorDB = new PythonVectorDB(apiUrl) {
            @Override
            protected RestTemplate getRestTemplate() {
                return restTemplate;
            }
            
            @Override
            protected boolean checkApiConnection() {
                return true; // Skip API connection check in tests
            }
        };
    }
    
    @Test
    public void testSearch_Success() {
        // Prepare test data
        String query = "user authentication";
        int topK = 3;
        
        // Mock API response
        String mockResponse = "{"
            + "\"results\": ["
            + "  {"
            + "    \"content\": \"public class AuthService { public boolean authenticate(String username, String password) { ... } }\","
            + "    \"path\": \"src/main/java/com/example/AuthService.java\","
            + "    \"score\": 0.85"
            + "  },"
            + "  {"
            + "    \"content\": \"public interface UserRepository { User findByUsername(String username); }\","
            + "    \"path\": \"src/main/java/com/example/UserRepository.java\","
            + "    \"score\": 0.75"
            + "  }"
            + "]"
            + "}";
        
        // Configure mock behavior
        when(restTemplate.postForObject(eq(apiUrl + "/query"), any(HttpEntity.class), eq(String.class)))
            .thenReturn(mockResponse);
        
        // Execute the method under test
        List<String> results = vectorDB.search(query, topK);
        
        // Verify results
        assertNotNull(results);
        assertEquals(2, results.size());
        assertTrue(results.get(0).contains("AuthService"));
        assertTrue(results.get(1).contains("UserRepository"));
    }
    
    @Test
    public void testSearch_EmptyResults() {
        // Prepare test data
        String query = "nonexistent feature";
        int topK = 3;
        
        // Mock API response with empty results
        String mockResponse = "{\"results\": []}";
        
        // Configure mock behavior
        when(restTemplate.postForObject(eq(apiUrl + "/query"), any(HttpEntity.class), eq(String.class)))
            .thenReturn(mockResponse);
        
        // Execute the method under test
        List<String> results = vectorDB.search(query, topK);
        
        // Verify results
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }
    
    @Test
    public void testSearch_ApiError() {
        // Prepare test data
        String query = "error test";
        int topK = 3;
        
        // Configure mock behavior to throw exception
        when(restTemplate.postForObject(eq(apiUrl + "/query"), any(HttpEntity.class), eq(String.class)))
            .thenThrow(new RuntimeException("API error"));
        
        // Execute the method under test
        List<String> results = vectorDB.search(query, topK);
        
        // Verify results - should return empty list on error
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }
}
