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
}
