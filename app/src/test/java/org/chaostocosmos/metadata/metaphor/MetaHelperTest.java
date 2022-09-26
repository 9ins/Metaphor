package org.chaostocosmos.metadata.metaphor;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * MetaHelperTest
 */
public class MetaHelperTest {

    static File metaFile = new File("D:/0.github/Leap/config/hosts.yml");

    @Test
    public static void testGet() throws IllegalArgumentException, IllegalAccessException {
        MetaTest user = MetaHelper.<MetaTest> get(metaFile, MetaTest.class);
        System.out.println(user);
    }

    @Test
    public static void testScanAll() {
        List<Object> list = MetaHelper.scanAnnotatedObject(metaFile, new Path[] {Paths.get("./app/bin/test/")});
        System.out.println(list.toString());
    }
    
    @Test
    public static String testAnnotationParam(@MetaParameter(expr = "hosts[0].users[0].username") String s) {
        System.out.println(s);
        return s;
    }

    public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
        testAnnotationParam("");
    }
}

