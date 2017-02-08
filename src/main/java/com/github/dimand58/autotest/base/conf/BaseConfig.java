package com.github.dimand58.autotest.base.conf;

import java.io.*;
import java.util.*;

import lombok.*;
import org.slf4j.bridge.*;
import ru.qatools.properties.*;

public class BaseConfig {

  public static final IConfigTesting TESTING = PropertyLoader.newInstance().populate(IConfigTesting.class);
  private static final String PATH_TO_ENVIRONMENT_FILE = "environment.properties";
  private static Properties allProperties;

  static {
    SLF4JBridgeHandler.removeHandlersForRootLogger();
    SLF4JBridgeHandler.install();
  }

  @Synchronized
  public static Properties getAllProperties() {
    if (allProperties == null) {
      allProperties = new Properties();
      try (
          InputStream stream = BaseConfig.class
              .getClassLoader()
              .getResourceAsStream(PATH_TO_ENVIRONMENT_FILE)
      ) {
        allProperties.load(stream);
        allProperties.putAll(System.getProperties());
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    }
    return allProperties;
  }
}
