package com.github.dimand58.autotest.base.api.restassured;

import static java.lang.String.*;

import com.github.dimand58.autotest.base.api.json.*;
import com.github.dimand58.autotest.base.util.*;

import java.util.*;

import com.cedarsoftware.util.io.*;
import io.restassured.filter.*;
import io.restassured.response.*;
import io.restassured.specification.*;
import lombok.extern.slf4j.*;

@Slf4j(topic = "restassured")
public class ApiSlf4jAndAllureFilter implements OrderedFilter {

  @Override
  public int getOrder() {
    return Integer.MAX_VALUE;
  }

  @Override
  public Response filter(FilterableRequestSpecification requestSpec,
                         FilterableResponseSpecification responseSpec,
                         FilterContext ctx) {
    logRequest(requestSpec);
    Response response = ctx.next(requestSpec, responseSpec);
    logResponse(response);
    return response;
  }

  private void logRequest(FilterableRequestSpecification requestSpec) {
    String fullUri = requestSpec.getMethod() + " " + requestSpec.getURI();
    String headers = Objects.toString(requestSpec.getHeaders(), "");
    String payload = Objects.toString(requestSpec.getBody(), "");

    if (requestSpec.getContentType().contains("application/json")) {
      try {
        payload = JsonWriter.formatJson(JsonHelper.toJson(payload));
      } catch (Exception ex) {
        log.debug(ex.getMessage());
      }
    }

    String request =
        fullUri + System.lineSeparator()
            + headers + System.lineSeparator()
            + payload;

    AttachHelper.attachText("Request", request);
    log.info("REQUEST - " + fullUri);
  }

  private void logResponse(Response response) {
    AttachHelper.attachText(
        format("Response headers (HTTP %d, %d ms)", response.statusCode(), response.time()),
        Objects.toString(response.getHeaders(), "")
    );
    AttachHelper.attachJson("Response payload", response.asString());
    log.info("RESPONSE - HTTP " + response.statusCode());
  }
}
