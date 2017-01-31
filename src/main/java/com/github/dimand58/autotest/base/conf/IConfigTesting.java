package com.github.dimand58.autotest.base.conf;

import ru.qatools.properties.Property;

public interface IConfigTesting {

  @Property("testing.tags")
  String getTags();
}
