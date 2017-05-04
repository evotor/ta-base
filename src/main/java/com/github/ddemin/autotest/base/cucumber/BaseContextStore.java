package com.github.ddemin.autotest.base.cucumber;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseContextStore {

  private static final ThreadLocal<Map<String, Map<String, Object>>> CONTEXT_MAP
      = ThreadLocal.withInitial(ConcurrentHashMap::new);

  public static <T> void put(ContextType<T> type, String context, T object) {
    if (type == null || context == null) {
      throw new IllegalArgumentException("Arguments can't be NULL: type, context");
    }
    getContextsByType(type).remove(context);
    getContextsByType(type).put(context, object);
  }

  public static <T> T getAndPutIfAbsent(ContextType<T> type, String context, Supplier<T> supplier) {
    T object = get(type, context) == null ? supplier.get() : get(type, context);
    if (object == null) {
      throw new IllegalStateException("Computed object can't be NULL");
    }
    put(type, context, object);
    return get(type, context);
  }

  public static <T> T get(ContextType<T> type, String context) {
    return (T) getContextsByType(type).get(context);
  }

  public static <T> T getLast(ContextType<T> type) {
    Map.Entry required = getContextsByType(type)
        .entrySet()
        .stream()
        .reduce((first, second) -> second)
        .orElse(null);
    return required == null ? null : (T) required.getValue();
  }

  public static <T> Object getFieldOf(ContextType<T> type, String context, String fieldName) {
    Object val = get(type, context);
    try {
      Field field = val.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      return field.get(val);
    } catch (IllegalAccessException | NoSuchFieldException ex) {
      log.error(ex.getMessage(), ex);
      throw new RuntimeException(ex);
    }
  }

  public static void clean() {
    CONTEXT_MAP.remove();
  }

  @Synchronized
  private static <T> Map<String, Object> getContextsByType(ContextType<T> type) {
    CONTEXT_MAP.get().computeIfAbsent(type.getName(), k -> new LinkedHashMap<>());
    return CONTEXT_MAP.get().get(type.getName());
  }
}
