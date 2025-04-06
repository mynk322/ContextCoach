package com.contextcoach.cli.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Phase 2: ComplexityAnalyzer uses code context and LLM reasoning to assess implementation effort.
 */
public class ComplexityAnalyzer {
    
    private static final Logger logger = LoggerFactory.getLogger(ComplexityAnalyzer.class);
    
    private final VectorDB vectorDB;
    private final LLMService llmService;

    public ComplexityAnalyzer(VectorDB vectorDB, LLMService llmService) {
        this.vectorDB = vectorDB;
        this.llmService = llmService;
        logger.info("ComplexityAnalyzer initialized");
    }

    /**
     * Analyzes the clarified feature description to evaluate implementation complexity and details.
     * @param featureDescription The clarified feature request from Phase 1.
     * @return A JSON string representing the complexity analysis report.
     */
    public String analyzeFeature(String featureDescription) {
        logger.info("Starting feature complexity analysis");
        
        // Retrieve relevant code snippets from the repository as context for the LLM.
        List<String> relatedCode = vectorDB.search(featureDescription, 5);
        logger.debug("Retrieved {} code snippets for context", relatedCode.size());

        // Create a prompt for the LLM to analyze complexity, story points, impacted areas, etc.
        String prompt = "Analyze the following feature request and the given code context:\n\""
                      + featureDescription + "\"\n"
                      + "Context:\n" + String.join("\n", relatedCode) + "\n"
                      + "Assess the implementation complexity (low/medium/high), estimate story points, "
                      + "list affected modules/files/classes, break down into subtasks, suggest any refactors, "
                      + "and highlight potential edge cases or risks. Provide the answer in JSON format with keys: "
                      + "complexity, storyPoints, affectedModules, subtasks, refactors, risks.";
        
        logger.debug("Sending prompt to LLM for complexity analysis");
        String analysisJson = llmService.askLLM(prompt);
        
        // In a real implementation, you might want to parse or validate the JSON.
        // Here we assume the LLM returns a well-formed JSON string.
        logger.info("Complexity analysis completed");
        return analysisJson;
    }
}
