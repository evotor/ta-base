package com.github.ddemin.autotest.base.api.json;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;

import java.util.*;

import lombok.extern.slf4j.*;
import org.hamcrest.*;
import org.json.*;
import org.skyscreamer.jsonassert.*;

@Slf4j
public class JsonMatchers {

  public static <T extends Comparable> Matcher compare(String comparatorAsString, T objectExtendsComparable) {
    switch (comparatorAsString) {
      case "=":
        return equalTo(objectExtendsComparable);
      case "<":
        return lessThan(objectExtendsComparable);
      case ">":
        return greaterThan(objectExtendsComparable);
      case "<=":
        return lessThanOrEqualTo(objectExtendsComparable);
      case ">=":
        return greaterThanOrEqualTo(objectExtendsComparable);
      case "!=":
        return not(equalTo(objectExtendsComparable));
      default:
        throw new UnsupportedOperationException(comparatorAsString);
    }
  }

  public static void assertEquals(Object entityActual, Object entityExpected, boolean lenient) {
    try {
      JSONAssert.assertEquals(
          entityExpected instanceof String
              ? (String) entityExpected
              : JsonHelper.toJson(entityExpected),
          entityActual instanceof String ? (String) entityActual : JsonHelper.toJson(entityActual),
          lenient ? JSONCompareMode.LENIENT : JSONCompareMode.NON_EXTENSIBLE
      );
    } catch (JSONException ex) {
      log.error(ex.getMessage(), ex);
      throw new RuntimeException("JSONAssert failed: " + ex.getMessage());
    }
  }

  public static void assertNotEquals(Object entityActual, Object entityExpected, boolean lenient) {
    try {
      JSONAssert.assertNotEquals(
          entityExpected instanceof String
              ? (String) entityExpected
              : JsonHelper.toJson(entityExpected),
          entityActual instanceof String ? (String) entityActual : JsonHelper.toJson(entityActual),
          lenient ? JSONCompareMode.LENIENT : JSONCompareMode.NON_EXTENSIBLE
      );
    } catch (JSONException ex) {
      log.error(ex.getMessage(), ex);
      throw new RuntimeException("JSONAssert failed: " + ex.getMessage());
    }
  }

  public static void assertEqualsLists(
      List<Object> listActual, List listExpected, boolean lenient) {
    try {
      JSONAssert.assertEquals(
          JsonHelper.toJson(listExpected),
          JsonHelper.toJson(listActual),
          lenient ? JSONCompareMode.LENIENT : JSONCompareMode.NON_EXTENSIBLE
      );
    } catch (JSONException ex) {
      log.error(ex.getMessage(), ex);
      throw new RuntimeException("JSONAssert failed: " + ex.getMessage());
    }
  }

  public static void assertEqualsLists(String listActual, String listExpected, boolean lenient) {
    try {
      JSONAssert.assertEquals(
          listExpected,
          listActual,
          lenient ? JSONCompareMode.LENIENT : JSONCompareMode.NON_EXTENSIBLE
      );
    } catch (JSONException ex) {
      log.error(ex.getMessage(), ex);
      throw new RuntimeException("JSONAssert failed: " + ex.getMessage());
    }
  }

  public static void assertNotEqualsLists(List<Object> listActual, List listExpected) {
    try {
      JSONAssert.assertNotEquals(
          JsonHelper.toJson(listExpected),
          JsonHelper.toJson(listActual),
          JSONCompareMode.NON_EXTENSIBLE
      );
    } catch (JSONException ex) {
      log.error(ex.getMessage(), ex);
      throw new RuntimeException("JSONAssert failed: " + ex.getMessage());
    }
  }

  public static void assertNotEqualsLists(String listActual, String listExpected) {
    try {
      JSONAssert.assertNotEquals(listExpected, listActual, JSONCompareMode.NON_EXTENSIBLE);
    } catch (JSONException ex) {
      log.error(ex.getMessage(), ex);
      throw new RuntimeException("JSONAssert failed: " + ex.getMessage());
    }
  }

  public static boolean isListContainsEntity(List<Object> listEntities, Object entity) {
    try {
      for (Object item : listEntities) {
        if (
            JSONCompare.compareJSON(
                entity instanceof String ? (String) entity : JsonHelper.toJson(entity),
                JsonHelper.toJson(item),
                JSONCompareMode.LENIENT)
                .passed()) {
          return true;
        }
      }
    } catch (JSONException ex) {
      log.error(ex.getMessage(), ex);
      throw new RuntimeException("JSONAssert failed: " + ex.getMessage());
    }
    return false;
  }

  public static boolean isListContainsList(String listForScan, String listForSearch) {
    return isListContainsList(
        JsonHelper.fromJson(listForScan, List.class), JsonHelper.fromJson(listForSearch, List.class));
  }

  public static boolean isListContainsList(List<Object> listForScan, List<Object> listForSearch) {
    boolean exists = false;
    List forScan = new LinkedList();
    forScan.addAll(listForScan);

    try {
      for (Object expectedItem : listForSearch) {
        exists = false;
        for (Object currentItem : forScan) {
          if (
              JSONCompare.compareJSON(
                  expectedItem instanceof String
                      ? (String) expectedItem
                      : JsonHelper.toJson(expectedItem),
                  JsonHelper.toJson(currentItem),
                  JSONCompareMode.LENIENT)
                  .passed()
              ) {
            exists = true;
            forScan.remove(currentItem);
            break;
          }
        }
      }
      if (!exists) {
        return false;
      }

    } catch (JSONException ex) {
      log.error(ex.getMessage(), ex);
      throw new RuntimeException("JSONAssert failed: " + ex.getMessage());
    }
    return true;
  }

  public static boolean isListContains(String listForScan, String objectForSearch) {
    return isListContains(JsonHelper.fromJson(listForScan, List.class),
        JsonHelper.fromJson(objectForSearch, Map.class));
  }

  public static boolean isListContains(List<Object> listForScan, Object objForSearch) {
    List forScan = new LinkedList();
    forScan.addAll(listForScan);

    try {
      boolean exists = false;
      for (Object currentItem : forScan) {
        if (
            JSONCompare.compareJSON(
                objForSearch instanceof String
                    ? (String) objForSearch
                    : JsonHelper.toJson(objForSearch),
                JsonHelper.toJson(currentItem),
                JSONCompareMode.LENIENT)
                .passed()
            ) {
          exists = true;
          forScan.remove(currentItem);
          break;
        }
      }
      return exists;
    } catch (JSONException ex) {
      log.error(ex.getMessage(), ex);
      throw new RuntimeException("JSONAssert failed: " + ex.getMessage());
    }
  }
}
