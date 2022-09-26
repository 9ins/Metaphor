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

/**
 * MetadataInjector
 * 
 * @author 9ins
 */
public class MetaInjector {
    /**
     * Inject meta data to object field
     * @param <T>
     * @param metadata
     * @param obj
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    public static <T> T injectMetaField(MetaStore metadata, T obj) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        return inject(metadata, obj, MetaField.class);
    }

    /**
     * Inject field value from MetaStore file mapping with specified annotation.
     * @param <T>
     * @param metadata
     * @param obj
     * @param metaAnnotation
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    @SuppressWarnings("unchecked")
    public static <T> T inject(MetaStore metadata, T obj, Class<? extends Annotation> metaAnnotation) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if(obj instanceof Field) {
            Field field = (Field) obj;
            Annotation annotation2 = field.getAnnotation(metaAnnotation);
            if(annotation2 == null) {
                return (T) (field.getType().isPrimitive() ? 0L : null);
            }
            String expr = (String) getAnnotationValue(annotation2, "expr");
            if(List.class.isAssignableFrom(field.getType())) {
                if(field.getGenericType() == null) {
                    return metadata.<T> getValue(expr);
                } else {
                    List list = new ArrayList<>();
                    List src = metadata.<List> getValue(expr);
                    for(int i=0; i<src.size(); i++) {
                        final int idx = i;
                        Object instance = ClassUtils.newGenericInstance(field);
                        Map<Field, String> fieldMap = Arrays.asList(instance.getClass().getDeclaredFields())
                                                            .stream()
                                                            .filter(f -> f.getAnnotation(metaAnnotation) != null)
                                                            .map(f -> new Object[] {f, changeAnnotationValue(f.getAnnotation(metaAnnotation), "expr", getAnnotationValue(f.getAnnotation(metaAnnotation), "expr").toString().replace("[i]", "["+idx+"]"))})
                                                            .collect(Collectors.toMap(k -> (Field) k[0], v -> (String) v[1]));
                        list.add(inject(metadata, instance, metaAnnotation));
                        fieldMap.entrySet()
                                .stream()
                                .map(e -> (String) changeAnnotationValue(e.getKey().getAnnotation(metaAnnotation), "expr", e.getValue()))
                                .collect(Collectors.toList());
                    }
                    return (T) list;
                }
            } else if(Map.class.isAssignableFrom(field.getType())) {
                Map map = metadata.<Map> getValue(expr);
                for(Object key : map.keySet()) {
                    Object instance = ClassUtils.getGenericClassName(field, 1);
                    if(instance.getClass().getAnnotation(metaAnnotation) == null) {
                        break;
                    } else {
                        map.put(key, inject(metadata, instance, metaAnnotation));
                    }
                }
                return (T) map;
            } else {
                return metadata.<T> getValue((String) expr);
            }
        } else {
            Field[] fields = obj.getClass().getDeclaredFields();
            for(Field field : fields) {
                field.setAccessible(true);
                field.set(obj, inject(metadata, field, metaAnnotation));
            }
            Method[] methods = obj.getClass().getDeclaredMethods();
            for(Method method : methods) {
                method.setAccessible(true);
                Annotation[][] annotations = method.getParameterAnnotations();
                List<Object> params = new ArrayList<>();
                for(int i=0; i<annotations.length; i++) {
                    Annotation[] anno = annotations[i];
                    if(anno.length > 0) {
                        Object value = metadata.<Object> getValue((String) getAnnotationValue(anno[0], "expr"));
                        if(method.getParameterTypes()[i].isAssignableFrom(value.getClass()) || method.getParameterTypes()[i].isPrimitive()) {
                            params.add(value);
                        } else {
                            throw new IllegalArgumentException("Metadata type is not matching with parameter type. Parameter type: "+method.getParameterTypes()[i].getName()+"   Metadata type: "+value.getClass().getName());
                        }
                    } else {
                        params.add(method.getParameterTypes()[i].isPrimitive() ? 0 : null);
                    }
                }
                Arrays.asList(params).stream().forEach(a -> System.out.println(a));
                System.out.println(method.getName());
                method.invoke(obj, params.toArray());
            }
            return obj;
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

