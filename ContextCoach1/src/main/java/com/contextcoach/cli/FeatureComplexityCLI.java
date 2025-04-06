package com.contextcoach.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;

import com.contextcoach.cli.service.ComplexityAnalyzer;
import com.contextcoach.cli.service.DummyLLMService;
import com.contextcoach.cli.service.DummyVectorDB;
import com.contextcoach.cli.service.FeatureClarifier;
import com.contextcoach.cli.service.LLMService;
import com.contextcoach.cli.service.PythonVectorDB;
import com.contextcoach.cli.service.VectorDB;

/**
 * Main CLI application for feature complexity analysis.
 * This can be run as a standalone application or as part of the main ContextCoach application.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.contextcoach"})
public class FeatureComplexityCLI {
    
    private static final Logger logger = LoggerFactory.getLogger(FeatureComplexityCLI.class);
    
    
    public static void main(String[] args) {
        SpringApplication.run(FeatureComplexityCLI.class, args);
    }
    
    @Bean
    @Profile("!test")
    public LLMService llmService() {
        logger.info("Using dummy LLM service");
        return new DummyLLMService();
    }
    
    @Bean
    public VectorDB vectorDB() {
        // Check if we should use the Python vector database
        String useRealVectorDB = System.getenv("USE_REAL_VECTOR_DB");
        if (useRealVectorDB != null && useRealVectorDB.equalsIgnoreCase("true")) {
            String apiUrl = System.getenv("VECTOR_DB_API_URL");
            if (apiUrl == null || apiUrl.isEmpty()) {
                apiUrl = "http://localhost:5000"; // Default API URL
            }
            logger.info("Using Python vector database with API URL: {}", apiUrl);
            return new PythonVectorDB(apiUrl);
        } else {
            logger.info("Using dummy vector database");
            return new DummyVectorDB();
        }
    }
    
    @Bean
    public CommandLineRunner commandLineRunner(LLMService llmService, VectorDB vectorDB) {
        return args -> {
            logger.info("Starting Feature Complexity Analysis CLI");
            
            // Read the initial feature description from a file if a filepath is provided as an argument.
            String initialDescription = "";
            if (args.length > 0) {
                try {
                    initialDescription = new String(Files.readAllBytes(Paths.get(args[0])));
                    logger.info("Read feature description from file: {}", args[0]);
                } catch (IOException e) {
                    logger.error("Failed to read feature description from file: {}", e.getMessage());
                    System.err.println("Failed to read feature description from file: " + e.getMessage());
                    return;
                }
            } else {
                // Otherwise, prompt the user to enter the feature description via CLI.
                System.out.println("Enter the feature description (end with an empty line):");
                Scanner scanner = new Scanner(System.in);
                StringBuilder inputBuilder = new StringBuilder();
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.isEmpty()) {
                        break;  // empty line denotes end of input
                    }
                    inputBuilder.append(line).append("\n");
                }
                initialDescription = inputBuilder.toString().trim();
                logger.info("Read feature description from user input");
            }
            
            if (initialDescription.isEmpty()) {
                logger.error("No feature description provided");
                System.err.println("Error: No feature description provided.");
                return;
            }

            // --- Phase 1: Feature Clarification ---
            System.out.println("\n=== Phase 1: Feature Clarification ===");
            FeatureClarifier clarifier = new FeatureClarifier(vectorDB, llmService);
            String clarifiedFeature = clarifier.clarifyFeature(initialDescription);
            System.out.println("\n--- Clarified Feature Description ---");
            System.out.println(clarifiedFeature);

            // --- Phase 2: Complexity Analysis ---
            System.out.println("\n=== Phase 2: Complexity Analysis ===");
            ComplexityAnalyzer analyzer = new ComplexityAnalyzer(vectorDB, llmService);
            String reportJson = analyzer.analyzeFeature(clarifiedFeature);

            // Output the final structured report in JSON format.
            System.out.println("\n=== Feature Complexity Analysis Report ===");
            System.out.println(reportJson);
            
            logger.info("Feature complexity analysis completed");
        };
    }
}
