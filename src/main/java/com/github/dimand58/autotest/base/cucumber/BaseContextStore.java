package com.github.dimand58.autotest.base.cucumber;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseContextStore {

  private static final ThreadLocal<Map<String, Map<String, Object>>> CONTEXT_MAP
      = ThreadLocal.withInitial(LinkedHashMap::new);

  public static <T> void put(ContextType<T> type, String context, T object) {
    getContextsByType(type).remove(context);
    getContextsByType(type).put(context, object);
  }

  public static <T> void putList(ContextType<T> type, String context, List<T> object) {
    getContextsByType(type).remove(context);
    getContextsByType(type).put(context, object);
  }

  public static <T> T get(ContextType<T> type, String context) {
    return (T) getContextsByType(type).get(context);
  }

  public static <T> List<T> getList(ContextType<T> type, String context) {
    return (List<T>) getContextsByType(type).get(context);
  }

  public static <T> T getLast(ContextType<T> type) {
    Map<String, Object> values = getContextsByType(type);
    return (T) values.values().toArray()[values.values().size() - 1];
  }

  public static <T> List<T> getLastList(ContextType<T> type) {
    Map<String, Object> values = getContextsByType(type);
    return (List<T>) values.values().toArray()[values.values().size() - 1];
  }

  public static <T> Object getFieldOf(ContextType<T> type, String context, String fieldName) {
    Object val = get(type, context);
    try {
      Field field = val.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      return field.get(val);
    } catch (IllegalAccessException | NoSuchFieldException ex) {
      log.error(ex.getMessage(), ex);
      throw new RuntimeException();
    }
  }

  public static void clean() {
    CONTEXT_MAP.remove();
  }

  private static <T> Map<String, Object> getContextsByType(ContextType<T> type) {
    CONTEXT_MAP.get().computeIfAbsent(type.getEntityName(), k -> new LinkedHashMap<>());
    return CONTEXT_MAP.get().get(type.getEntityName());
  }
}
