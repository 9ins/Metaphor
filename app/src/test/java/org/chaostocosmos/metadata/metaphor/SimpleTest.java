package org.chaostocosmos.metadata.metaphor;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.security.crypto.codec.Base64;

public class SimpleTest {
    
    public static void main(String[] args) throws URISyntaxException {
        URI uri = ClassLoader.getSystemClassLoader().getResource("").toURI();
        System.out.println(uri.toString());
        String originalString = "mysecret";
        String encodedString = new String(Base64.encode(originalString.getBytes()));
        System.out.println("Encoded String: " + encodedString);

    }
}
