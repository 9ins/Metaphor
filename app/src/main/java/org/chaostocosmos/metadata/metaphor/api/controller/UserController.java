package org.chaostocosmos.metadata.metaphor.api.controller;

import java.util.List;

import org.chaostocosmos.metadata.metaphor.api.config.UsersConfig;
import org.chaostocosmos.metadata.metaphor.api.config.UsersConfig.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.ServletRequest;

@RestController
public class UserController {

    @Autowired
    private UsersConfig usersConfig;

    @GetMapping("/users")
    public List<User> getUsers() {
        return usersConfig.getUsers();
    }

    @GetMapping("/user")
    public UsersConfig.User getUser(ServletRequest request) {        
        String name = request.getParameter("name");        
        return usersConfig.getUsers().stream().filter(u -> u.getName().equals(name)).findAny().get();
    }
}

