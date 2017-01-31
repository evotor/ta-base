package com.github.dimand58.autotest.base.conf;

import ru.qatools.properties.Property;

public interface IConfigTesting {

  @Property("testing.environment.file")
  default String getPropertiesFilePath() {
    return "environment.properties";
  }

  @Property("testing.groups")
  String getGroups();

  @Property("testing.tags")
  String getTags();
}
