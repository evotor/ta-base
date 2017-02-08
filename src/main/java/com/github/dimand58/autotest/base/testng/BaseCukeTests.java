package com.github.dimand58.autotest.base.testng;

import com.github.dimand58.autotest.base.conf.*;

import java.lang.reflect.*;
import java.util.*;

import com.google.common.base.*;
import cucumber.api.*;
import cucumber.api.testng.*;
import org.testng.annotations.*;

public class BaseCukeTests {

  protected final ThreadLocal<TestNGCucumberRunner> testNgCucumberRunner =
      ThreadLocal.withInitial(() -> new TestNGCucumberRunner(this.getClass()));

  @DataProvider(parallel = true)
  public Object[][] features() {
    return testNgCucumberRunner.get().provideFeatures();
  }

  // TODO Refactoring
  @BeforeClass(alwaysRun = true)
  public void setUpClass() throws Exception {

    CucumberOptions cucOpts = this.getClass().getAnnotation(CucumberOptions.class);
    List<String> tags = new ArrayList<>();
    if (BaseConfig.TESTING.getTags() == null) {
      tags = null;
    } else if (BaseConfig.TESTING.getTags().matches("\\[.*\\]")) {
      tags = Splitter.on(',').splitToList(BaseConfig.TESTING.getTags());
    } else {
      tags.add(BaseConfig.TESTING.getTags());
    }

    if (tags != null && !tags.isEmpty() && !tags.get(0).isEmpty()) {
      Object handler = Proxy.getInvocationHandler(cucOpts);
      Field fld;
      try {
        fld = handler.getClass().getDeclaredField("memberValues");
      } catch (NoSuchFieldException | SecurityException ex) {
        throw new IllegalStateException(ex);
      }
      fld.setAccessible(true);

      Map<String, Object> memberValues;
      try {
        memberValues = (Map<String, Object>) fld.get(handler);
      } catch (IllegalArgumentException | IllegalAccessException ex) {
        throw new IllegalStateException(ex);
      }

      Object oldValue = memberValues.get("tags");
      String[] newValue = tags.toArray(new String[tags.size()]);
      if (oldValue == null || oldValue.getClass() != newValue.getClass()) {
        throw new IllegalArgumentException();
      }
      memberValues.put("tags", newValue);
    }

    testNgCucumberRunner.set(new TestNGCucumberRunner(this.getClass()));
  }

  @AfterClass(alwaysRun = true)
  public void tearDownClass() throws Exception {
    testNgCucumberRunner.get().finish();
  }
}
