package org.chaostocosmos.metadata.metaphor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.chaostocosmos.metadata.metaphor.enums.EVENT_TYPE;
import org.chaostocosmos.metadata.metaphor.enums.META_EXT;
import org.chaostocosmos.metadata.metaphor.event.MetaEvent;
import org.chaostocosmos.metadata.metaphor.event.MetaListener;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;

/**
 * MetadataStorage
 * 
 * @author 9ins
 */
public class MetaStore {
    /**
     * Metadata file path
     */
    File metaFile;

    /**
     * Metadata Map object
     */
    Map<String, Object> metadata;    

    /**
     * Metadata manager object
     */
    MetaManager metaManager;

    /**
     * Metadata listener list
     */
    List<MetaListener> metaListeners;

    /**
     * Constructs with meta file
     * @param metaFile
     * @throws JsonProcessingException
     * @throws JsonMappingException
     * @throws FileNotFoundException
     */
    public MetaStore(Path metaFile) throws JsonMappingException, JsonProcessingException, FileNotFoundException {
        this(metaFile.toFile());
    }

    /**
     * Constructs with resource path
     * @param resourcePath
     * @throws URISyntaxException
     * @throws JsonMappingException
     * @throws JsonProcessingException
     * @throws FileNotFoundException
     */
    public MetaStore(String resourcePath) throws URISyntaxException, JsonMappingException, JsonProcessingException, FileNotFoundException {
        this(new File(ClassLoader.getSystemClassLoader().getResource(resourcePath).toURI()));
    }

    /**
     * Constructs with meta file path object
     * @param metaFile
     * @throws JsonProcessingException
     * @throws JsonMappingException
     * @throws FileNotFoundException
     */
    public MetaStore(File metaFile) throws JsonMappingException, JsonProcessingException, FileNotFoundException {
        if(!metaFile.exists()) {
            throw new FileNotFoundException("Specified file not found: "+metaFile.toString());
        } else if(metaFile.isDirectory()) {
            throw new IllegalArgumentException("Metadata file cannot be directory - "+metaFile.getAbsolutePath());
        } else if(!metaFile.exists()) {
            throw new IllegalArgumentException("Metadata file not exist - "+metaFile.getName());
        }
        this.metaFile = metaFile;
        this.metaListeners = new ArrayList<>();
        this.metadata = load(metaFile);
    }

    /**
     * Load metadata from file
     * @throws JsonProcessingException
     * @throws JsonMappingException
     * @throws NotSupportedException
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> load(File metaStream) throws JsonMappingException, JsonProcessingException {
        return MetaHelper.load(metaStream);
    }

    /**
     * Reload metadata
     * @throws JsonProcessingException
     * @throws JsonMappingException
     */
    public void reload() throws JsonMappingException, JsonProcessingException {
        load(this.metaFile);
    }

    /**
     * Save metadata to file
     * @param metaFile
     * @param metadata
     * @throws IOException
     */
    public <T> void save(File metaFile, T metadata) throws IOException {
        MetaHelper.save(metaFile, metadata);
    }

    /**
     * Get metadata file
     * @return
     */
    public File getMetaFile() {
        return this.metaFile;
    }
    
    /**
     * Get value by expression
     * @param <V> value type
     * @param expr metadata path expression
     * @return
     */
    public <V> V getValue(String expr) {
        V value = MetaStructureOpr.<V> getValue(this.metadata, expr);
        if(value != null) {
            return value;
        } else {
            throw new IllegalArgumentException("There isn't exist value on specified expression: "+expr);
        }
    }

    /**
     * Set value on specified position
     * @param <V>
     * @param expr
     * @param value
     */
    public <V> void setValue(String expr, V value) {
        MetaStructureOpr.<V> setValue(this.metadata, expr, value);
        dispatchMetaEvent(EVENT_TYPE.MODIFIED, new MetaEvent<V> (this, this.metaFile, expr, value));
    }

    /**
     * Add metadata value placed on expression
     * @param expr
     * @param value
     */
    @SuppressWarnings("unchecked")
    public <V> void addValue(String expr, V value) {
        Object parent = MetaStructureOpr.<V> getValue(this.metadata, expr.substring(0, expr.lastIndexOf(".")));
        if(parent == null) {
            throw new IllegalArgumentException("Specified expression's parent is not exist: "+expr);
        } else if(parent instanceof List) {
            ((List<Object>) parent).add(value);
        } else if(parent instanceof Map) {
            ((Map<String, Object>) parent).put(expr.substring(expr.lastIndexOf(".")+1), value);
        } else {
            throw new RuntimeException("Parent  type is wrong. Metadata structure failed: "+parent);
        }
        dispatchMetaEvent(EVENT_TYPE.CREATED, new MetaEvent<V> (this, this.metaFile, expr, value));
    }

    /**
     * Remove metadata value with the expression
     * @param expr
     */
    @SuppressWarnings("unchecked")
    public <V> V removeValue(String expr) {
        Object parent = MetaStructureOpr.<V> getValue(this.metadata, expr.substring(0, expr.lastIndexOf(".")));
        V value = null;
        if(parent == null) {
            throw new IllegalArgumentException("Specified expression's parent is not exist: "+expr);
        } else if(parent instanceof List) {
            int idx = Integer.valueOf(expr.substring(expr.lastIndexOf(".")+1));
            value = (V) ((List<Object>) parent).remove(idx);
        } else if(parent instanceof Map) {
            value = (V) ((Map<String, Object>) parent).remove(expr.substring(expr.lastIndexOf(".")+1));
        } else {
            throw new RuntimeException("Parent data type is wired. Context data structure failed: "+parent);
        }
        dispatchMetaEvent(EVENT_TYPE.REMOVED, new MetaEvent<V> (this, this.metaFile, expr, value));
        return value;
    }

    /**
     * Exists context value by specified expression
     * @param expr
     */
    public boolean exists(String expr) {
        Object value = MetaStructureOpr.getValue(this.metadata, expr);
        if(value != null) {
            return true;
        } else {
            return false;
        }
    }    

    /**
     * Get metadata Map
     * @return
     */
    public Map<String, Object> getMetadata() {
        return this.metadata;
    }

    /**
     * Get metadata manager
     * @return
     */
    public MetaManager getMetaManager() {
        return this.metaManager;
    }

    /**
     * Set metadata manager
     * @param metaManager
     */
    public void setMetaManager(MetaManager metaManager) {
        this.metaManager = metaManager;
    }

    /**
     * Add metadata listener
     * @param metaListener
     */
    public void addMetaListener(MetaListener metaListener) {
        this.metaListeners.add(metaListener);
    }

    /**
     * Remove metadata listener
     * @param metaListener
     */
    public void removeMetaListener(MetaListener metaListener) {
        this.metaListeners.remove(metaListener);
    }

    /**
     * Dispatch metadata event
     * @param <T>
     * @param eventType
     * @param me
     */
    public synchronized <T> void dispatchMetaEvent(EVENT_TYPE eventType, MetaEvent<T> me) {
        if(this.metaListeners.size() > 0) {
            switch(eventType) {
                case INJECTED :
                this.metaListeners.stream().forEach(l -> l.metadataInjected(me));                
                break;
                case CREATED :
                this.metaListeners.stream().forEach(l -> l.metadataCreated(me));
                break;
                case REMOVED :
                this.metaListeners.stream().forEach(l -> l.metadataRemoved(me));
                break;
                case MODIFIED :
                this.metaListeners.stream().forEach(l -> l.metadataModified(me));
                break;
            }
        }
    }

    @Override
    public String toString() {
        return this.metaFile.toString()+"\n"+metadata.toString();
    }    
}
