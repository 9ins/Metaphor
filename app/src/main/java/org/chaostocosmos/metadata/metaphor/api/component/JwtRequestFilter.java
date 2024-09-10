package org.chaostocosmos.metadata.metaphor.api.component;

import java.io.IOException;

import org.chaostocosmos.metadata.metaphor.api.service.BasicUserDetailsService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JwtRequestFilter object
 * 
 * @Kooin-Shin
 */
@Component
public class JwtRequestFilter extends GenericFilterBean {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BasicUserDetailsService userDetailsService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        final HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        final String authorizationHeader = httpServletRequest.getHeader("Authorization");
        String username = null;
        String jwtToken = null;
        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwtToken = authorizationHeader.substring(7);
                username = jwtUtil.extractUsername(jwtToken);
            }
        } catch(Exception e) {
            LoggerFactory.getLogger(JwtRequestFilter.class).error("Exception in Filter: "+this.getFilterName(), e);
            httpServletResponse.sendError(401, e.getMessage());
            return;
        } 
        LoggerAspect.getLogger().info("User: "+username+" ---------- Auth: "+authorizationHeader+" ------------------------- ", username, authorizationHeader);
        if(username != null) {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                if(!jwtUtil.validateToken(jwtToken, username)) {
                    httpServletResponse.sendError(401, "Your request is invalied.");
                    return;
                } else if(jwtUtil.isTokenExpired(jwtToken)) {
                    httpServletResponse.sendError(410, "Your token is expired."); 
                    return;
                } else if (jwtUtil.validateToken(jwtToken, username)) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        LoggerAspect.getLogger().info("Filter process finish......");
        chain.doFilter(request, response);
    }
}
