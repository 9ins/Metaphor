package org.chaostocosmos.metadata.metaphor;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test; 

public class MetadataTest {

    @Test
    public void loadTest() throws IOException, URISyntaxException {
        MetaStore metadata = new MetaStore("sample.json");
        System.out.println(metadata.toString());

        File dir = new File(ClassLoader.getSystemClassLoader().getResource("").toURI());
        File[] files = dir.listFiles();
        for(File file : files) {
            System.out.println(file.toString());
        }
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        MetadataTest test = new MetadataTest();
        test.loadTest();
    }
}

