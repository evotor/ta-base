package com.github.dimand58.autotest.base.api.util;

import java.text.SimpleDateFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.fge.jackson.JacksonUtils;

public class MapperFactory {

  public static ObjectMapper getDefault() {
    return getDefault(null);
  }

  public static ObjectMapper getDefault(String dateTimeFormat) {
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

  public static ObjectMapper getDefaultWoNulls() {
    return getDefaultWoNulls(null);
  }

  public static ObjectMapper getDefaultWoNulls(String dateTimeFormat) {
    return getDefault(dateTimeFormat)
        .disable(SerializationFeature.WRITE_NULL_MAP_VALUES)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL);
  }
}
