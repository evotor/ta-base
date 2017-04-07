package com.github.ddemin.autotest.base.api.restassured.filter;

import com.github.ddemin.autotest.base.api.json.*;
import com.github.ddemin.autotest.base.api.model.*;
import com.github.ddemin.autotest.base.api.restassured.*;

import java.util.*;
import java.util.stream.*;

import com.cedarsoftware.util.io.*;
import io.restassured.filter.*;
import io.restassured.response.*;
import io.restassured.specification.*;
import javafx.util.*;
import lombok.extern.slf4j.*;

@Slf4j
public class ApiModFilter implements OrderedFilter {

  public static final ThreadLocal<List<Pair<RequestModType, List<Object>>>> REQUEST_MODS =
      ThreadLocal.withInitial(ArrayList::new);

  public static void registerMod(RequestModType modType, List<Object> modParams) {
    REQUEST_MODS.get().add(new Pair<>(modType, modParams));
  }

  public static void registerMod(RequestModType modType, Object... modParams) {
    registerMod(modType, Arrays.asList(modParams));
  }

  public static List<List<Object>> getMods(RequestModType modType) {
    return REQUEST_MODS
        .get()
        .stream()
        .filter(it -> it.getKey() == modType)
        .map(Pair::getValue)
        .collect(Collectors.toList());
  }

  @Override
  public int getOrder() {
    return Integer.MIN_VALUE;
  }

  @Override
  public Response filter(
      FilterableRequestSpecification requestSpec,
      FilterableResponseSpecification responseSpec,
      FilterContext ctx) {
    modifyUrlPath(requestSpec);
    modifyHeaders(requestSpec);
    modifyQueryParams(requestSpec);

    if (requestSpec.getBody() != null) {
      modifyPayload(requestSpec);
    } else {
      modifyFormParams(requestSpec);
    }

    REQUEST_MODS.remove();

    return ctx.next(requestSpec, responseSpec);
  }

  private void modifyUrlPath(FilterableRequestSpecification spec) {
    StringBuilder builder = new StringBuilder();
    builder.append(spec.getBaseUri());

    for (Pair<RequestModType, List<Object>> mod : REQUEST_MODS.get()) {
      if (mod.getKey() == RequestModType.URL_POSTFIX) {
        builder.append(mod.getValue().get(0));
      }
    }

    spec.baseUri(builder.toString());
  }

  private void modifyHeaders(FilterableRequestSpecification spec) {
    REQUEST_MODS.get()
        .forEach(mod -> {
          if (mod.getKey() == RequestModType.HEADER_REPLACE) {
            spec.removeHeader((String) mod.getValue().get(0));
            spec.header((String) mod.getValue().get(0), mod.getValue().get(1));
          } else if (mod.getKey() == RequestModType.HEADER_DELETE) {
            spec.removeHeader((String) mod.getValue().get(0));
          }
        });
  }

  private void modifyFormParams(FilterableRequestSpecification spec) {
    REQUEST_MODS.get()
        .forEach(mod -> {
          if (mod.getKey() == RequestModType.FIELD_REPLACE) {
            spec.removeFormParam((String) mod.getValue().get(0));
            spec.formParam((String) mod.getValue().get(0), mod.getValue().get(1));
          } else if (mod.getKey() == RequestModType.FIELD_DELETE) {
            spec.removeFormParam((String) mod.getValue().get(0));
          }
        });
  }

  private void modifyQueryParams(FilterableRequestSpecification spec) {
    REQUEST_MODS.get()
        .forEach(mod -> {
          if (mod.getKey() == RequestModType.QUERY_FIELD_REPLACE) {
            spec.removeQueryParam((String) mod.getValue().get(0));
            spec.queryParam((String) mod.getValue().get(0), mod.getValue().get(1));
          } else if (mod.getKey() == RequestModType.QUERY_FIELD_DELETE) {
            spec.removeQueryParam((String) mod.getValue().get(0));
          }
        });
  }

  // TODO refactoring
  private void modifyPayload(FilterableRequestSpecification requestSpec) {
    REQUEST_MODS.get()
        .forEach(mod -> {
          if (requestSpec.getContentType().contains("application/json")) {
            if (mod.getKey() == RequestModType.FIELD_REPLACE) {
              requestSpec.body(JsonHelper.toJsonWith(
                  JsonHelper.toJson(requestSpec.getBody()),
                  mod.getValue().get(0).toString(),
                  mod.getValue().get(1)
              ));
            } else if (mod.getKey() == RequestModType.FIELD_DELETE) {
              requestSpec.body(
                  JsonHelper.toJsonWo(JsonHelper.toJson(requestSpec.getBody()), mod.getValue().get(0).toString())
              );
            }
          }
          if (mod.getKey() == RequestModType.PAYLOAD_REPLACE) {
            requestSpec.body(mod.getValue().get(0).toString());
          }
        });

    if (requestSpec.getContentType().contains("application/json")) {
      try {
        requestSpec.body(JsonWriter.formatJson(JsonHelper.toJson(requestSpec.getBody())));
      } catch (Exception ex) {
        log.debug(ex.getMessage());
      }
    }

    if (requestSpec.getContentType().contains("application/json") && requestSpec.getBody() instanceof BaseModel) {
      ((BaseModel) requestSpec.getBody()).saveJson(requestSpec.getBody());
    }
  }
}
