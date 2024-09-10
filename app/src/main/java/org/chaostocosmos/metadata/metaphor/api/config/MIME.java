package org.chaostocosmos.metadata.metaphor.api.config;

import org.chaostocosmos.metadata.metaphor.api.component.PropertiesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * Mime type enum
 * 
 * @author Kooin-Shin
 */ 
@Configuration
public class MIME {

    @Autowired
    private static PropertiesUtils propertiesUtils;    
    
    public static enum Type {
        APPLICATION_OCTET_STREAM(propertiesUtils.getMimeType("APPLICATION_OCTET_STREAM"));

        Object type;

        Type(Object type) {
            this.type = type;
        }
    }
}
