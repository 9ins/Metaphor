package org.chaostocosmos.metadata.metaphor.api.session;

import org.chaostocosmos.metadata.metaphor.api.component.LoggerAspect;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

/**
 * APISessionListener
 * 
 * @author Kooin-Shin
 */
public class APISessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        // Handle session creation event
        LoggerAspect.info("Session created with ID: {}", se.getSession().getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        // Handle session destruction event
        LoggerAspect.info("Session destroyed with ID: {}", se.getSession().getId());
    }    
}
