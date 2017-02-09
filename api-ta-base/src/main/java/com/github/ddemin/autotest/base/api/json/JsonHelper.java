package com.github.ddemin.autotest.base.api.json;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

import com.fasterxml.jackson.core.*;
import com.jayway.jsonpath.*;
import lombok.extern.slf4j.*;

@Slf4j
public class JsonHelper {

  public static <T> T fromJsonFile(String pathToFile, Class<T> clazz) {
    String fileContent;
    try {
      fileContent =
          new String(
              Files.readAllBytes(Paths.get(pathToFile.getClass().getResource(pathToFile).toURI())),
              Charset.forName("UTF-8")
          );
    } catch (IOException | URISyntaxException ex) {
      log.error(ex.getMessage(), ex);
      throw new RuntimeException(ex);
    }
    return fromJson(fileContent, clazz);
  }

  public static <T> T fromJson(String json, Class<T> clazz) {
    try {
      return JsonMapperFactory.getDefaultMapper().readerFor(clazz).readValue(json);
    } catch (IOException ex) {
      log.error(ex.getMessage(), ex);
      throw new RuntimeException(ex);
    }
  }

  public static String toJson(Object entity) {
    if (entity instanceof String) {
      return (String) entity;
    }
    try {
      return JsonMapperFactory.getDefaultMapper().writeValueAsString(entity);
    } catch (JsonProcessingException ex) {
      log.error(ex.getMessage(), ex);
      throw new RuntimeException(ex);
    }
  }

  public static String toJsonWo(Object entity, String... pathsToJsonNode) {
    DocumentContext context = JsonPathFactory.createJsonParser().parse(toJson(entity));
    for (String path : pathsToJsonNode) {
      context.delete(path);
    }
    return context.jsonString();
  }

  public static String toJsonWo(Object entity, List<String> pathsToJsonNode) {
    DocumentContext context = JsonPathFactory.createJsonParser().parse(toJson(entity));
    for (String path : pathsToJsonNode) {
      context.delete(path);
    }
    return context.jsonString();
  }

  public static String toJsonWith(Object entity, String pathToNode, Object newNodeValue) {
    DocumentContext context = JsonPathFactory.createJsonParser().parse(toJson(entity));
    context.set(pathToNode, newNodeValue);
    return context.jsonString();
  }
}
