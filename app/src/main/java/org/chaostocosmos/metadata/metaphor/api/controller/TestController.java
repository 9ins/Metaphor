package org.chaostocosmos.metadata.metaphor.api.controller;

import org.chaostocosmos.metadata.metaphor.api.config.MIME;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/null")
    public String test() {
        throw new NullPointerException("Test NullPointerException");
    }

    @GetMapping("/illegal-argument")
    public String testIllegalArgument() {
        throw new IllegalArgumentException("Test IllegalArgumentException");
    }

    @GetMapping("/exception")
    public String testException() {
        throw new RuntimeException("Test General Exception");
    }

    @GetMapping("/mimeType") 
    public String testMimeType() {
        String mime = MIME.Type.APPLICATION_OCTET_STREAM.toString();
        System.out.println(mime);
        return mime;
    }
}
