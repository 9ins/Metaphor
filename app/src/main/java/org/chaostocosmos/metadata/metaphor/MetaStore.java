package org.chaostocosmos.metadata.metaphor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.chaostocosmos.metadata.metaphor.enums.EVENT_TYPE;
import org.chaostocosmos.metadata.metaphor.enums.META_EXT;
import org.chaostocosmos.metadata.metaphor.event.MetaEvent;
import org.yaml.snakeyaml.Yaml;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
     * Constructs with meta path string
     * @param metaFile
     */
    public MetaStore(String metaFile) {
        this(Paths.get(metaFile));
    }

    /**
     * Constructs with meta file
     * @param metaFile
     */
    public MetaStore(Path metaFile) {
        this(metaFile.toFile());
    }

    /**
     * Constructs with meta file path object
     * @param metaFile
     */
    public MetaStore(File metaFile) {
        if(metaFile.isDirectory()) {
            throw new IllegalArgumentException("Metadata file cannot be directory - "+metaFile.getAbsolutePath());
        } else if(!metaFile.exists()) {
            throw new IllegalArgumentException("Metadata file not exist - "+metaFile.getName());
        }
        this.metaFile = metaFile;        
        this.metadata = load(metaFile);
    }

    /**
     * Load metadata from file
     * @throws NotSupportedException
     * @throws IOException
     */
    private synchronized Map<String, Object> load(File metaFile) {
        String metaName = metaFile.getName();
        String metaExt = metaName.substring(metaName.lastIndexOf(".")+1);
        String metaString;
        try {
            metaString = Files.readString(metaFile.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(metaExt.equalsIgnoreCase(META_EXT.YAML.name()) || metaExt.equalsIgnoreCase(META_EXT.YML.name())) {
            return new Yaml().<Map<String, Object>> load(metaString);
        } else if(metaExt.equalsIgnoreCase(META_EXT.JSON.name())) {
            return new Gson().<Map<String, Object>> fromJson(metaString, Map.class);
        } else if(metaExt.equalsIgnoreCase(META_EXT.PROPERTIES.name()) || metaExt.equalsIgnoreCase(META_EXT.CONFIG.name()) || metaExt.equalsIgnoreCase(META_EXT.CONF.name())) {
            return Arrays.asList(metaString.split(System.lineSeparator()))
                         .stream().map(l -> new Object[]{l.substring(0, l.indexOf("=")).trim(), l.substring(l.indexOf("=")+1).trim()})
                         .collect(Collectors.toMap(k -> (String)k[0], v -> v[1]));
        } else {
            throw new IllegalArgumentException("Metadata file extention not supported: "+metaName);
        }
    }

    /**
     * Reload metadata
     */
    public void reload() {
        load(this.metaFile);
    }

    /**
     * Save metadata to file
     * @param metaFile
     * @throws IOException
     */
    public void save(File metaFile) throws IOException {
        String metaName = metaFile.getName();
        String metaExt = metaName.substring(metaName.lastIndexOf(".")+1);
        try(FileWriter writer = new FileWriter(metaFile)) {
            if(metaExt.equalsIgnoreCase(META_EXT.YAML.name()) || metaExt.equalsIgnoreCase(META_EXT.YML.name())) {
                new Yaml().dump(this.metadata, writer);
            } else if(metaExt.equalsIgnoreCase(META_EXT.JSON.name())) {                
                new GsonBuilder().setPrettyPrinting().create().toJson(this.metadata, writer);
            } else if(metaExt.equalsIgnoreCase(META_EXT.PROPERTIES.name()) || metaExt.equalsIgnoreCase(META_EXT.CONFIG.name()) || metaExt.equalsIgnoreCase(META_EXT.CONF.name())) {
                Properties properties = new Properties();
                properties.putAll(this.metadata);
                properties.store(writer, null);
            } else {
                throw new IllegalArgumentException("Metadata file extention not supported: "+metaName);
            }
        }
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
        this.metaManager.dispatchMetaEvent(EVENT_TYPE.MODIFIED, new MetaEvent<V> (this, this.metaFile, expr, value));
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
        this.metaManager.dispatchMetaEvent(EVENT_TYPE.CREATED, new MetaEvent<V> (this, this.metaFile, expr, value));
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
        this.metaManager.dispatchMetaEvent(EVENT_TYPE.REMOVED, new MetaEvent<V> (this, this.metaFile, expr, value));
        return value;
    }

    /**
     * Exists context value by specified expression
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

    @Override
    public String toString() {
        return this.metaFile.toString()+"\n"+metadata.toString();
    }    
}
