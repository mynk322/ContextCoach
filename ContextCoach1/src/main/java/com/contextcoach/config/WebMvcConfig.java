package com.contextcoach.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.contextcoach.logging.ApiRequestLogger;

/**
 * Web MVC configuration for the application
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final ApiRequestLogger apiRequestLogger;

    @Autowired
    public WebMvcConfig(ApiRequestLogger apiRequestLogger) {
        this.apiRequestLogger = apiRequestLogger;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Register API request logger interceptor
        registry.addInterceptor(apiRequestLogger)
                .addPathPatterns("/api/**"); // Apply to all API endpoints
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Configure CORS for development
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:3007") // Frontend URLs
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
