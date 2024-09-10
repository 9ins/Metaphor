package org.chaostocosmos.metadata.metaphor.api.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * UsersConfig object
 * 
 * @author Kooin-Shin
 */
@Configuration
@ConfigurationProperties(prefix = "spring.security")
public class UsersConfig {
    /**
     * User list
     */
    private List<User> users;

    /**
     * Get user list
     * @return
     */
    public List<User> getUsers() {
        return users;
    }

    /**
     * Set user list
     * @param users
     */
    public void setUsers(List<User> users) {
        this.users = users;
    }

    /**
     * User object
     * 
     * @author Kooin-Shin
     */
    public static class User {
        /**
         * user name
         */
        private String name;

        /**
         * User password
         */
        private String password;

        /**
         * User roles
         */
        private List<String> roles;

        /**
         * JWT token
         */
        private String jwtToken;

        /**
         * Get name
         * @return
         */
        public String getName() {
            return name;
        }

        /**
         * Set name
         * @param name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Get password
         * @return
         */
        public String getPassword() {
            return password;
        }

        /**
         * Set password
         * @param password
         */
        public void setPassword(String password) {
            this.password = password;
        }

        /**
         * Get roles
         * @return
         */
        public List<String> getRoles() {
            return roles;
        }

        /**
         * Set roles
         * @param roles
         */
        public void setRoles(List<String> roles) {
            this.roles = roles;
        }

        /**
         * Get JWT token
         * @return
         */
        public String getJwtToken() {
            return jwtToken;
        }

        /**
         * Set JWT token
         * @param jwtToken
         */
        public void setJwtToken(String jwtToken) {            
            this.jwtToken = jwtToken;
        }
    }
}
