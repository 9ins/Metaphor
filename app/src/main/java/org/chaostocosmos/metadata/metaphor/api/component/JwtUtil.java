package org.chaostocosmos.metadata.metaphor.api.component;
import java.util.Date;

import org.chaostocosmos.metadata.metaphor.api.config.UsersConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import jakarta.annotation.PostConstruct;

/**
 * JWT uility object 
 * 
 * @author Kooin-Shin
 */
@Component
public class JwtUtil {    
    /**
     * Secret key
     */
    @Value("${spring.security.jwt.secret}")
    private String secretKey;

    /**
     * Token expireation data milliseconds
     */
    @Value("${spring.security.jwt.expiration}")
    private long expiration;

    /**
     * Autowired UsersConfig object
     */
    @Autowired
    private UsersConfig usersConfig;
    
    /**
     * Token algorithm
     */
    private Algorithm algorithm;

    /**
     * JWT verifier object
     */
    private JWTVerifier verifier;

    @PostConstruct
    public void init() {
        algorithm = Algorithm.HMAC256(secretKey);
        verifier = JWT.require(algorithm).build();
        System.out.println(this.usersConfig.getUsers());
        //print users information
        //usersConfig.getUsers().stream().forEach(u -> u.setJwtToken(generateToken(u.getName())));
    }

    /**
     * Generate token for parameted user
     * @param username
     * @return
     */
    public String generateToken(String username) {        
        String token = JWT.create()
                          .withSubject(username)
                          .withIssuedAt(new Date())
                          .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
                          .sign(algorithm);
        return token;
    }

    /**
     * Extract username from token
     * @param token
     * @return
     */
    public String extractUsername(String token) {
        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT.getSubject();
    }

    /**
     * Whether token expired
     * @param token
     * @return
     */
    public boolean isTokenExpired(String token) {
        DecodedJWT decodedJWT = verifier.verify(token);
        Date expirationDate = decodedJWT.getExpiresAt();
        return expirationDate.before(new Date());
    }

    /**
     * Validate token with specified username
     * @param token
     * @param username
     * @return
     */
    public boolean validateToken(String token, String username) {
        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
    }
}
