package com.github.dimand58.autotest.base.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import lombok.Synchronized;
import org.slf4j.bridge.SLF4JBridgeHandler;
import ru.qatools.properties.PropertyLoader;

public class BaseConfig {

  public static final IConfigTesting TESTING = PropertyLoader.newInstance().populate(IConfigTesting.class);
  protected static final String PROPERTIES_FILE_NAME = TESTING.getPropertiesFilePath();

  private static Properties allProperties;

  static {
    SLF4JBridgeHandler.removeHandlersForRootLogger();
    SLF4JBridgeHandler.install();
  }

  @Synchronized
  public static Properties getAllProperties() {
    if (allProperties == null) {
      allProperties = new Properties();
      try (InputStream stream = BaseConfig.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME)) {
        allProperties.load(stream);
        allProperties.putAll(System.getProperties());
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    }
    return allProperties;
  }
}
