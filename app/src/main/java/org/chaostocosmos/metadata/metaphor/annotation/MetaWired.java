package org.chaostocosmos.metadata.metaphor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, 
          ElementType.FIELD, 
          ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
/**
 * Config mapping for field
 * 
 * @author 9ins
 */
public @interface MetaWired {

    /**
     * Metadata path expression
     * @return
     */
    String expr();      
}
