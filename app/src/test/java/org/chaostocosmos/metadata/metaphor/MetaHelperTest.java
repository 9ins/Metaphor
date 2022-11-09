package org.chaostocosmos.metadata.metaphor;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.chaostocosmos.metadata.metaphor.annotation.MetaWired;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * MetaHelperTest
 * 
 * @author 9ins
 */
public class MetaHelperTest {
    @Test
    public static void testGet() throws URISyntaxException, JsonMappingException, JsonProcessingException {
        File metaFile = new File(ClassLoader.getSystemClassLoader().getResource("sample.json").toURI());
        MetaTest user = MetaHelper.<MetaTest> get(metaFile, MetaTest.class);
        System.out.println(user);
    }

    @Test
    public static void testScanAll() throws JsonMappingException, JsonProcessingException, URISyntaxException {
        File metaFile = new File(ClassLoader.getSystemClassLoader().getResource("sample.json").toURI());
        List<Object> list = MetaHelper.scanAnnotatedObject(metaFile, new Path[] {Paths.get("./app/bin/test/")});
        System.out.println(list.toString());
    }

    String s;
    
    @Test
    public void testAnnotationParam(@MetaWired(expr = "hosts[0].users[0].username") String s) {
        this.s = s;
    }    

    public static void main(String[] args) throws JsonMappingException, JsonProcessingException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, FileNotFoundException {
        MetaStore metaStore = new MetaStore(new File("D:/0.github/Metaphor/hosts.yml"));
        MetaHelperTest test = MetaHelper.get(metaStore, MetaHelperTest.class);
        System.out.println(test.s);
    }
}

