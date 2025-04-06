package com.contextcoach.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.contextcoach.model.AmbiguityDetectionResult;
import com.contextcoach.model.ImplementationPlan;
import com.contextcoach.model.Requirement;
import com.contextcoach.model.ScopeEstimationResult;
import com.contextcoach.repository.AmbiguityDetectionResultRepository;
import com.contextcoach.repository.DeveloperProfileRepository;
import com.contextcoach.repository.ImplementationPlanRepository;
import com.contextcoach.repository.RequirementRepository;
import com.contextcoach.repository.ScopeEstimationResultRepository;

class RequirementServiceTest {

    @Mock
    private RequirementRepository requirementRepository;

    @Mock
    private AmbiguityDetectionResultRepository ambiguityResultRepository;

    @Mock
    private ScopeEstimationResultRepository scopeResultRepository;

    @Mock
    private ImplementationPlanRepository implementationPlanRepository;
    
    @Mock
    private DeveloperProfileRepository developerProfileRepository;

    @Mock
    private FileService fileService;

    @Mock
    private RabbitHoleService rabbitHoleService;

    @InjectMocks
    private RequirementService requirementService;

    private Requirement testRequirement;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Create a test requirement
        testRequirement = new Requirement();
        testRequirement.setId("1");
        testRequirement.setTitle("Test Requirement");
        testRequirement.setContent("This is a test requirement content.");
        testRequirement.setSourceType("TEXT");
        
        // Mock repository behavior
        when(requirementRepository.findById("1")).thenReturn(Optional.of(testRequirement));
        when(requirementRepository.save(testRequirement)).thenReturn(testRequirement);
        
        // Mock ambiguity detection result
        Map<String, Object> ambiguityResult = new HashMap<>();
        ambiguityResult.put("ambiguityCategories", List.of("Vague terms", "Missing constraints"));
        ambiguityResult.put("analysis", "Test analysis");
        ambiguityResult.put("confidenceScore", 0.85);
        ambiguityResult.put("suggestedImprovements", "Test improvements");
        when(rabbitHoleService.detectAmbiguities(anyString())).thenReturn(ambiguityResult);
        
        // Mock scope estimation result
        Map<String, Object> scopeResult = new HashMap<>();
        scopeResult.put("estimatedHours", 24.0);
        scopeResult.put("complexityLevel", "Medium");
        scopeResult.put("confidenceLevel", 0.75);
        scopeResult.put("justification", "Test justification");
        scopeResult.put("riskFactors", "Test risk factors");
        when(rabbitHoleService.estimateScope(anyString())).thenReturn(scopeResult);
        
        // Mock implementation plan result
        Map<String, Object> planResult = new HashMap<>();
        planResult.put("summary", "Test summary");
        planResult.put("implementationSteps", List.of("Step 1", "Step 2"));
        planResult.put("technicalApproach", "Test approach");
        planResult.put("dependencies", "Test dependencies");
        when(rabbitHoleService.generateImplementationPlan(anyString())).thenReturn(planResult);
        
        // Mock repository save behavior
        when(ambiguityResultRepository.save(org.mockito.ArgumentMatchers.any(AmbiguityDetectionResult.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(scopeResultRepository.save(org.mockito.ArgumentMatchers.any(ScopeEstimationResult.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(implementationPlanRepository.save(org.mockito.ArgumentMatchers.any(ImplementationPlan.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void testAnalyzeRequirement() {
        AmbiguityDetectionResult result = requirementService.analyzeRequirement("1");
        
        assertNotNull(result);
        assertEquals(testRequirement, result.getRequirement());
        assertEquals(2, result.getAmbiguityCategories().size());
        assertEquals("Test analysis", result.getAnalysis());
        assertEquals(0.85, result.getConfidenceScore());
        assertEquals("Test improvements", result.getSuggestedImprovements());
    }

    @Test
    void testEstimateScope() {
        ScopeEstimationResult result = requirementService.estimateScope("1");
        
        assertNotNull(result);
        assertEquals(testRequirement, result.getRequirement());
        assertEquals(24.0, result.getEstimatedHours());
        assertEquals("Medium", result.getComplexityLevel());
        assertEquals(0.75, result.getConfidenceLevel());
        assertEquals("Test justification", result.getJustification());
        assertEquals("Test risk factors", result.getRiskFactors());
    }

    @Test
    void testGenerateImplementationPlan() {
        ImplementationPlan result = requirementService.generateImplementationPlan("1");
        
        assertNotNull(result);
        assertEquals(testRequirement, result.getRequirement());
        assertEquals("Test summary", result.getSummary());
        assertEquals(2, result.getImplementationSteps().size());
        assertEquals("Test approach", result.getTechnicalApproach());
        assertEquals("Test dependencies", result.getDependencies());
    }
    
    @Test
    void testCalculateStoryPoints() {
        // Mock story points result
        Map<String, Object> storyPointsResult = new HashMap<>();
        storyPointsResult.put("storyPoints", 5);
        storyPointsResult.put("complexity", "Medium");
        storyPointsResult.put("confidenceLevel", 0.8);
        storyPointsResult.put("justification", "Test justification");
        storyPointsResult.put("considerations", List.of("Factor 1", "Factor 2"));
        when(rabbitHoleService.calculateStoryPoints(anyString(), isNull())).thenReturn(storyPointsResult);
        
        // Call the method without repository complexity
        Map<String, Object> result = requirementService.calculateStoryPoints("1");
        
        // Verify the result
        assertNotNull(result);
        assertEquals(5, result.get("storyPoints"));
        assertEquals("Medium", result.get("complexity"));
        assertEquals(0.8, result.get("confidenceLevel"));
        assertEquals("Test justification", result.get("justification"));
        assertEquals(List.of("Factor 1", "Factor 2"), result.get("considerations"));
    }
    
    @Test
    void testCalculateStoryPointsWithRepositoryComplexity() {
        // Mock story points result with repository complexity
        Double repositoryComplexity = 0.75;
        Map<String, Object> storyPointsResult = new HashMap<>();
        storyPointsResult.put("storyPoints", 8);
        storyPointsResult.put("complexity", "High");
        storyPointsResult.put("confidenceLevel", 0.9);
        storyPointsResult.put("justification", "Test justification with repository complexity");
        storyPointsResult.put("considerations", List.of("Repository complexity", "Factor 2"));
        when(rabbitHoleService.calculateStoryPoints(anyString(), eq(repositoryComplexity))).thenReturn(storyPointsResult);
        
        // Call the method with repository complexity
        Map<String, Object> result = requirementService.calculateStoryPoints("1", repositoryComplexity);
        
        // Verify the result
        assertNotNull(result);
        assertEquals(8, result.get("storyPoints"));
        assertEquals("High", result.get("complexity"));
        assertEquals(0.9, result.get("confidenceLevel"));
        assertEquals("Test justification with repository complexity", result.get("justification"));
        assertEquals(List.of("Repository complexity", "Factor 2"), result.get("considerations"));
    }

    @Test
    void testDetermineSourceType() {
        // Test private method through public methods
        assertEquals("TEXT", testRequirement.getSourceType());
    }
}
