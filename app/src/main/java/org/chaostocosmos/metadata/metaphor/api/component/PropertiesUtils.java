package org.chaostocosmos.metadata.metaphor.api.component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

/**
 * Utility object
 * 
 * @author Kooin-Shin
 */
@Component
public class PropertiesUtils {

    @Autowired
    LoggerAspect logger;

    /**
     * Object mapper
     */
    ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Mime type Map
     */
    Map<String, Object> mimeMap;

    @PostConstruct
    private void loadMime() throws IOException {
        this.mimeMap = loadYamlResource("mime.yaml");
    }

    /**
     * Get MIME type
     * @param mimeType
     * @return
     */
    public Object getMimeType(String mimeType) {
        return mimeMap.get(mimeType);
    }

    /**
     * Convert to YAML format to Property format
     * @param prefix
     * @param structuredMap
     * @param properties
     */
    public void convertMapToProperties(String prefix, Map<String, Object> structuredMap, Map<String, Object> properties) {
        for (Map.Entry<String, Object> entry : structuredMap.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : entry.getKey().startsWith(prefix) ? entry.getKey().substring(entry.getKey().indexOf(".")+1) : entry.getKey() ;
            Object value = entry.getValue();
            if (value instanceof Map) {
                convertMapToProperties(key, (Map<String, Object>) value, properties);
            } else if (value instanceof Iterable) {
                int index = 0;
                for (Object item : (Iterable<?>) value) {
                    convertMapToProperties(key + "[" + index + "]", Map.of("value", item), properties);
                    index++;
                }
            } else {
                properties.put(key, value+"");
            }
        }
    }

    /**
     * Load Yaml resource by resource path
     * @param resourcePath
     * @return
     * @throws IOException
     */
    public Map<String, Object> loadYamlResource(String resourcePath) throws IOException {        
        return loadYamlResource(new ClassPathResource(resourcePath));
    }

    /**
     * Load Yaml resource by ClassPathResource
     * @param resource
     * @return
     * @throws IOException
     */
    public Map<String, Object> loadYamlResource(ClassPathResource resource) throws IOException {                
        List<PropertySource<?>> props = new YamlPropertySourceLoader().load(resource.getFilename(), resource);        
        if(props.size() > 0) {
            return (Map<String, Object>) props.get(0).getSource();
        }
        throw new RuntimeException("Specified resource is not found: "+resource.toString());
    }    

    /**
     * Load Json resource by resource path
     * @param resourcePath
     * @return
     * @throws StreamReadException
     * @throws DatabindException
     * @throws IOException
     */
    public Map<String, Object> loadJsonResource(String resourcePath) throws StreamReadException, DatabindException, IOException {
        return loadJsonResource(new ClassPathResource(resourcePath));
    }

    /**
     * Load Json resource by ClassPathResource
     * @param resource
     * @return
     * @throws StreamReadException
     * @throws DatabindException
     * @throws IOException
     */
    public Map<String, Object> loadJsonResource(ClassPathResource resource) throws StreamReadException, DatabindException, IOException {
        return objectMapper.readValue(resource.getInputStream(), new TypeReference<Map<String, Object>>(){});
    }

    /**
     * Load Properties resource by resource path
     * @param resource
     * @return
     */
    public Map<String, Object> loadPropertiesResource(String resourcePath) {
        return loadPropertiesResource(resourcePath);
    }

    /**
     * Load properties resource by ClassPathResource
     * @param resource
     * @return
     * @throws IOException
     */
    public Map<String, Object> loadPropertiesResource(ClassPathResource resource) throws IOException {
        List<PropertySource<?>> props = new PropertiesPropertySourceLoader().load(resource.getFilename(), resource);
        if(props.size() > 0) {
            return (Map<String, Object>) props.get(0).getSource();
        }
        throw new RuntimeException("Specified resource is not found: "+resource.toString());
    }
}
