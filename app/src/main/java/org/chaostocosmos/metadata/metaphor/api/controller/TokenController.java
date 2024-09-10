package org.chaostocosmos.metadata.metaphor.api.controller;

import org.chaostocosmos.metadata.metaphor.api.component.JwtUtil;
import org.chaostocosmos.metadata.metaphor.api.http.AuthRequest;
import org.chaostocosmos.metadata.metaphor.api.service.BasicUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AuthController
 * 
 * @author Kooin-Shin
 */
@RestController
@RequestMapping("")
public class TokenController {
    /**
     * JWT Util 
     */
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * User details service 
     */
    @Autowired
    BasicUserDetailsService userDetailsService;

    /**
     * Generate token with request of login account
     */
    @PostMapping("/token")
    public String generateToken(@RequestBody AuthRequest authRequest) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());        
        if(!userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "Only account which has ADMIN role can generate token!!!";
        }
        if (userDetails.getUsername().equals(authRequest.getUsername()) && userDetails.getPassword().equals(authRequest.getPassword())) {
            //LogAspect.info("User {} is logged in  data: {} ====================", userDetails.getUsername(), new Date().toString());
            return jwtUtil.generateToken(authRequest.getUsername());
        } else {
            return "Invalid credentials!!!";
        }
    }
}
