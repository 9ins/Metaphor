package org.chaostocosmos.metadata.metaphor;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test; 

public class MetadataTest {

    @Test
    public void loadTest() throws IOException {
        MetaStore metadata = new MetaStore(Paths.get("D:/0.github/Leap/config/hosts.yml"));
        System.out.println(metadata.toString());
    }    
}
