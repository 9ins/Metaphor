package org.chaostocosmos.metadata.metaphor;

import java.io.Console;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.chaostocosmos.metadata.metaphor.annotation.MetaWired;
import org.chaostocosmos.metadata.metaphor.enums.EVENT_TYPE;
import org.chaostocosmos.metadata.metaphor.event.MetaEvent;

/**
 * MetadataInjector
 * 
 * @author 9ins
 */
public class MetaInjector <T> {
    /**
     * Object to be injected
     */
    T obj;

    /**
     * Constructs with object being injected.
     * @param obj
     */
    public MetaInjector(T obj) {
        this.obj = obj;
    }    

    /**
     * Inject meta data to object
     * @param metaStore
     * @return
     */
    public T inject(MetaStore metaStore) {
        return inject(metaStore, MetaWired.class);
    }

    /**
     * Inject meta data to object
     * @param metaStore
     * @return
     */
    public T inject(MetaStore metaStore, Class<? extends MetaWired> metaAnnotation) {
        try {
            T obj = inject(metaStore, this.obj, metaAnnotation);
            return obj;
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Inject field value from MetaStore file mapping with specified annotation.
     * 
     * @param <T>
     * @param metaStore
     * @param obj
     * @param metaAnnotation
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    @SuppressWarnings("unchecked")
    private T inject(MetaStore metaStore, Object obj, Class<? extends MetaWired> metaAnnotation) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if(obj instanceof Field) {
            Field field = (Field) obj;
            Annotation annotation2 = field.getAnnotation(metaAnnotation);
            if(annotation2 == null) {
                MetaWired meta = field.getDeclaringClass().getAnnotation(metaAnnotation);
                try {
                    return metaStore.<T> getValue(meta.expr()+"."+field.getName());
                } catch(Exception e) {
                    return (T) (field.getType().isPrimitive() ? 0 : null);
                }
            }
            String expr = (String) getAnnotationValue(annotation2, "expr");            
            if(List.class.isAssignableFrom(field.getType())) {
                if(field.getGenericType() == null) {
                    T value = metaStore.<T> getValue(expr);
                    metaStore.dispatchMetaEvent(EVENT_TYPE.INJECTED, new MetaEvent<T> (this, metaStore.getMetaFile(), expr, (T) value));
                    return value;
                } else {
                    List list = new ArrayList<>();
                    List src = metaStore.<List> getValue(expr);
                    for(int i=0; i<src.size(); i++) {
                        final int idx = i;
                        Object instance = ClassUtils.newGenericInstance(field);
                        Map<Field, String> fieldMap = Arrays.asList(instance.getClass().getDeclaredFields())
                                                            .stream()
                                                            .filter(f -> f.getAnnotation(metaAnnotation) != null)
                                                            .map(f -> new Object[] {f, changeAnnotationValue(f.getAnnotation(metaAnnotation), "expr", getAnnotationValue(f.getAnnotation(metaAnnotation), "expr").toString().replace("[i]", "["+idx+"]"))})
                                                            .collect(Collectors.toMap(k -> (Field) k[0], v -> (String) v[1]));
                        list.add(inject(metaStore, instance, metaAnnotation));
                        fieldMap.entrySet()
                                .stream()
                                .map(e -> (String) changeAnnotationValue(e.getKey().getAnnotation(metaAnnotation), "expr", e.getValue()))
                                .collect(Collectors.toList());
                    }
                    metaStore.dispatchMetaEvent(EVENT_TYPE.INJECTED, new MetaEvent<T> (this, metaStore.getMetaFile(), expr, (T) list));
                    return (T) list;
                }
            } else if(Map.class.isAssignableFrom(field.getType())) {
                Map map = metaStore.<Map> getValue(expr);
                for(Object key : map.keySet()) {
                    Object instance = ClassUtils.getGenericClassName(field, 1);
                    if(instance.getClass().getAnnotation(metaAnnotation) == null) {
                        break;
                    } else {
                        map.put(key, inject(metaStore, instance, metaAnnotation));
                    }
                }
                metaStore.dispatchMetaEvent(EVENT_TYPE.INJECTED, new MetaEvent<T> (this, metaStore.getMetaFile(), expr, (T) map));
                return (T) map;
            } else {
                T value = metaStore.<T> getValue((String) expr);
                metaStore.dispatchMetaEvent(EVENT_TYPE.INJECTED, new MetaEvent<T> (this, metaStore.getMetaFile(), expr, (T) value));
                return value;
            }
        } else {
            Field[] fields = obj.getClass().getDeclaredFields();
            for(Field field : fields) {
                field.setAccessible(true);                
                T value = inject(metaStore, field, metaAnnotation);
                field.set(obj, value);
            }
            Method[] methods = obj.getClass().getDeclaredMethods();
            for(Method method : methods) {                
                method.setAccessible(true);                
                if(Arrays.asList(method.getParameters()).stream().anyMatch(p -> p.getAnnotation(MetaWired.class) != null)) {
                    List<Object> params = Arrays.asList(method.getParameters())
                    .stream()
                    .map(p -> {
                        MetaWired a = p.getAnnotation(metaAnnotation);
                        if(a != null) {
                            Object value = metaStore.<Object> getValue((String) getAnnotationValue(a, "expr"));
                            metaStore.dispatchMetaEvent(EVENT_TYPE.INJECTED, new MetaEvent<T> (this, metaStore.getMetaFile(), a.expr(), (T) value));
                            if(p.getType().isAssignableFrom(value.getClass()) || p.getType().isPrimitive()) {                                                                                                        
                                return value;
                            } else {
                                throw new IllegalArgumentException("Metadata type is not matching with parameter type. Parameter type: "+p.getType().getName()+"   Metadata type: "+value.getClass().getName());
                            }
                        } else {
                            MetaWired ma = method.getAnnotation(metaAnnotation);
                            return p.getType().isPrimitive() ? 0 : null;   
                        }
                    }).collect(Collectors.toList());
                    method.invoke(obj, params.toArray());
                }
            }            
            return (T) obj;
        }
    }

    /**
     * Get annotation value of key
     * @param annotation
     * @param key
     * @return
     */
    public static Object getAnnotationValue(Annotation annotation, String key) {
        try {
            Method method = annotation.getClass().getDeclaredMethod(key, new Class<?>[0]);
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
    public static Object changeAnnotationValue(Annotation annotation, String key, Object newValue) {
        Object handler = Proxy.getInvocationHandler(annotation);
        try {
            if (!MetaInjector.class.getModule().isNamed()) {
                Console.class.getModule().addOpens(Console.class.getPackageName(), MetaInjector.class.getModule());
            }
            Field field = handler.getClass().getDeclaredField("memberValues");
            //field.setAccessible(true);
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

