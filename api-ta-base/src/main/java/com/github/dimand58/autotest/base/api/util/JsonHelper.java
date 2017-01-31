package com.github.dimand58.autotest.base.api.util;

import com.github.dimand58.autotest.base.api.base.ApiRequestFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.jsonpath.DocumentContext;
import lombok.extern.slf4j.Slf4j;

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
      return MapperFactory.getDefault().readerFor(clazz).readValue(json);
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
      return MapperFactory.getDefault().writeValueAsString(entity);
    } catch (JsonProcessingException ex) {
      log.error(ex.getMessage(), ex);
      throw new RuntimeException(ex);
    }
  }

  public static String toJsonWo(Object entity, String... pathsToJsonNode) {
    DocumentContext context = ApiRequestFactory.createJsonParser().parse(toJson(entity));
    for (String path : pathsToJsonNode) {
      context.delete(path);
    }
    return context.jsonString();
  }

  public static String toJsonWo(Object entity, List<String> pathsToJsonNode) {
    DocumentContext context = ApiRequestFactory.createJsonParser().parse(toJson(entity));
    for (String path : pathsToJsonNode) {
      context.delete(path);
    }
    return context.jsonString();
  }

  public static String toJsonWith(Object entity, String pathToNode, Object newNodeValue) {
    DocumentContext context = ApiRequestFactory.createJsonParser().parse(toJson(entity));
    context.set(pathToNode, newNodeValue);
    return context.jsonString();
  }
}
