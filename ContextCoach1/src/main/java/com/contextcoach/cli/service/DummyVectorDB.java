package com.contextcoach.cli.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dummy implementation of VectorDB for simulation.
 * Always returns a fixed dummy code snippet related to the query.
 */
public class DummyVectorDB implements VectorDB {
    
    private static final Logger logger = LoggerFactory.getLogger(DummyVectorDB.class);
    
    public DummyVectorDB() {
        logger.info("DummyVectorDB initialized");
    }
    
    @Override
    public List<String> search(String query, int topK) {
        logger.info("Searching for code snippets related to: {}", query);
        // In a real implementation, this would perform a vector similarity search on embedded code.
        // Here we simulate by returning placeholder snippets.
        List<String> results = new ArrayList<>();
        
        // Add some dummy code snippets that might be relevant to the query
        results.add("// Dummy code snippet 1 relevant to: " + query + "\n" +
                    "public class UserService {\n" +
                    "    public User findUserById(String userId) {\n" +
                    "        return userRepository.findById(userId);\n" +
                    "    }\n" +
                    "}");
        
        if (topK > 1) {
            results.add("// Dummy code snippet 2 relevant to: " + query + "\n" +
                        "public interface UserRepository {\n" +
                        "    User findById(String id);\n" +
                        "    List<User> findByRole(String role);\n" +
                        "}");
        }
        
        if (topK > 2) {
            results.add("// Dummy code snippet 3 relevant to: " + query + "\n" +
                        "public class AuthenticationService {\n" +
                        "    public boolean authenticate(String username, String password) {\n" +
                        "        User user = userRepository.findByUsername(username);\n" +
                        "        return user != null && passwordEncoder.matches(password, user.getPassword());\n" +
                        "    }\n" +
                        "}");
        }
        
        logger.debug("Returning {} code snippets", results.size());
        return results;
    }
}
