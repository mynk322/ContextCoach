package com.contextcoach.cli.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DummyVectorDBTest {

    private DummyVectorDB vectorDB;

    @BeforeEach
    void setUp() {
        vectorDB = new DummyVectorDB();
    }

    @Test
    void testSearch_WithQuery_ReturnsCodeSnippets() {
        // Arrange
        String query = "user authentication";
        int topK = 3;
        
        // Act
        List<String> results = vectorDB.search(query, topK);
        
        // Assert
        assertNotNull(results);
        assertEquals(3, results.size());
        
        // Verify each result contains the query
        for (String snippet : results) {
            assertNotNull(snippet);
            assertFalse(snippet.isEmpty());
            assertTrue(snippet.contains(query));
        }
    }
    
    @Test
    void testSearch_WithLimitedResults_ReturnsCorrectNumberOfSnippets() {
        // Arrange
        String query = "user authentication";
        int topK = 1;
        
        // Act
        List<String> results = vectorDB.search(query, topK);
        
        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
    }
    
    @Test
    void testSearch_WithZeroResults_ReturnsEmptyList() {
        // Arrange
        String query = "user authentication";
        int topK = 0;
        
        // Act
        List<String> results = vectorDB.search(query, topK);
        
        // Assert
        assertNotNull(results);
        assertEquals(0, results.size());
    }
    
    @Test
    void testSearch_WithDifferentQueries_ReturnsQuerySpecificResults() {
        // Arrange
        String query1 = "user authentication";
        String query2 = "payment processing";
        int topK = 1;
        
        // Act
        List<String> results1 = vectorDB.search(query1, topK);
        List<String> results2 = vectorDB.search(query2, topK);
        
        // Assert
        assertNotNull(results1);
        assertNotNull(results2);
        assertEquals(1, results1.size());
        assertEquals(1, results2.size());
        
        assertTrue(results1.get(0).contains(query1));
        assertTrue(results2.get(0).contains(query2));
    }
}
