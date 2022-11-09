package org.chaostocosmos.metadata.metaphor;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.List;

import org.chaostocosmos.metadata.metaphor.event.MetaEvent;
import org.chaostocosmos.metadata.metaphor.event.MetaListener;
import org.junit.jupiter.api.Test; 

public class MetaInjectorTest implements MetaListener {

    public MetaManager metaManager = MetaManager.get(Paths.get(""));

    @Test
    public void testInject() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        MetaTest obj = new MetaTest();
        obj = new MetaInjector<MetaTest>(obj).inject(metaManager.getMetaStore("sample.json"));
        System.out.println(obj.toString());
        List<User> users = obj.getUsers();
        System.out.println(users.get(0).username);
    }

    @Test
    public void testInject2() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        MetaTest obj = new MetaTest();
        obj = new MetaInjector<MetaTest>(obj).inject(metaManager.getMetaStore("sample.json"));
        System.out.println(obj);
    }

    public void testInject3() {
        User user = new User();
        MetaStore metaStore = this.metaManager.getMetaStore("sample.json");
        metaStore.addMetaListener(this);
        user = new MetaInjector<User>(user).inject(metaStore);
        System.out.println(user);
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
        MetaInjectorTest test = new MetaInjectorTest();
        test.testInject3();
    }   
}

