package com.github.ddemin.autotest.base.api.json;

import java.text.*;
import java.util.*;
import java.util.function.*;

import com.fasterxml.jackson.databind.*;
import com.jayway.jsonpath.*;
import com.jayway.jsonpath.spi.json.*;
import com.jayway.jsonpath.spi.mapper.*;

public class JsonMapperFactory {

  private static Supplier<ObjectMapper> mapperSupplier;

  static {
    Configuration.setDefaults(
        new Configuration.Defaults() {
          @Override
          public JsonProvider jsonProvider() {
            return new JacksonJsonProvider(getDefaultMapper());
          }

          @Override
          public Set<Option> options() {
            return new HashSet<>();
          }

          @Override
          public MappingProvider mappingProvider() {
            return new JacksonMappingProvider(getDefaultMapper());
          }
        }
    );
  }

  public static ObjectMapper getDefaultMapper() {
    if (mapperSupplier == null) {
      throw new IllegalStateException("mapperSupplier must be defined via JsonMapperFactory#setJsonMapperSupplier");
    }
    return mapperSupplier.get();
  }

  public static ObjectMapper getDefaultMapper(String dateTimeFormat) {
    return dateTimeFormat != null
        ? getDefaultMapper().setDateFormat(new SimpleDateFormat(dateTimeFormat))
        : getDefaultMapper();
  }

  public static void setJsonMapperSupplier(Supplier<ObjectMapper> mapperSupplier) {
    JsonMapperFactory.mapperSupplier = mapperSupplier;
  }
}
