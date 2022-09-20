package org.chaostocosmos.metadata.metaphor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test; 

public class MetaInjectorTest {

    public static MetaManager metaStorage = new MetaManager(Paths.get("D:/0.github/Leap/config/"));

    @Test
    public static void testInject() throws IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, NoSuchFieldException {
        MetaTest obj = new MetaTest();
        obj = (MetaTest) new MetaInjector(metaStorage.getMetadata("hosts.yml")).inject(obj, MetaField.class);
        System.out.println(obj.toString());
        List<User> users = obj.getUsers();
        System.out.println(users.get(0).username);
    }

    @Test
    public static void testInject2() throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, InvocationTargetException, NoSuchFieldException {
        MetaTest obj = new MetaTest();
        MetaInjector injector = new MetaInjector(metaStorage.getMetadata("hosts.yml"));
        obj = (MetaTest) injector.inject(obj, MetaField.class);
        System.out.println(obj);        
    }

    public static void save() throws IOException {
        MetaStore metadata = metaStorage.getMetadata("hosts.yml");
        metadata.save(new File("D:/0.github/Leap/config/hosts.json"));
    }

    public static void main(String[] args) throws Exception {
        testInject2();
    }
    
}
