package org.chaostocosmos.metadata.metaphor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * MetadataInjector
 * 
 * @author 9ins
 */
public class MetaInjector {
    /**
     * MetaContainer
     */
    MetaStore metadata;

    /**
     * Constructs with metadata
     * @param metadata
     */
    public MetaInjector(MetaStore metadata) {
        this.metadata = metadata;
    }

    /**
     * Inject
     * @param <T>
     * @param obj
     * @param annotation
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    @SuppressWarnings("unchecked")
    public <T> T inject(T obj, Class<? extends Annotation> annotation) throws IllegalArgumentException, IllegalAccessException {
        if(obj instanceof Field) {
            Field field = (Field) obj;
            Annotation annotation2 = field.getAnnotation(annotation);
            String expr = (String) getValue(annotation2, "expr");
            //System.out.println("expr: "+expr);
            if(List.class.isAssignableFrom(field.getType())) {
                //System.out.println("field is List  "+field.getGenericType().getTypeName());
                if(field.getGenericType() == null) {
                    return this.metadata.<T> getValue(expr);
                } else {
                    List list = new ArrayList<>();
                    List src = this.metadata.<List> getValue(expr);
                    for(int i=0; i<src.size(); i++) {
                        final int idx = i;
                        Object instance = newInstance(getFirstGenericClassName(field));
                        Map<Field, String> fieldMap = Arrays.asList(instance.getClass().getDeclaredFields()).stream().map(f -> new Object[] {f, changeAnnotationValue(f.getAnnotation(annotation), "expr", getValue(f.getAnnotation(annotation), "expr").toString().replace("[i]", "["+idx+"]"))}).collect(Collectors.toMap(k -> (Field) k[0], v -> (String) v[1]));
                        list.add(inject(instance, annotation));
                        fieldMap.entrySet().stream().map(e -> (String) changeAnnotationValue(e.getKey().getAnnotation(annotation), "expr", e.getValue())).collect(Collectors.toList());
                    }
                    return (T) list;
                }
            } else if(Map.class.isAssignableFrom(field.getType())) {
                //System.out.println("field is Map - "+field.getGenericType());
                Map map = this.metadata.<Map> getValue(expr);
                for(Object key : map.keySet()) {
                    Object instance = newInstance(getGenericClassName(field, 1));
                    if(instance.getClass().getAnnotation(annotation) == null) {
                        break;
                    } else {
                        map.put(key, inject(instance, annotation));
                    }
                }
                return (T) map;
            } else {
                //System.out.println("primitive or non-generic");                
                return this.metadata.<T> getValue((String) expr);
            }
        } else if(obj instanceof Method) {
            //Not implemented yet.
        } else {
            //System.out.println("it's Object");
            Field[] fields = obj.getClass().getDeclaredFields();
            for(Field field : fields) {
                field.setAccessible(true);
                field.set(obj, inject(field, annotation));
            }
            return obj;
        }
        throw new IllegalArgumentException("Not supported object type: "+obj.getClass().getName());
    }

    /**
     * Get generic class name first element of field
     * @param field
     * @return
     */
    public String getFirstGenericClassName(Field field) {
        return getGenericClassName(field, 0);
    }

    /**
     * Get generic class name of field with argument index
     * @param field
     * @param argsIdx
     * @return
     */
    public String getGenericClassName(Field field, int argsIdx) {
        return getGenericClassName(field).get(argsIdx);
    }

    /**
     * Get generic class name of Field
     * @param field
     * @return
     */
    public List<String> getGenericClassName(Field field) {
        ParameterizedType pType = (ParameterizedType) field.getGenericType();
        return Arrays.asList(pType.getActualTypeArguments()).stream().map(t -> ((Class<?>) t).getName()).collect(Collectors.toList());
    }

    /**
     * Create instance of generic type of specified Field
     * @param <T>
     * @param field
     * @return
     */
    public Object newGenericInstance(Field field) {
        return newInstance(getFirstGenericClassName(field));
    }

    /**
     * Create instance with specified class name
     * @param <T>
     * @param classname
     * @return
     */
    public Object newInstance(String classname) {
        Class<?> clazz;
        try {
            clazz = Class.forName(classname);
            Constructor<?> constructor = clazz.getConstructor(new Class<?>[0]);
            return constructor.newInstance(new Object[0]);
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get annotation value of key
     * @param annotation
     * @param key
     * @return
     */
    public Object getValue(Annotation annotation, String key) {
        Method method;
        try {
            method = annotation.getClass().getDeclaredMethod(key, new Class<?>[0]);
            return method.invoke(annotation, new Object[0]);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Change field annotation value
     * @param annotation
     * @param key
     * @param value
     * @return
     */
    @SuppressWarnings("unchecked")
    public Object changeAnnotationValue(Annotation annotation, String key, Object newValue) {
        Object handler = Proxy.getInvocationHandler(annotation);
        Field field;
        try {
            field = handler.getClass().getDeclaredField("memberValues");
            field.setAccessible(true);
            Map<String, Object> memberValues = (Map<String, Object>) field.get(handler);
            Object oldValue = memberValues.get(key);
            if (oldValue == null || oldValue.getClass() != newValue.getClass()) {
                throw new IllegalArgumentException("Something wrong in value.");
            }
            memberValues.put(key, newValue);
            return oldValue;
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }    
}
