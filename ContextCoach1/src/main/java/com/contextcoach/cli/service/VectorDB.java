package com.contextcoach.cli.service;

import java.util.List;

/**
 * Interface for vector database search operations on code embeddings.
 */
public interface VectorDB {
    /**
     * Searches the vector database for code relevant to the given query.
     * @param query The search query (e.g., feature description or keywords).
     * @param topK The maximum number of results to return.
     * @return A list of relevant code snippet strings (or identifiers) from the repository.
     */
    List<String> search(String query, int topK);
}
