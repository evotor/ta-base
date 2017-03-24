package com.github.ddemin.autotest.base.conf;

import ru.qatools.properties.*;

public interface IConfigTesting {

  @Property("testing.tags")
  String getTags();

  @Property("testing.assert.timeout")
  @DefaultValue("5000")
  long getAssertTimeout();

  @Property("testing.assert.delay")
  @DefaultValue("5000")
  long getAssertDelay();

  @Property("testing.assert.poll")
  @DefaultValue("500")
  long getAssertPoll();

  @Property("testing.semaphore.timeout")
  @DefaultValue("60000")
  long getTestDataTimeout();
}
