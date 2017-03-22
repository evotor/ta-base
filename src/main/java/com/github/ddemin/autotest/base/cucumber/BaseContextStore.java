package com.github.ddemin.autotest.base.cucumber;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import javax.annotation.*;

import lombok.extern.slf4j.*;

@Slf4j
public class BaseContextStore {

  private static final String LIST_MARK = "list of ";
  private static final ThreadLocal<Map<String, Map<String, Object>>> CONTEXT_MAP
      = ThreadLocal.withInitial(LinkedHashMap::new);

  public static <T> void put(ContextType<T> type, String context, T object) {
    if (type == null || context == null) {
      throw new IllegalArgumentException("Arguments can't be NULL: type, context");
    }
    getContextsByType(type).remove(context);
    getContextsByType(type).put(context, object);
  }

  public static <T> void putList(ContextType<T> type, String context, List<T> object) {
    if (type == null || context == null) {
      throw new IllegalArgumentException("Arguments can't be NULL: type, context");
    }
    getContextsByType(type).remove(LIST_MARK + context);
    getContextsByType(type).put(LIST_MARK + context, object);
  }

  public static <T> T get(ContextType<T> type, String context) {
    return (T) getContextsByType(type).get(context);
  }

  public static <T> List<T> getList(ContextType<T> type, String context) {
    Object required = getContextsByType(type).get(LIST_MARK + context);
    if (required != null && !(required instanceof List)) {
      throw new IllegalStateException(
          String.format("Required object(%s, %s) isn't a List", type.getEntityName(), context)
      );
    }
    return (List<T>) getContextsByType(type).get(LIST_MARK + context);
  }

  public static <T> T getAndPutIfAbsent(ContextType<T> type, String context, Supplier<T> supplier) {
    T object = get(type, context) == null ? supplier.get() : get(type, context);
    if (object == null) {
      throw new IllegalStateException("Computed object can't be NULL");
    }
    put(type, context, object);
    return get(type, context);
  }

  public static <T> List<T> getListAndPutIfAbsent(ContextType<T> type, String context, Supplier<List<T>> supplier) {
    List<T> listObject = getList(type, context) == null ? supplier.get() : getList(type, context);
    if (listObject == null) {
      throw new IllegalStateException("Computed listObject can't be NULL");
    }
    putList(type, context, listObject);
    return getList(type, context);
  }

  public static <T> T getLast(ContextType<T> type) {
    Map.Entry required = getContextsByType(type).entrySet()
        .stream()
        .filter(it -> !(it.getValue() instanceof List))
        .reduce((first, second) -> second)
        .orElse(null);
    return required == null ? null : (T) required.getValue();
  }

  public static <T> List<T> getLastList(ContextType<T> type) {
    Map.Entry required = getContextsByType(type).entrySet()
        .stream()
        .filter(it -> it.getValue() instanceof List)
        .reduce((first, second) -> second)
        .orElse(null);
    return required == null ? null : (List<T>) required.getValue();
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

  private static <T> Map<String, Object> getContextsByType(ContextType<T> type) {
    CONTEXT_MAP.get().computeIfAbsent(type.getEntityName(), k -> new LinkedHashMap<>());
    return CONTEXT_MAP.get().get(type.getEntityName());
  }
}
