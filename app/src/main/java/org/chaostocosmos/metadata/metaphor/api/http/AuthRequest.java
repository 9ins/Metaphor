package org.chaostocosmos.metadata.metaphor.api.http;

/**
 * AuthRequest object
 * 
 * @author Kooin-Shin
 */
public class AuthRequest {
    /**
     * User name
     */
    private String username;

    /**
     * User password
     */
    private String password;

    /**
     * Get user name
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * Get user password
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set user name
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }    
    
    /**
     * Set user password
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }    
}
