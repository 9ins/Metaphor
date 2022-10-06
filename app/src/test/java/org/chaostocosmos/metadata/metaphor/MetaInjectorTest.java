package org.chaostocosmos.metadata.metaphor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.List;

import org.chaostocosmos.metadata.metaphor.event.MetaEvent;
import org.chaostocosmos.metadata.metaphor.event.MetaListener;
import org.junit.jupiter.api.Test; 

public class MetaInjectorTest implements MetaListener {

    public static MetaManager metaStorage = MetaManager.get(Paths.get("D:/0.github/Leap/config/"));

    @Test
    public static void testInject() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        MetaTest obj = new MetaTest();
        obj = new MetaInjector<MetaTest>(obj).inject(metaStorage.getMetaStore("hosts.yml"));
        System.out.println(obj.toString());
        List<User> users = obj.getUsers();
        System.out.println(users.get(0).username);
    }

    @Test
    public static void testInject2() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        MetaTest obj = new MetaTest();
        obj = new MetaInjector<MetaTest>(obj).inject(metaStorage.getMetaStore("hosts.yml"));
        System.out.println(obj);
    }

    public static void testInject3() {
        User user = new User();
        user = new MetaInjector<User>(user).inject(metaStorage.getMetaStore("hosts.yml"));
        System.out.println(user);
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
        testInject3();
    }   
}
