package org.chaostocosmos.metadata.metaphor.api.config;

import org.chaostocosmos.metadata.metaphor.api.component.JwtRequestFilter;
import org.chaostocosmos.metadata.metaphor.api.service.BasicUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * SecurityConfig
 * 
 * @author Kooin-Shin
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private BasicUserDetailsService userDetailsService;

    @Autowired
    private UsersConfig usersConfig;

    @Autowired
    private RolesConfig rolesConfig;

    @Value("${spring.security.cors}")
    private boolean cors;
    
    @Value("${spring.security.csrf}") 
    private boolean csrf;

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {        
        //LogAspect.info("Configuring SecurityFilterChain");
        http.csrf().disable();
        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers("/token").permitAll();
            rolesConfig.getRoles().forEach((role, paths) -> {
                String roleWithoutPrefix = role.substring(5); //Ensure you handle prefix if necessary
                paths.forEach(path -> {
                    //LogAspect.info("Mapping path {} to role {}", path, roleWithoutPrefix);
                    auth.requestMatchers(path).hasRole(roleWithoutPrefix);
                    });
                });
                auth.anyRequest()
                    .authenticated(); // Ensure all other requests are authenticated
            });
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterAfter(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);    
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    } 
}
