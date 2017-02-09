package com.github.ddemin.autotest.base.api.restassured;

import io.restassured.filter.*;
import io.restassured.response.*;
import io.restassured.specification.*;
import lombok.extern.slf4j.*;

@Slf4j
public class ApiSaveFilter implements OrderedFilter {

  public static final ThreadLocal<FilterableRequestSpecification> REQUEST_LAST = ThreadLocal.withInitial(() -> null);
  public static final ThreadLocal<Response> RESPONSE_LAST = ThreadLocal.withInitial(() -> null);

  @Override
  public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec,
                         FilterContext ctx) {
    REQUEST_LAST.set(requestSpec);
    RESPONSE_LAST.set(ctx.next(requestSpec, responseSpec));
    return RESPONSE_LAST.get();
  }

  @Override
  public int getOrder() {
    return Integer.MAX_VALUE;
  }
}
