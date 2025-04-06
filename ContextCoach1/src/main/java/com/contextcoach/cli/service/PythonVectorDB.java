package com.contextcoach.cli.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

/**
 * Implementation of VectorDB that uses the Python vector database API.
 */
public class PythonVectorDB implements VectorDB {
    
    private static final Logger logger = LoggerFactory.getLogger(PythonVectorDB.class);
    
    private final String apiUrl;
    private RestTemplate restTemplate;
    
    /**
     * Constructor for PythonVectorDB.
     * 
     * @param apiUrl The URL of the vector database API
     */
    public PythonVectorDB(String apiUrl) {
        this.apiUrl = apiUrl;
        this.restTemplate = new RestTemplate();
        logger.info("PythonVectorDB initialized with API URL: {}", apiUrl);
        
        // Check if the API is accessible
        if (!checkApiConnection()) {
            logger.warn("Vector database API is not accessible at {}. Starting the API server...", apiUrl);
            startApiServer();
        }
    }
    
    /**
     * Check if the vector database API is accessible.
     * 
     * @return true if the API is accessible, false otherwise
     */
    protected boolean checkApiConnection() {
        try {
            URL url = new URL(apiUrl + "/health");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                logger.info("Vector database API is accessible at {}", apiUrl);
                return true;
            } else {
                logger.warn("Vector database API returned status code: {}", responseCode);
                return false;
            }
        } catch (Exception e) {
            logger.warn("Error connecting to vector database API: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Start the vector database API server.
     */
    private void startApiServer() {
        try {
            logger.info("Starting vector database API server...");
            
            // Build the command to start the API server
            ProcessBuilder processBuilder = new ProcessBuilder(
                "python", 
                System.getProperty("user.home") + "/Desktop/workspace/convertToVectorDB/mock_vector_db_api.py",
                "--host", "127.0.0.1",
                "--port", "5000"
            );
            
            // Redirect error stream to output stream
            processBuilder.redirectErrorStream(true);
            
            // Start the process
            Process process = processBuilder.start();
            
            // Start a thread to read the output
            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        logger.info("API Server: {}", line);
                    }
                } catch (IOException e) {
                    logger.error("Error reading API server output: {}", e.getMessage());
                }
            }).start();
            
            // Wait for the API server to start
            logger.info("Waiting for API server to start...");
            TimeUnit.SECONDS.sleep(5);
            
            // Check if the API is accessible now
            if (checkApiConnection()) {
                logger.info("API server started successfully");
            } else {
                logger.error("Failed to start API server");
            }
        } catch (Exception e) {
            logger.error("Error starting API server: {}", e.getMessage());
        }
    }
    
    /**
     * Get the RestTemplate instance.
     * This method is protected to allow overriding in tests.
     * 
     * @return The RestTemplate instance
     */
    protected RestTemplate getRestTemplate() {
        return restTemplate;
    }
    
    @Override
    public List<String> search(String query, int topK) {
        logger.info("Searching for code snippets related to: {}", query);
        
        try {
            // Create the request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("query_text", query);
            requestBody.put("top_k", topK);
            
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Create the request entity
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody.toString(), headers);
            
            // Make the request
            String response = getRestTemplate().postForObject(apiUrl + "/query", requestEntity, String.class);
            
            // Parse the response
            JSONObject responseJson = new JSONObject(response);
            JSONArray results = responseJson.getJSONArray("results");
            
            // Extract the code snippets
            List<String> codeSnippets = new ArrayList<>();
            for (int i = 0; i < results.length(); i++) {
                JSONObject result = results.getJSONObject(i);
                String content = result.getString("content");
                String path = result.getString("path");
                double score = result.getDouble("score");
                
                // Format the code snippet with metadata
                String snippet = String.format(
                    "// File: %s (Similarity: %.2f)\n%s",
                    path, score, content
                );
                
                codeSnippets.add(snippet);
            }
            
            logger.info("Found {} code snippets", codeSnippets.size());
            return codeSnippets;
        } catch (Exception e) {
            logger.error("Error searching vector database: {}", e.getMessage());
            
            // Return empty list in case of error
            return new ArrayList<>();
        }
    }
}
