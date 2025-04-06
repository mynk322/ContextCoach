package com.contextcoach.logging;

import java.util.Enumeration;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Interceptor to log API requests and responses
 */
@Component
public class ApiRequestLogger implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ApiRequestLogger.class);
    private static final String REQUEST_ID_ATTRIBUTE = "requestId";
    private static final String START_TIME_ATTRIBUTE = "startTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Generate a unique request ID
        String requestId = UUID.randomUUID().toString();
        request.setAttribute(REQUEST_ID_ATTRIBUTE, requestId);
        request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());

        // Log request details
        StringBuilder logMessage = new StringBuilder();
        logMessage.append(String.format("REQUEST [%s] %s %s", requestId, request.getMethod(), request.getRequestURI()));
        
        // Log query parameters if present
        String queryString = request.getQueryString();
        if (queryString != null) {
            logMessage.append("?").append(queryString);
        }
        
        // Log request headers
        logMessage.append("\nHeaders: {");
        Enumeration<String> headerNames = request.getHeaderNames();
        boolean firstHeader = true;
        while (headerNames.hasMoreElements()) {
            if (!firstHeader) {
                logMessage.append(", ");
            }
            String headerName = headerNames.nextElement();
            // Skip sensitive headers like Authorization
            if (!"authorization".equalsIgnoreCase(headerName)) {
                logMessage.append(headerName).append("=").append(request.getHeader(headerName));
                firstHeader = false;
            }
        }
        logMessage.append("}");
        
        // Log client info
        logMessage.append("\nClient: ").append(request.getRemoteAddr());
        
        logger.info(logMessage.toString());
        
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        // Not used in this implementation
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String requestId = (String) request.getAttribute(REQUEST_ID_ATTRIBUTE);
        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        long duration = System.currentTimeMillis() - startTime;
        
        StringBuilder logMessage = new StringBuilder();
        logMessage.append(String.format("RESPONSE [%s] %s %s - Status: %d, Duration: %dms", 
                requestId, request.getMethod(), request.getRequestURI(), response.getStatus(), duration));
        
        // Log exception if present
        if (ex != null) {
            logMessage.append("\nException: ").append(ex.getMessage());
        }
        
        logger.info(logMessage.toString());
    }
}
