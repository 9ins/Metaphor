package org.chaostocosmos.metadata.metaphor;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.chaostocosmos.metadata.metaphor.enums.META_EXT;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

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
     * MetaManager instance
     */
    private static MetaManager metaManager;

    /**
     * Get MetaManager instance
     * @param resourcePath
     * @return
     * @throws URISyntaxException
     */
    public static MetaManager get(String resourcePath) throws URISyntaxException {
        return get(new File(ClassLoader.getSystemClassLoader().getResource(resourcePath).toURI()).toPath());
    }

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
        this.metadataMap = new HashMap<>();
        if(!metadataDir.toFile().isDirectory()) {
            throw new IllegalArgumentException("Metadata path must be directory!!!");
        }
        try {
            load();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        disableAccessWarnings();
    }

    /**
     * Load metadata files
     * @throws JsonProcessingException
     * @throws JsonMappingException
     * @throws FileNotFoundException
     */
    private synchronized void load() throws JsonMappingException, JsonProcessingException, FileNotFoundException {
        File[] files = this.metadataDir.toFile().listFiles(new FileFilter() {
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
        });
        for(File file : files) {
            MetaStore ms = new MetaStore(file);
            ms.setMetaManager(metaManager);
            this.metadataMap.put(file.toPath(), ms);
        }
    }

    /**
     * Reload all of meta store
     * @throws JsonProcessingException
     * @throws JsonMappingException
     * @throws FileNotFoundException
     */
    public void reload() throws JsonMappingException, JsonProcessingException, FileNotFoundException {
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
