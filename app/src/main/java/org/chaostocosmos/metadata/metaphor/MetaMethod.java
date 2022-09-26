package org.chaostocosmos.metadata.metaphor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
/**
 * MetaMethod Annotation
 * 
 * @author 9ins
 */
public @interface MetaMethod {    
    //Metadata finding expression
    public String[] parameters();
}
