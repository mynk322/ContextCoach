package com.contextcoach.cli.service;

import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Phase 1: FeatureClarifier interacts with the user to refine the feature description.
 */
public class FeatureClarifier {
    
    private static final Logger logger = LoggerFactory.getLogger(FeatureClarifier.class);
    
    private final VectorDB vectorDB;
    private final LLMService llmService;

    public FeatureClarifier(VectorDB vectorDB, LLMService llmService) {
        this.vectorDB = vectorDB;
        this.llmService = llmService;
        logger.info("FeatureClarifier initialized");
    }

    /**
     * Clarifies the feature description by identifying ambiguities and asking the user for details.
     * @param featureDescription The initial feature request description provided by the user.
     * @return A clarified feature description with ambiguities resolved.
     */
    public String clarifyFeature(String featureDescription) {
        logger.info("Starting feature clarification process");
        Scanner scanner = new Scanner(System.in);
        String clarifiedDesc = featureDescription;
        
        // Loop to iteratively resolve ambiguities (limited to a few rounds to avoid infinite loops).
        for (int round = 1; round <= 5; round++) {
            logger.debug("Clarification round {}", round);
            
            // Retrieve code context related to the current description
            List<String> contextSnippets = vectorDB.search(clarifiedDesc, 5);
            logger.debug("Retrieved {} code snippets for context", contextSnippets.size());

            // Prepare a prompt for the LLM to identify ambiguities or missing details.
            String prompt = "Identify ambiguities in the following feature request:\n\""
                          + clarifiedDesc + "\"\n"
                          + "Context:\n" + String.join("\n", contextSnippets) + "\n"
                          + "If there are ambiguities, ask a clarifying question. If none, respond 'None'.";
            
            logger.debug("Sending prompt to LLM to identify ambiguities");
            String llmResponse = llmService.askLLM(prompt);
            
            if (llmResponse == null || llmResponse.trim().isEmpty() || 
                llmResponse.toLowerCase().contains("none")) {
                // No ambiguities found by the LLM, exit the clarification loop.
                logger.info("No ambiguities found, ending clarification process");
                break;
            }

            // The LLM returned a clarifying question for the user.
            System.out.println(">> LLM identifies an ambiguity:");
            System.out.println(llmResponse);
            System.out.print("Your answer: ");
            String userAnswer = scanner.nextLine().trim();

            if (userAnswer.isEmpty()) {
                // If the user didn't provide an answer, break out to avoid endless loop.
                logger.info("No clarification provided, ending clarification process");
                System.out.println("No clarification provided. Using current description as-is.");
                break;
            }

            // Incorporate the user's answer into the feature description.
            clarifiedDesc += "\nClarification: " + userAnswer;
            logger.debug("Added clarification to feature description");
        }
        
        logger.info("Feature clarification process completed");
        return clarifiedDesc;
    }
}
