package org.chaostocosmos.metadata.metaphor;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.chaostocosmos.metadata.metaphor.annotation.MetaWired;

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
        if(isAnnotatedClass(clazz, annotation)) {
            return new MetaInjector<T>(ClassUtils.<T> newInstance(clazz.getName())).inject(metaStore);
        }
        return null;
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
}
