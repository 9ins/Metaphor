package org.chaostocosmos.metadata.metaphor;

import java.net.URISyntaxException;
import java.util.List;

import org.chaostocosmos.metadata.metaphor.core.MetaInjector;
import org.chaostocosmos.metadata.metaphor.core.MetaManager;
import org.chaostocosmos.metadata.metaphor.core.MetaStore;
import org.chaostocosmos.metadata.metaphor.event.MetaEvent;
import org.chaostocosmos.metadata.metaphor.event.MetaListener;
import org.junit.jupiter.api.Test; 

public class MetaInjectorTest implements MetaListener {    

    @Test
    public void testInject() throws URISyntaxException {
        MetaManager metaManager = MetaManager.get("");
        MetaTest obj = new MetaTest();
        obj = new MetaInjector<MetaTest>(obj).inject(metaManager.getMetaStore("hosts.yml"));
        System.out.println(obj.toString());
        List<User> users = obj.getUsers();
        System.out.println(users.get(0).username);
    }

    @Test
    public void testInject2() throws URISyntaxException {
        MetaManager metaManager = MetaManager.get("");        
        MetaTest obj = new MetaTest();
        obj = new MetaInjector<MetaTest>(obj).inject(metaManager.getMetaStore("hosts.yml"));
        System.out.println(obj);
    }

    public void testInject3() throws URISyntaxException {
        MetaManager metaManager = MetaManager.get("");
        User user = new User();
        MetaStore metaStore = metaManager.getMetaStore("sample.json");
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

