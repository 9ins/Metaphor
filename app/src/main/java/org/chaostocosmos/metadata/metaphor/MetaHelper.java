package org.chaostocosmos.metadata.metaphor;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Metadata helper object
 * 
 * @author 9ins
 */
public class MetaHelper {
    /**
     * Scan all meta data instances
     * @param metaStoreFile
     * @param classpaths
     * @return
     */
    public static List<Object> scanAnnotatedObject(File metaStoreFile, Path[] classpaths) {
        return ClassUtils.findClasses(classpaths).stream().map(clazz -> get(metaStoreFile, clazz)).filter(o -> o != null).collect(Collectors.toList());
    }

    /**
     * Scan all meta data instances
     * @param metaStoreFile
     * @param classpaths
     * @return
     */
    public static Map<String, Object> scanAnnotatedObjectWithNames(File metaStoreFile, Path[] classpaths) {
        return scanAnnotatedObject(metaStoreFile, classpaths).stream().map(o -> new Object[] {o.getClass().getName(), o}).collect(Collectors.toMap(k -> (String)k[0], v -> v));
    }

    /**
     * Get metadata of meta file
     * @param metaStoreFile
     * @return
     */
    public static Map<String, Object> getMetaMap(File metaStoreFile) {
        return getMetaStore(metaStoreFile).getMetadata();
    }
    
    /**
     * Get MetaStore object
     * @param metaStoreFile
     * @return
     */
    public static MetaStore getMetaStore(File metaStoreFile) {
        return MetaManager.get(metaStoreFile.toPath().getParent()).getMetaStore(metaStoreFile.toPath());
    }

    /**
     * Get meta data instance
     * @param <T>
     * @param clazz
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public static <T> T get(File metaStoreFile, Class<T> clazz) {
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
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static <T> T get(MetaStore metaStore, Class<T> clazz) {
        return get(metaStore, clazz, MetaField.class);
    }

    /**
     * Get meta data instance
     * @param <T>
     * @param metaStore
     * @param clazz
     * @param annotation
     * @return
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(MetaStore metaStore, Class<T> clazz, Class<? extends Annotation> annotation) {        
        try {
            if(isAnnotatedClass(clazz, annotation)) {
                return (T) MetaInjector.inject(metaStore, ClassUtils.newInstance(clazz.getName()), annotation);
            }
            return null;
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
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
}
