package org.chaostocosmos.metadata.metaphor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.chaostocosmos.metadata.metaphor.annotation.MetaWired;
import org.chaostocosmos.metadata.metaphor.enums.META_EXT;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;

/**
 * Metadata helper object
 * 
 * @author 9ins
 */
public class MetaHelper {
    /**
     * Jackson object mapper
     */
    private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Scan all meta data instances
     * @param metaStoreFile
     * @param classpaths
     * @return
     * @throws JsonProcessingException
     * @throws JsonMappingException
     */
    public static List<Object> scanAnnotatedObject(File metaStoreFile, Path[] classpaths) throws JsonMappingException, JsonProcessingException {
        List<Class<?>> classes = ClassUtils.findClasses(classpaths);
        List<Object> list = new ArrayList<>();
        for(Class<?> clazz : classes) {
            list.add(get(metaStoreFile, clazz));
        }
        return list;
    }

    /**
     * Scan all meta data instances
     * @param metaStoreFile
     * @param classpaths
     * @return
     * @throws JsonProcessingException
     * @throws JsonMappingException
     */
    public static Map<String, Object> scanAnnotatedObjectWithNames(File metaStoreFile, Path[] classpaths) throws JsonMappingException, JsonProcessingException {
        List<Object> list = scanAnnotatedObject(metaStoreFile, classpaths);
        Map<String, Object> map = new HashMap<>();
        for(Object obj : list) {
            map.put(obj.getClass().getName(), obj);
        }
        return map;
    }

    /**
     * Get metadata of meta file
     * @param metaStoreFile
     * @return
     * @throws JsonProcessingException
     * @throws JsonMappingException
     */
    public static Map<String, Object> getMetaMap(File metaStoreFile) throws JsonMappingException, JsonProcessingException {
        return getMetaStore(metaStoreFile).getMetadata();
    }
    
    /**
     * Get MetaStore object
     * @param metaStoreFile
     * @return
     * @throws JsonProcessingException
     * @throws JsonMappingException
     */
    public static MetaStore getMetaStore(File metaStoreFile) throws JsonMappingException, JsonProcessingException {
        return MetaManager.get(metaStoreFile.toPath().getParent()).getMetaStore(metaStoreFile.toPath());
    }

    /**
     * Get meta data instance
     * @param <T>
     * @param clazz
     * @return
     * @throws JsonProcessingException
     * @throws JsonMappingException
     */
    public static <T> T get(File metaStoreFile, Class<T> clazz) throws JsonMappingException, JsonProcessingException {
        if(!metaStoreFile.exists() || metaStoreFile.isDirectory()) {
            throw new IllegalArgumentException("Metadata file not exists or might directory. Metadata file should be File!!!");
        }
        return get(MetaManager.get(metaStoreFile.toPath().getParent()).getMetaStore(metaStoreFile.getName()), clazz);
    }

    /**
     * Get meta data instance
     * @param <T>
     * @param metaStore
     * @param clazz
     * @return
     */
    public static <T> T get(MetaStore metaStore, Class<T> clazz) {
        return get(metaStore, clazz, MetaWired.class);
    }

    /**
     * Get meta data instance
     * @param <T>
     * @param metaStore
     * @param clazz
     * @param annotation
     * @return
     */
    public static <T> T get(MetaStore metaStore, Class<T> clazz, Class<? extends Annotation> annotation) {        
        return new MetaInjector<T>(ClassUtils.<T> newInstance(clazz.getName())).inject(metaStore);
    }

    /**
     * Invoke meta specified method
     * @param metaStore
     * @param object
     * @param methodName
     * @param exprs
     * @return
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public static Object invokeMetaMethod(MetaStore metaStore, Object object, String methodName, String[] exprs) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method method = object.getClass().getDeclaredMethod(methodName);
        Object[] params = Arrays.asList(exprs).stream().map(expr -> metaStore.getValue(expr)).toArray();
        return method.invoke(object, params);
    }

    /**
     * Whether specified annotation is on field in class
     * @param clazz
     * @param annotation
     * @return
     */
    public static boolean isAnnotatedClass(Class<?> clazz, final Class<? extends Annotation> annotation) {
        return Arrays.asList(clazz.getDeclaredFields()).stream().map(field -> field.getAnnotation(annotation)).filter(a -> a != null).count() > 0;
    }

    /**
     * Load metadata from file
     * @throws JsonProcessingException
     * @throws JsonMappingException
     * @throws NotSupportedException
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public static synchronized Map<String, Object> load(File metaStream) throws JsonMappingException, JsonProcessingException {
        String metaName = metaStream.getName();
        String metaExt = metaName.substring(metaName.lastIndexOf(".")+1);
        String metaString;
        try {
            metaString = Files.readString(metaStream.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(metaExt.equalsIgnoreCase(META_EXT.YAML.name()) || metaExt.equalsIgnoreCase(META_EXT.YML.name())) {
            return new Yaml().<Map<String, Object>> load(metaString);
        } else if(metaExt.equalsIgnoreCase(META_EXT.JSON.name())) {            
            return objectMapper.readValue(metaString, Map.class);
        } else if(metaExt.equalsIgnoreCase(META_EXT.PROPERTIES.name()) || metaExt.equalsIgnoreCase(META_EXT.CONFIG.name()) || metaExt.equalsIgnoreCase(META_EXT.CONF.name())) {
            return Arrays.asList(metaString.split(System.lineSeparator()))
                         .stream().map(l -> new Object[]{l.substring(0, l.indexOf("=")).trim(), l.substring(l.indexOf("=")+1).trim()})
                         .collect(Collectors.toMap(k -> (String)k[0], v -> v[1]));
        } else {
            throw new IllegalArgumentException("Metadata file extention not supported: "+metaName);
        }
    }

    /**
     * Save metadata to file
     * @param metaFile
     * @param metadata
     * @throws IOException
     */
    public static synchronized <T> void save(File metaFile, T metadata) throws IOException {
        String metaName = metaFile.getName();
        String metaExt = metaName.substring(metaName.lastIndexOf(".")+1);
        try(FileWriter writer = new FileWriter(metaFile)) {
            if(metaExt.equalsIgnoreCase(META_EXT.YAML.name()) || metaExt.equalsIgnoreCase(META_EXT.YML.name())) {
                new Yaml().dump(metadata, writer);
            } else if(metaExt.equalsIgnoreCase(META_EXT.JSON.name())) {
                objectMapper.writeValue(writer, metadata);
            } else if(metaExt.equalsIgnoreCase(META_EXT.PROPERTIES.name()) || metaExt.equalsIgnoreCase(META_EXT.CONFIG.name()) || metaExt.equalsIgnoreCase(META_EXT.CONF.name())) {
                JavaPropsMapper mapper = new JavaPropsMapper();
                Properties properties = mapper.writeValueAsProperties(metadata);
                mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
                properties.store(writer, null);
            } else {
                throw new IllegalArgumentException("Metadata file extention not supported: "+metaName);
            }
        }
    }    
}


