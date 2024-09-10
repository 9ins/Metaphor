package org.chaostocosmos.metadata.metaphor.api.controller;

/**
 * ErrorResponse object
 * 
 * @author Kooin-Shin
 */
public class ErrorResponse {
    /**
     * Error status
     */
    private int status;

    /**
     * Error message
     */
    private String message;

    /**
     * Timestamp
     */
    private long timestamp;

    /**
     * Constructs with status, message, timestamp
     * @param status
     * @param message
     * @param timestamp
     */
    public ErrorResponse(int status, String message, long timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    /**
     * Get status code
     * @return
     */
    public int getStatus() {
        return status;
    }

    /**
     * Set status code
     * @param status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Get error message
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set error message
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Get timestamp
     * @return
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Set timestamp
     * @param timestamp
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ErrorResponse [status=" + status + ", message=" + message + ", timestamp=" + timestamp + "]";
    }    
}
