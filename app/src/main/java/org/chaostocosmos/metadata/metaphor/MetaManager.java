package org.chaostocosmos.metadata.metaphor;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * MetadataContext
 * 
 * @author 9ins
 */
public class MetaManager {
    /**
     * Metadata directory path
     */
    Path metadataDir;

    /**
     * Metadata Map
     */
    Map<Path, MetaStore> metadataMap;

    /**
     * Metadata listener list
     */
    List<MetaListener> metadataListeners;

    /**
     * MetaManager instance
     */
    private static MetaManager metaManager;

    /**
     * Get MetaManager instance
     * @param metadataPath
     * @return
     */
    public static MetaManager get(Path metadataPath) {
        if(metaManager == null) {
            metaManager = new MetaManager(metadataPath);
        }
        return metaManager;
    }

    /**
     * Constructs with metadata directory path
     * @param metadataPath
     */
    private MetaManager(String metadataPath) {
        this(Paths.get(metadataPath));
    }

    /**
     * Constructs with metadata directory Path object
     * @param metadataDir
     */
    private MetaManager(Path metadataDir) {
        this.metadataDir = metadataDir;
        if(!metadataDir.toFile().isDirectory()) {
            throw new IllegalArgumentException("Metadata path must be directory!!!");
        }
        this.metadataListeners = new ArrayList<>();
        load();
        disableAccessWarnings();
    }

    /**
     * Load metadata files
     */
    private synchronized void load() {
        this.metadataMap = Stream.of(this.metadataDir.toFile().listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String ext = pathname.getName().substring(pathname.getName().lastIndexOf(".")+1);
                return pathname.isFile() &&
                    (  
                       ext.equalsIgnoreCase(META_EXT.CONF.name())
                    || ext.equalsIgnoreCase(META_EXT.CONFIG.name())
                    || ext.equalsIgnoreCase(META_EXT.JSON.name())
                    || ext.equalsIgnoreCase(META_EXT.PROPERTIES.name())
                    || ext.equalsIgnoreCase(META_EXT.YML.name())
                    || ext.equalsIgnoreCase(META_EXT.YAML.name())
                    );
            }            
        })).map( f -> {
            MetaStore ms = new MetaStore(f);
            ms.setMetaManager(metaManager);
            return new Object[]{f.toPath(), ms};
        }).collect(Collectors.toMap(k -> (Path)k[0], v -> (MetaStore)v[1]));
    }

    /**
     * Reload all of meta store
     */
    public void reload() {
        load();
    }

    /**
     * Get metadata value
     * @param <V>
     * @param filename
     * @param expr
     * @return
     */
    public <V> V getValue(String filename, String expr) {
        return getMetaStore(filename).getValue(expr);
    }

    /**
     * Get metadata value
     * @param <V>
     * @param metaFile
     * @param expr
     * @return
     */
    public <V> V getValue(Path metaFile, String expr) {
        return getMetaStore(metaFile).getValue(expr);
    }

    /**
     * Get metadata 
     * @param filename 
     * @return
     */
    public MetaStore getMetaStore(String filename) {
        return getMetaStore(this.metadataDir.resolve(filename));
    }

    /**
     * Get metadata
     * @param metaFile
     * @return
     */
    public MetaStore getMetaStore(Path metaFile) {
        return this.metadataMap.get(metaFile);
    }

    /**
     * Dispatch metadata event
     * @param <T>
     * @param eventType
     * @param me
     */
    public <T> void dispatchMetaEvent(EVENT_TYPE eventType, MetaEvent<T> me) {
        if(this.metadataListeners.size() > 0) {
            switch(eventType) {
                case INJECTED :
                this.metadataListeners.stream().forEach(l -> l.metadataInjected(me));                
                break;
                case CREATED :
                this.metadataListeners.stream().forEach(l -> l.metadataCreated(me));
                break;
                case REMOVED :
                this.metadataListeners.stream().forEach(l -> l.metadataRemoved(me));
                break;
                case MODIFIED :
                this.metadataListeners.stream().forEach(l -> l.metadataModified(me));
                break;
            }
        }
    }

    /**
     * Add metadata listener
     * @param listener
     */
    public void addMetaListener(MetaListener listener) {
        this.metadataListeners.add(listener);
    }        

    /**
     * Remove metadata listener
     * @param listener
     */
    public void removeListener(MetaListener listener) {
        this.metadataListeners.remove(listener);
    }

    /**
     * Disable access warning
     */
    public void disableAccessWarnings() {
        try {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Field field = unsafeClass.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            Object unsafe = field.get(null);
            Method volatileMethod = unsafeClass.getDeclaredMethod("putObjectVolatile", Object.class, long.class, Object.class);
            Method offsetMethod = unsafeClass.getDeclaredMethod("staticFieldOffset", Field.class);
            Class<?> loggerClass = Class.forName("jdk.internal.module.IllegalAccessLogger");
            Field loggerField = loggerClass.getDeclaredField("logger");
            Long offset = (Long) offsetMethod.invoke(unsafe, loggerField);
            volatileMethod.invoke(unsafe, loggerClass, offset, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    
}
