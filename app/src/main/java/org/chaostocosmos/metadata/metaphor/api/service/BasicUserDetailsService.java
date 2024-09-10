package org.chaostocosmos.metadata.metaphor.api.service;

import org.chaostocosmos.metadata.metaphor.api.config.UsersConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class BasicUserDetailsService implements UserDetailsService {

    @Autowired
    private UsersConfig usersConfig;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Here you can fetch the user from the database
        // For simplicity, let's return a hardcoded user
        // For in this case I would use for this with user information in application.yaml
        UsersConfig.User user = usersConfig.getUsers().stream().filter(u -> u.getName().equals(username)).findAny().orElse(null);
        if(user == null) {
            throw new UsernameNotFoundException("User: "+username+" is not found.");
        }
        return User.withUsername(user.getName()).password(user.getPassword()).roles(user.getRoles().stream().map(r -> r.substring(5)).toArray(String[]::new)).build();
    }
    
}
