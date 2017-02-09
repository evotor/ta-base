package com.github.dimand58.autotest.base.api.util;

import com.google.common.base.*;
import io.restassured.specification.*;

public class CurlBuilder {

  private static String NEW_LINE = " \\" + System.lineSeparator();

  public static String buildFromRequestSpec(FilterableRequestSpecification spec) {
    StringBuilder rezult = new StringBuilder();

    rezult.append("curl -i -X ")
        .append(spec.getMethod())
        .append(NEW_LINE);

    spec.getHeaders().asList()
        .forEach(
            h -> rezult
                .append(
                    String.format(
                        "-H \"%s:%s\"",
                        h.getName(),
                        h.getValue()
                    )
                )
                .append(NEW_LINE)
    );


    if (spec.getFormParams().size() > 0) {
      rezult
          .append(
              String.format(
                  "-d%s'%s'",
                  NEW_LINE,
                  Joiner.on("&").withKeyValueSeparator("=").join(spec.getFormParams())
              )
          )
          .append(NEW_LINE);
    } else if (spec.getBody() != null && !spec.getBody().toString().isEmpty()) {
      rezult
          .append(
              String.format(
                  "-d%s'%s'",
                  NEW_LINE,
                  spec.getBody().toString()
              )
          )
          .append(NEW_LINE);
    }

    rezult.append(String.format("'%s'", spec.getURI()));

    return rezult.toString();
  }
}
