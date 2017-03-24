package com.github.ddemin.autotest.base.conf;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

import lombok.*;
import lombok.extern.slf4j.*;
import ru.qatools.properties.*;

@Slf4j
public class BaseConfig {

  public static final IConfigTesting TESTING = PropertyLoader.newInstance().populate(IConfigTesting.class);
  private static final String PATH_TO_PROPERTIES_FOLDER
      = System.getProperty("testing.properties.folder", "./properties");
  private static Properties allProperties;

  static {
    try {
      BaseConfig.getAllProperties();
    } catch (IOException ex) {
      log.error("", ex);
    }
  }

  @Synchronized
  public static Properties getAllProperties() throws IOException {
    if (allProperties == null) {
      allProperties = new Properties();

      try (Stream<Path> paths = Files.walk(Paths.get(PATH_TO_PROPERTIES_FOLDER))) {
        paths.forEach(filePath -> {
          if (Files.isRegularFile(filePath)) {
            InputStream fileInputStream = null;
            try {
              fileInputStream = Files.newInputStream(filePath, StandardOpenOption.READ);
              allProperties.load(fileInputStream);
            } catch (IOException ex) {
              log.error("", ex);
            } finally {
              if (fileInputStream != null) {
                try {
                  fileInputStream.close();
                } catch (IOException ex) {
                  log.error("", ex);
                }
              }
            }
          }
        });
      } finally {
        allProperties.putAll(System.getProperties());
        System.setProperties(allProperties);
      }

    }

    return allProperties;
  }
}
