package com.contextcoach.cli.service;

/**
 * Interface for LLM interactions.
 * Abstracts away calls to an external LLM API.
 */
public interface LLMService {
    /**
     * Sends a prompt to the LLM and returns the response as a String.
     */
    String askLLM(String prompt);
}
