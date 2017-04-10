package com.github.ddemin.autotest.base.cucumber;

import cucumber.api.testng.*;
import cucumber.runtime.model.*;
import javafx.util.*;
import lombok.*;
import org.testng.*;
import org.testng.annotations.*;

/**
 * <p>1) YOU SHOULD create a STATIC DATAPROVIDER named as 'scenarios' at your subclass !</p>
 * <p>2) Than you should (inside dataprovider) init static field clazz by value = YourSubclass.class</p>
 */

@NoArgsConstructor
@Getter
@Setter
public class CucumberScenarioBasedTest implements ITest {

  protected final ThreadLocal<ScenarioTestNGCucumberRunner> cucumberRunner
      = ThreadLocal.withInitial(() -> new ScenarioTestNGCucumberRunner(this.getClass()));

  private Pair<CucumberTagStatement, CucumberFeatureWrapperImpl> scenarioWrapper;

  public CucumberScenarioBasedTest(Pair<CucumberTagStatement, CucumberFeatureWrapperImpl> scenarioWrapper) {
    this.scenarioWrapper = scenarioWrapper;
  }

  public void runScenario() {
    cucumberRunner.get().runCucumber(this.scenarioWrapper);
  }

  @AfterClass(alwaysRun = true)
  public void tearDownClass() throws Exception {
    cucumberRunner.get().finish();
  }

  @Override
  public String getTestName() {
    return this.scenarioWrapper == null
        ? "undefined"
        : this.scenarioWrapper.getKey().getGherkinModel().getName();
  }
}
