package org.chaostocosmos.metadata.metaphor;

import java.net.URI;
import java.net.URISyntaxException;

public class SimpleTest {
    
    public static void main(String[] args) throws URISyntaxException {
        URI uri = ClassLoader.getSystemClassLoader().getResource("").toURI();
        System.out.println(uri.toString());
    }
}
