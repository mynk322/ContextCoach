package com.contextcoach.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a service operation fails
 */
public class ServiceException extends RuntimeException {

    private final HttpStatus status;

    /**
     * Constructor with message and status
     * 
     * @param message The error message
     * @param status The HTTP status code
     */
    public ServiceException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    /**
     * Constructor with message, cause, and status
     * 
     * @param message The error message
     * @param cause The cause of the exception
     * @param status The HTTP status code
     */
    public ServiceException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }

    /**
     * Gets the HTTP status code
     * 
     * @return The HTTP status code
     */
    public HttpStatus getStatus() {
        return status;
    }
}
