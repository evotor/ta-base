package com.github.ddemin.autotest.base.api.json;

import java.util.*;

import com.jayway.jsonpath.*;
import com.jayway.jsonpath.spi.json.*;
import com.jayway.jsonpath.spi.mapper.*;

public class JsonPathFactory {

  static {
    Configuration.setDefaults(
        new Configuration.Defaults() {

          @Override
          public JsonProvider jsonProvider() {
            return new JacksonJsonProvider(JsonMapperFactory.getDefaultMapper());
          }

          @Override
          public Set<Option> options() {
            return new HashSet<>();
          }

          @Override
          public MappingProvider mappingProvider() {
            return new JacksonMappingProvider(JsonMapperFactory.getDefaultMapper());
          }
        }
    );
  }

  public static ParseContext createJsonParser() {
    return JsonPath.using(
        Configuration.builder()
            .jsonProvider(new JacksonJsonProvider(JsonMapperFactory.getDefaultMapper()))
            .mappingProvider(new JacksonMappingProvider(JsonMapperFactory.getDefaultMapper()))
            .build()
    );
  }

}
