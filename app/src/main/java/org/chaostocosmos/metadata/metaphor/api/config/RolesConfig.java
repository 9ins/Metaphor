package org.chaostocosmos.metadata.metaphor.api.config;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * RolesConfig object
 * 
 * @author Kooin-Shin
 */
@Configuration
@ConfigurationProperties(prefix = "spring.security")
public class RolesConfig {    
    /**
     * Roles
     */
    private Map<String, List<String>> roles;

    /**
     * Get roles Map
     * @return
     */
    public Map<String, List<String>> getRoles() {
        return roles;
    }

    /**
     * Set roles Map
     * @param roles
     */
    public void setRoles(Map<String, List<String>> roles) {
        this.roles = roles;
    }
}

