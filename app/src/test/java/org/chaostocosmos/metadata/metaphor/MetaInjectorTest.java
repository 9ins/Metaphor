package org.chaostocosmos.metadata.metaphor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test; 

public class MetaInjectorTest implements MetaListener {

    public static MetaManager metaStorage = MetaManager.get(Paths.get("D:/0.github/Leap/config/"));

    @Test
    public static void testInject() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        MetaTest obj = new MetaTest();
        obj = (MetaTest) MetaInjector.inject(metaStorage.getMetaStore("hosts.yml"), obj, MetaField.class);
        System.out.println(obj.toString());
        List<User> users = obj.getUsers();
        System.out.println(users.get(0).username);
    }

    @Test
    public static void testInject2() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        MetaTest obj = new MetaTest();
        obj = MetaInjector.inject(metaStorage.getMetaStore("hosts.yml"), obj, MetaField.class);
        System.out.println(obj);
    }

    public static void save() throws IOException {
        MetaStore metadata = metaStorage.getMetaStore("hosts.yml");
        metadata.save(new File("D:/0.github/Leap/config/hosts.json"));
    }

    @Override
    public <T> void metadataInjected(MetaEvent<T> e) {
        System.out.println(e.toString());        
    }

    @Override
    public <T> void metadataModified(MetaEvent<T> e) {
        System.out.println(e.toString());
    }

    @Override
    public <T> void metadataRemoved(MetaEvent<T> e) {
        System.out.println(e.toString());
    }

    @Override
    public <T> void metadataCreated(MetaEvent<T> e) {
        System.out.println(e.toString());
    }

    public static void main(String[] args) throws Exception {
        testInject2();
    }   
}
