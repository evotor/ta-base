package com.github.ddemin.autotest.base.cucumber;

import com.github.ddemin.autotest.base.conf.BaseConfig;
import com.google.common.base.Splitter;
import cucumber.api.CucumberOptions;
import cucumber.api.testng.CucumberFeatureWrapper;
import cucumber.api.testng.TestNGCucumberRunner;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

/**
 * YOU SHOULD annotate your subclass with @Test which contains dataProvider parameter = features
 */
public class CucumberFeatureBasedTest {

  protected final ThreadLocal<TestNGCucumberRunner> threadLocalRunner
      = ThreadLocal.withInitial(() -> new TestNGCucumberRunner(this.getClass()));

  @DataProvider(parallel = true)
  public Object[][] features() {
    return threadLocalRunner.get().provideFeatures();
  }

  public void runFeature(CucumberFeatureWrapper cucumberFeature) {
    threadLocalRunner.get().runCucumber(cucumberFeature.getCucumberFeature());
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

    threadLocalRunner.set(new TestNGCucumberRunner(this.getClass()));
  }

  @AfterClass(alwaysRun = true)
  public void tearDownClass() throws Exception {
    threadLocalRunner.get().finish();
  }
}
