package com.contextcoach.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.contextcoach.exception.ResourceNotFoundException;
import com.contextcoach.service.RequirementService;

@ExtendWith(MockitoExtension.class)
class RequirementControllerTest {

    @Mock
    private RequirementService requirementService;

    @InjectMocks
    private RequirementController requirementController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(requirementController).build();
    }

    @Test
    void testCalculateStoryPoints() throws Exception {
        // Mock story points result
        Map<String, Object> storyPointsResult = new HashMap<>();
        storyPointsResult.put("storyPoints", 5);
        storyPointsResult.put("complexity", "Medium");
        storyPointsResult.put("confidenceLevel", 0.8);
        storyPointsResult.put("justification", "Test justification");
        storyPointsResult.put("considerations", List.of("Factor 1", "Factor 2"));
        
        when(requirementService.calculateStoryPoints(eq("1"), eq(null))).thenReturn(storyPointsResult);
        
        // Call the endpoint without repository complexity
        mockMvc.perform(post("/api/requirements/1/story-points")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.storyPoints").value(5))
                .andExpect(jsonPath("$.complexity").value("Medium"))
                .andExpect(jsonPath("$.confidenceLevel").value(0.8))
                .andExpect(jsonPath("$.justification").value("Test justification"))
                .andExpect(jsonPath("$.considerations[0]").value("Factor 1"))
                .andExpect(jsonPath("$.considerations[1]").value("Factor 2"));
    }
    
    @Test
    void testCalculateStoryPointsWithRepositoryComplexity() throws Exception {
        // Mock story points result with repository complexity
        Double repositoryComplexity = 0.75;
        Map<String, Object> storyPointsResult = new HashMap<>();
        storyPointsResult.put("storyPoints", 8);
        storyPointsResult.put("complexity", "High");
        storyPointsResult.put("confidenceLevel", 0.9);
        storyPointsResult.put("justification", "Test justification with repository complexity");
        storyPointsResult.put("considerations", List.of("Repository complexity", "Factor 2"));
        
        when(requirementService.calculateStoryPoints(eq("1"), eq(repositoryComplexity))).thenReturn(storyPointsResult);
        
        // Call the endpoint with repository complexity
        mockMvc.perform(post("/api/requirements/1/story-points")
                .param("repositoryComplexity", "0.75")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.storyPoints").value(8))
                .andExpect(jsonPath("$.complexity").value("High"))
                .andExpect(jsonPath("$.confidenceLevel").value(0.9))
                .andExpect(jsonPath("$.justification").value("Test justification with repository complexity"))
                .andExpect(jsonPath("$.considerations[0]").value("Repository complexity"))
                .andExpect(jsonPath("$.considerations[1]").value("Factor 2"));
    }
    
    @Test
    void testCalculateStoryPointsRequirementNotFound() throws Exception {
        when(requirementService.calculateStoryPoints(anyString(), any())).thenThrow(new ResourceNotFoundException("Requirement not found"));
        
        mockMvc.perform(post("/api/requirements/999/story-points")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
