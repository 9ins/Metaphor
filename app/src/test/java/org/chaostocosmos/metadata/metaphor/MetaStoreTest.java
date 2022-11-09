package org.chaostocosmos.metadata.metaphor;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;

public class MetaStoreTest {

    @Test
    public void testSave() throws IOException, URISyntaxException {
        User user = new User();
        MetaHelper.save(new File("D:/0.github/Metaphor/user.properties"), user);        
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        MetaStoreTest test = new MetaStoreTest();
        test.testSave();
    }
}
