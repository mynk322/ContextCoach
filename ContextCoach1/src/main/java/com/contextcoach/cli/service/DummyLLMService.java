package com.contextcoach.cli.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dummy implementation of LLMService for simulation purposes.
 * It returns hard-coded responses for specific prompts to mimic LLM behavior.
 */
public class DummyLLMService implements LLMService {
    
    private static final Logger logger = LoggerFactory.getLogger(DummyLLMService.class);
    
    public DummyLLMService() {
        logger.info("DummyLLMService initialized");
    }
    
    @Override
    public String askLLM(String prompt) {
        logger.info("Processing prompt with DummyLLMService");
        String lower = prompt.toLowerCase();
        
        if (lower.contains("identify ambiguities")) {
            // Simulate finding an ambiguity in the feature description.
            // If the description already contains a clarification, assume no more ambiguities.
            if (lower.contains("clarification:")) {
                logger.debug("No further ambiguities found");
                return "None";  // No further ambiguities
            } else {
                logger.debug("Returning a sample clarifying question");
                return "Which user role is this feature for?";  // A sample clarifying question
            }
        } else if (lower.contains("analyze the following feature request")) {
            // Simulate returning a structured JSON analysis for complexity.
            logger.debug("Returning a complexity analysis");
            return "{\n" +
                   "  \"complexity\": \"Medium\",\n" +
                   "  \"storyPoints\": 5,\n" +
                   "  \"affectedModules\": [\"UserModule\", \"AuthService\"],\n" +
                   "  \"subtasks\": [\"Update user schema\", \"Modify login flow\"],\n" +
                   "  \"refactors\": [\"Refactor user service abstraction\"],\n" +
                   "  \"risks\": [\"Potential auth timeout issues\"]\n" +
                   "}";
        } else {
            // Default fallback response
            logger.debug("Returning default response for unrecognized prompt");
            return "I don't have a specific response for this prompt.";
        }
    }
}
