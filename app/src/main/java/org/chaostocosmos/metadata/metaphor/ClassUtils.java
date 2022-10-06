package org.chaostocosmos.metadata.metaphor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class Utility
 * 
 * @author 9ins
 */
public class ClassUtils {
    /**
     * Get generic class name first element of field
     * @param field
     * @return
     */
    public static String getFirstGenericClassName(Field field) {
        return getGenericClassName(field, 0);
    }

    /**
     * Get generic class name of field with argument index
     * @param field
     * @param argsIdx
     * @return
     */
    public static String getGenericClassName(Field field, int argsIdx) {
        return getGenericClassName(field).get(argsIdx);
    }

    /**
     * Get generic class name of Field
     * @param field
     * @return
     */
    public static List<String> getGenericClassName(Field field) {
        ParameterizedType pType = (ParameterizedType) field.getGenericType();
        return Arrays.asList(pType.getActualTypeArguments()).stream().map(t -> ((Class<?>) t).getName()).collect(Collectors.toList());
    }

    /**
     * Create instance of generic type in specified Field
     * @param <T>
     * @param field
     * @return
     */
    public static Object newGenericInstance(Field field) {
        return newInstance(getFirstGenericClassName(field));
    }

    /**
     * Create instance of index of generic type in specified Field
     * @param field
     * @param argsIdx
     * @return
     */
    public static Object newGenericInstance(Field field, int argsIdx) {
        return newInstance(getGenericClassName(field, argsIdx));
    }

    /**
     * Create instance with specified class name
     * @param <T>
     * @param classname
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(String classname) {
        try {
            Class<?> clazz = Class.forName(classname);
            Constructor<?> constructor = clazz.getConstructor(new Class<?>[0]);
            return (T) constructor.newInstance(new Object[0]);
        } catch (ClassNotFoundException 
               | NoSuchMethodException 
               | SecurityException 
               | InstantiationException 
               | IllegalAccessException 
               | IllegalArgumentException 
               | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Find classes on specified Path in array
     * @param classpaths
     * @return
     */
    public static List<Class<?>> findClasses(Path[] classpaths) {
        return findClasses(Arrays.asList(classpaths).stream().map(path -> {
            try {
                return path.toUri().toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }).toArray(URL[]::new));
    }
    
    /**
     * Scan classes with specified URL array
     * @param urls
     * @return
     */
    public static List<Class<?>> findClasses(URL[] urls) {
        return Arrays.asList(urls).stream().flatMap(url -> findClassnames(url).stream()).map(s -> {
            try {
                return Class.forName(s);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    /**
     * Get class names from URL
     * @param url
     * @param filters
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public static List<String> findClassnames(URL url) {
        List<String> classes = null;
        String protocol = url.getProtocol();
        try {
            if(protocol.equals("file")) {
                return classes = Files.walk(Paths.get(url.toURI()))
                               .filter(p -> !Files.isDirectory(p) && p.toString().endsWith(".class"))
                               .map(p -> p.toString().substring(new File(url.getFile()).getAbsolutePath().length()+1).replace(File.separator, "."))
                               .map(c -> c.substring(0, c.lastIndexOf(".")))
                               .collect(Collectors.toList());
            } else if(protocol.equals("jar")) {
                try(FileSystem filesystem = FileSystems.newFileSystem(url.toURI(), new HashMap<>())) {
                    return classes = Files.walk(filesystem.getPath(""))
                                .filter(p -> !Files.isDirectory(p) && p.toString().endsWith(".class"))
                                .map(p -> p.toString().replace(File.separator, "."))
                                .map(c -> c.substring(0, c.lastIndexOf(".")))
                                .collect(Collectors.toList());
                }
            } else {
                throw new IllegalArgumentException("Protocol not collect - "+protocol+"  Allowed file of jar.");
            }     
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }        
}
