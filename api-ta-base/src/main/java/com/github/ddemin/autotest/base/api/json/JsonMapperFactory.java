package com.github.ddemin.autotest.base.api.json;

import java.text.*;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;
import com.github.fge.jackson.*;

public class JsonMapperFactory {

  public static ObjectMapper getDefaultMapper() {
    return getDefaultMapper(null);
  }

  public static ObjectMapper getDefaultMapper(String dateTimeFormat) {
    ObjectMapper mapper =
        JacksonUtils.newMapper()
            .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    if (dateTimeFormat != null) {
      mapper = mapper.setDateFormat(new SimpleDateFormat(dateTimeFormat));
    }
    return mapper;
  }

  public static ObjectMapper getDefaultMapperWoNulls() {
    return getDefaultMapperWoNulls(null);
  }

  public static ObjectMapper getDefaultMapperWoNulls(String dateTimeFormat) {
    return getDefaultMapper(dateTimeFormat)
        .disable(SerializationFeature.WRITE_NULL_MAP_VALUES)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL);
  }
}
