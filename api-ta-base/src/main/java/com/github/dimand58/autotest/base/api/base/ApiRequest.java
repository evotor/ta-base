package com.github.dimand58.autotest.base.api.base;

import static com.google.common.net.HttpHeaders.CACHE_CONTROL;
import static com.jayway.restassured.RestAssured.given;
import static java.lang.String.format;

import com.github.dimand58.autotest.base.api.util.ApiDictionary;
import com.github.dimand58.autotest.base.api.util.JsonHelper;
import com.github.dimand58.autotest.base.util.AttachHelper;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.cedarsoftware.util.io.JsonWriter;
import com.google.common.base.Joiner;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.http.Method;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Headers;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import lombok.Getter;
import lombok.Synchronized;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Getter
@ToString
@Slf4j(topic = "restassured")
public class ApiRequest {

  Method method;
  String url;
  String path;
  String urlFinal;
  Map<String, Object> headers = new HashMap<>();
  Map urlParams;
  Object payload;
  RequestSpecification spec = buildNewSpec();

  ApiRequest() {
  }

  private static void waitDelay() {
    try {
      Thread.sleep(250);
    } catch (InterruptedException ex) {
      log.error(ex.getMessage(), ex);
    }
  }

  @Synchronized
  private static RequestSpecification buildNewSpec() {
    return given()
        .config(ApiRequestFactory.RESTASSURED_CONFIG)
        .contentType(ApiDictionary.CONTENT_JSON)
        .accept(ApiDictionary.CONTENT_JSON)
        .header(CACHE_CONTROL, "no-cache")
        .baseUri("");
  }

  private static void logRequest() {
    String requestPath =
        ApiRequestFactory.REQUEST_LAST.get().getMethod().toString()
            + " "
            + ApiRequestFactory.REQUEST_LAST.get().getUrlFinal();

    Headers headers = null;
    if (ApiRequestFactory.REQUEST_LAST.get().getHeaders() != null && !ApiRequestFactory.REQUEST_LAST.get().getHeaders()
        .isEmpty()) {
      headers = new Headers(ApiRequestFactory.REQUEST_LAST.get().getHeaders()
          .entrySet()
          .stream()
          .map(it -> new Header(it.getKey(), it.getValue() == null ? null : it.getValue().toString()))
          .collect(Collectors.toList()));
    }

    String headersString = Objects.toString(headers, "");
    String payload = JsonHelper.toJson(ApiRequestFactory.REQUEST_LAST.get().getPayload());
    try {
      payload = JsonWriter.formatJson(payload);
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
      if (ApiRequestFactory.REQUEST_LAST.get().getPayload() instanceof String) {
        payload = (String) ApiRequestFactory.REQUEST_LAST.get().getPayload();
      }
    }
    String request = requestPath
        + System.lineSeparator()
        + headersString
        + System.lineSeparator()
        + payload;

    AttachHelper.attachText("ApiRequest info", request);
    log.info("REQUEST - " + requestPath);
  }

  private static void logResponse() {
    Response response = ApiRequestFactory.RESPONSE_LAST.get();
    String headersString = Objects.toString(response.getHeaders(), "");
    AttachHelper.attachText(
        format("Response headers (HTTP %d, %d ms)", response.statusCode(), response.time()),
        headersString
    );
    AttachHelper.attachJson("Response body", response.asString());
    log.info("RESPONSE - HTTP " + response.statusCode());
  }

  public ApiRequest setMethod(Method method) {
    this.method = method;
    return this;
  }

  public ApiRequest setPath(String path) {
    this.path = path;
    urlFinal = this.url + this.path;
    return this;
  }

  public ApiRequest setHeaders(Map<String, Object> headers) {
    this.headers = ApiRequestFactory.modifyHeaders(headers);
    spec.headers(this.headers);
    return this;
  }

  public ApiRequest setHeaders(Headers hdrs) {
    Map<String, Object> headersMap = new HashMap();
    if (hdrs != null) {
      for (Header header : hdrs.asList()) {
        headersMap.put(header.getName(), header.getValue());
      }
    }
    return setHeaders(headersMap);
  }

  public ApiRequest setUrlParams(Map urlParams) {
    if (method == Method.GET) {
      this.urlParams = ApiRequestFactory.modifyQuery(urlParams);
      if (!this.urlParams.isEmpty()) {
        urlFinal += "?" + Joiner.on("&").withKeyValueSeparator("=").join(this.urlParams);
      }
    }
    return this;
  }

  public ApiRequest setPayload(Object payload) {
    if (payload instanceof Map) {
      this.payload = payload;
      spec = spec.formParams((Map) this.payload).contentType(ContentType.URLENC);
    } else if (payload != null) {
      this.payload = ApiRequestFactory.modifyPayload(payload);
      spec = spec.body(this.payload);
    }
    return this;
  }

  public Response send() {
    urlFinal = ApiRequestFactory.modifyUrl(urlFinal);

    ApiRequestFactory.REQUEST_MODS.remove();
    ApiRequestFactory.REQUEST_LAST.set(this);
    logRequest();
    switch (method) {
      case GET:
        ApiRequestFactory.RESPONSE_LAST.set(spec.get(urlFinal));
        break;
      case PUT:
        ApiRequestFactory.RESPONSE_LAST.set(spec.put(urlFinal));
        break;
      case POST:
        ApiRequestFactory.RESPONSE_LAST.set(spec.post(urlFinal));
        break;
      case DELETE:
        ApiRequestFactory.RESPONSE_LAST.set(spec.delete(urlFinal));
        break;
      default:
        throw new UnsupportedOperationException();
    }
    logResponse();
    waitDelay();
    return ApiRequestFactory.RESPONSE_LAST.get();
  }

  public URL getUrl() {
    try {
      return new URI(url + path).toURL();
    } catch (MalformedURLException | URISyntaxException ex) {
      log.error(ex.getMessage());
      return null;
    }
  }

  public ApiRequest setUrl(String url) {
    this.url = url;
    urlFinal = this.url + this.path;
    return this;
  }
}
