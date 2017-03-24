package com.github.ddemin.autotest.base.cucumber;

import static org.testng.Assert.*;

import com.github.ddemin.autotest.base.conf.*;

import java.lang.reflect.*;
import java.util.*;

import com.google.common.base.*;
import cucumber.api.*;
import cucumber.api.testng.*;
import cucumber.runtime.model.*;
import edu.umd.cs.findbugs.annotations.*;
import javafx.util.*;
import lombok.*;
import org.testng.annotations.*;

/**
 * <p>1) YOU SHOULD create a STATIC DATAPROVIDER named as 'scenarios' at your subclass !</p>
 * <p>2) Than you should (inside dataprovider) init static field clazz by value = YourSubclass.class</p>
 */
@NoArgsConstructor
public class CucumberScenarioBasedTest {

  @SuppressFBWarnings
  protected static Class clazz;

  protected final ThreadLocal<ScenarioTestNGCucumberRunner> cucumberRunner = ThreadLocal.withInitial(() -> {
    assertNotNull(clazz, "Static field CucumberScenarioBasedTest#clazz must be initialized");
    return new ScenarioTestNGCucumberRunner(clazz);
  });

  private Pair<CucumberTagStatement, CucumberFeatureWrapperImpl> scenarioWrapper;

  @Factory(dataProvider = "scenarios")
  public CucumberScenarioBasedTest(Pair<CucumberTagStatement, CucumberFeatureWrapperImpl> scenarioWrapper) {
    this.scenarioWrapper = scenarioWrapper;
  }

  public void runScenario() {
    cucumberRunner.get().runCucumber(this.scenarioWrapper);
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

    cucumberRunner.set(new ScenarioTestNGCucumberRunner(this.getClass()));
  }

  @AfterClass(alwaysRun = true)
  public void tearDownClass() throws Exception {
    cucumberRunner.get().finish();
  }
}
