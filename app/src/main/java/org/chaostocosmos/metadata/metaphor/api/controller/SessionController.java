package org.chaostocosmos.metadata.metaphor.api.controller;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * SessionController object
 * 
 * @author Kooin-Shin
 */
@RestController
@RequestMapping("/session")
public class SessionController {
    /**
     * Set value to session attribute
     * @param servletRequest
     * @param session
     * @return
     */
    @GetMapping("/set")
    public String setSessionAttribute(HttpServletRequest servletRequest, HttpSession session) {
        Map<String, String[]> paramMap = servletRequest.getParameterMap();
        paramMap.entrySet().stream().forEach(e -> session.setAttribute(e.getKey(), e.getValue()));
        return "Session attribute set: "+System.lineSeparator()+paramMap.entrySet().stream().map(e -> e.getKey()+": "+Arrays.toString(e.getValue())).collect(Collectors.joining(System.lineSeparator()));
    }
    
    /**
     * Get value from session attribute
     * @param servletRequest
     * @param session
     * @return
     */
    @GetMapping("/get")                          
    public String getSessionAttribute(HttpServletRequest servletRequest, HttpSession session) {        
        String attribute = (String) session.getAttribute(servletRequest.getParameter("attribute"));
        return attribute != null ? attribute : "No attribute found!";
    }

    /**
     * Invalidate session
     * @param servletRequest
     * @param session
     * @return
     */
    @GetMapping("/invalidate")
    public String invalidateSession(HttpServletRequest servletRequest, HttpSession session) {
        session.invalidate();
        return "Session invalidated!";
    }
}
