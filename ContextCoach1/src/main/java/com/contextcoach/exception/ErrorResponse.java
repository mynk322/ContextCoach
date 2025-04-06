package com.contextcoach.exception;

import java.time.LocalDateTime;

/**
 * Standard error response for API errors
 */
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    /**
     * Constructor for ErrorResponse
     * 
     * @param timestamp The time when the error occurred
     * @param status The HTTP status code
     * @param error The error type
     * @param message The error message
     * @param path The request path
     */
    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    /**
     * Default constructor
     */
    public ErrorResponse() {
    }

    /**
     * Gets the timestamp
     * 
     * @return The timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp
     * 
     * @param timestamp The timestamp
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the status code
     * 
     * @return The status code
     */
    public int getStatus() {
        return status;
    }

    /**
     * Sets the status code
     * 
     * @param status The status code
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Gets the error type
     * 
     * @return The error type
     */
    public String getError() {
        return error;
    }

    /**
     * Sets the error type
     * 
     * @param error The error type
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     * Gets the error message
     * 
     * @return The error message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the error message
     * 
     * @param message The error message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the request path
     * 
     * @return The request path
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the request path
     * 
     * @param path The request path
     */
    public void setPath(String path) {
        this.path = path;
    }
}
