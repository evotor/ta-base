package com.github.ddemin.autotest.base.cucumber;

import cucumber.api.testng.CucumberFeatureWrapperImpl;
import cucumber.runtime.model.CucumberTagStatement;
import javafx.util.Pair;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.testng.ITest;
import org.testng.annotations.AfterClass;

/**
 * <p>1) YOU SHOULD create a STATIC DATAPROVIDER named as 'scenarios' at your subclass !</p> <p>2)
 * Than you should (inside dataprovider) init static field clazz by value = YourSubclass.class</p>
 */

@NoArgsConstructor
@Getter
@Setter
public class CucumberScenarioBasedTest implements ITest {

  protected final ThreadLocal<ScenarioTestNgCucumberRunner> cucumberRunner
      = ThreadLocal.withInitial(() -> new ScenarioTestNgCucumberRunner(this.getClass()));

  private Pair<CucumberTagStatement, CucumberFeatureWrapperImpl> scenarioWrapper;

  public CucumberScenarioBasedTest(
      Pair<CucumberTagStatement, CucumberFeatureWrapperImpl> scenarioWrapper) {
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
