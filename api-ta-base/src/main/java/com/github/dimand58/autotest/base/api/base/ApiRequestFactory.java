package com.github.dimand58.autotest.base.api.base;

import static com.jayway.restassured.config.HttpClientConfig.httpClientConfig;
import static com.jayway.restassured.internal.mapper.ObjectMapperType.JACKSON_2;

import com.github.dimand58.autotest.base.api.util.JsonHelper;
import com.github.dimand58.autotest.base.api.util.MapperFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Splitter;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ParseContext;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.DecoderConfig;
import com.jayway.restassured.config.EncoderConfig;
import com.jayway.restassured.config.JsonConfig;
import com.jayway.restassured.config.ObjectMapperConfig;
import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.config.SSLConfig;
import com.jayway.restassured.parsing.Parser;
import com.jayway.restassured.path.json.config.JsonPathConfig;
import com.jayway.restassured.response.Response;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.params.CoreConnectionPNames;

@Slf4j(topic = "restassured")
public class ApiRequestFactory {

  public static final RestAssuredConfig RESTASSURED_CONFIG;
  public static final String ENCODING = System.getProperty("file.encoding", "UTF-8");
  public static final ThreadLocal<ApiRequest> REQUEST_LAST = ThreadLocal.withInitial(() -> null);
  public static final ThreadLocal<Response> RESPONSE_LAST = ThreadLocal.withInitial(() -> null);
  public static final ThreadLocal<List<Pair<RequestModType, List<Object>>>> REQUEST_MODS =
      ThreadLocal.withInitial(ArrayList::new);

  static {
    RestAssured.defaultParser = Parser.JSON;

    RESTASSURED_CONFIG =
        new RestAssuredConfig()
            .sslConfig(new SSLConfig().relaxedHTTPSValidation().allowAllHostnames())
            .encoderConfig(new EncoderConfig(ENCODING, ENCODING))
            .decoderConfig(new DecoderConfig(ENCODING))
            .jsonConfig(
                new JsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL)
            )
            .objectMapperConfig(
                new ObjectMapperConfig(JACKSON_2)
                    .jackson2ObjectMapperFactory((cls, charset) -> MapperFactory.getDefault())
            )
            .httpClient(httpClientConfig()
                .setParam(CoreConnectionPNames.SO_TIMEOUT, 60000)
            );

    Configuration.setDefaults(
        new Configuration.Defaults() {

          @Override
          public JsonProvider jsonProvider() {
            return new JacksonJsonProvider(MapperFactory.getDefault());
          }

          @Override
          public Set<Option> options() {
            return new HashSet<>();
          }

          @Override
          public MappingProvider mappingProvider() {
            return new JacksonMappingProvider(MapperFactory.getDefault());
          }
        }
    );
  }

  public static ApiRequest createRequest() {
    return new ApiRequest();
  }

  public static ParseContext createJsonParser() {
    return JsonPath.using(
        Configuration.builder()
            .jsonProvider(new JacksonJsonProvider(MapperFactory.getDefault()))
            .mappingProvider(new JacksonMappingProvider(MapperFactory.getDefault()))
            .build()
    );
  }

  public static void registerMod(RequestModType modType, Object... modParams) {
    REQUEST_MODS.get().add(new Pair<>(modType, Arrays.asList(modParams)));
  }

  public static List<List<Object>> getMods(RequestModType modType) {
    return REQUEST_MODS
        .get()
        .stream()
        .filter(it -> it.getKey() == modType)
        .map(Pair::getValue)
        .collect(Collectors.toList());
  }

  static String modifyUrl(String url) {
    StringBuilder builder = new StringBuilder();
    builder.append(url);
    for (Pair<RequestModType, List<Object>> mod : REQUEST_MODS.get()) {
      if (mod.getKey() == RequestModType.URL_POSTFIX) {
        builder.append(mod.getValue().get(0));
      }
    }

    return builder.toString();
  }

  static Map<String, Object> modifyHeaders(Map<String, Object> headers) {
    REQUEST_MODS
        .get()
        .stream()
        .filter(mod -> mod.getKey() == RequestModType.HEADER_DELETE)
        .forEach(mod -> headers.remove(mod.getValue().get(0).toString()));
    REQUEST_MODS
        .get()
        .stream()
        .filter(mod -> mod.getKey() == RequestModType.HEADER_REPLACE)
        .forEach(mod -> headers.put(mod.getValue().get(0).toString(), mod.getValue().get(1)));
    return headers;
  }

  static Map modifyQuery(Object entity) {
    Map map;
    if (entity instanceof Map) {
      map = (Map) entity;
    } else if (entity instanceof String) {
      map = Splitter.on("&").withKeyValueSeparator("=").split((String) entity);
    } else if (entity == null) {
      map = new LinkedHashMap();
    } else {
      throw new IllegalArgumentException();
    }

    for (Pair<RequestModType, List<Object>> mod : REQUEST_MODS.get()) {
      switch (mod.getKey()) {
        case FIELD_REPLACE:
          map.put(mod.getValue().get(0), mod.getValue().get(1));
          break;
        case FIELD_DELETE:
          map.remove(mod.getValue().get(0));
          break;
        default:
      }
    }

    return map;
  }

  static String modifyPayload(Object entity) {
    String jsonChanged = entity instanceof String ? (String) entity : JsonHelper.toJson(entity);
    for (Pair<RequestModType, List<Object>> mod : REQUEST_MODS.get()) {
      switch (mod.getKey()) {
        case FIELD_REPLACE:
          jsonChanged =
              JsonHelper.toJsonWith(
                  jsonChanged, mod.getValue().get(0).toString(), mod.getValue().get(1));
          break;
        case FIELD_DELETE:
          jsonChanged = JsonHelper.toJsonWo(jsonChanged, mod.getValue().get(0).toString());
          break;
        case JSON_REPLACE:
          return mod.getValue().get(0).toString();
        default:
      }
    }

    if (entity instanceof BaseModel) {
      ((BaseModel) entity).setJson(jsonChanged);
    }

    return jsonChanged;
  }
}
