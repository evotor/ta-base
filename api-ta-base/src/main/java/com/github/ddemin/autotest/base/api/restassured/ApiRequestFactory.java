package com.github.ddemin.autotest.base.api.restassured;

import static com.google.common.net.HttpHeaders.*;
import static io.restassured.RestAssured.*;
import static io.restassured.config.HttpClientConfig.*;
import static io.restassured.mapper.ObjectMapperType.*;

import com.github.ddemin.autotest.base.api.json.*;

import io.restassured.*;
import io.restassured.config.*;
import io.restassured.http.*;
import io.restassured.parsing.*;
import io.restassured.path.json.config.*;
import io.restassured.specification.*;
import org.apache.http.params.*;

public class ApiRequestFactory {

  public static final RestAssuredConfig RESTASSURED_CONFIG;
  public static final String ENCODING = System.getProperty("file.encoding", "UTF-8");

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
                    .jackson2ObjectMapperFactory((cls, charset) -> JsonMapperFactory.getDefaultMapper())
            )
            .httpClient(httpClientConfig()
                .setParam(CoreConnectionPNames.SO_TIMEOUT, 60000)
            );
  }

  public static RequestSpecification createJsonRequest() {
    return given()
        .config(RESTASSURED_CONFIG)
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .header(CACHE_CONTROL, "no-cache")
        .baseUri("")
        .filters(new ApiModFilter(), new ApiSaveFilter(), new ApiSlf4jAndAllureFilter());
  }

  public static RequestSpecification createFormRequest() {
    return given()
        .config(RESTASSURED_CONFIG)
        .contentType(ContentType.URLENC)
        .header(CACHE_CONTROL, "no-cache")
        .baseUri("")
        .filters(new ApiModFilter(), new ApiSaveFilter(), new ApiSlf4jAndAllureFilter());
  }

}
