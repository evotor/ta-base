package com.github.ddemin.autotest.base.cucumber;

import java.util.*;

import cucumber.api.testng.*;
import cucumber.runtime.*;
import cucumber.runtime.Runtime;
import cucumber.runtime.io.*;
import cucumber.runtime.model.*;
import javafx.util.*;

public class ScenarioTestNGCucumberRunner extends TestNGCucumberRunner {
  private Runtime runtime;
  private RuntimeOptions runtimeOptions;
  private ResourceLoader resourceLoader;
  private FeatureResultListener resultListener;
  private ClassLoader classLoader;

  public ScenarioTestNGCucumberRunner(Class clazz) {
    super(clazz);

    classLoader = clazz.getClassLoader();
    resourceLoader = new MultiLoader(classLoader);

    RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(clazz);
    runtimeOptions = runtimeOptionsFactory.create();

    ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader);
    resultListener = new FeatureResultListener(runtimeOptions.reporter(classLoader), runtimeOptions.isStrict());
    runtime = new Runtime(resourceLoader, classFinder, classLoader, runtimeOptions);
  }

  public void runCucumber(Pair<CucumberTagStatement, CucumberFeatureWrapperImpl> scenarioWrapper) {
    resultListener.startFeature();

    runtimeOptions.formatter(classLoader).uri(scenarioWrapper.getValue().getCucumberFeature().getPath());
    runtimeOptions.formatter(classLoader).feature(scenarioWrapper.getValue().getCucumberFeature().getGherkinFeature());

    scenarioWrapper.getKey().run(
        runtimeOptions.formatter(classLoader),
        resultListener,
        runtime
    );

    runtimeOptions.formatter(classLoader).eof();

    finish();

    if (!resultListener.isPassed()) {
      throw new CucumberException(resultListener.getFirstError());
    }
  }

  public Object[][] provideScenarios() {
    try {
      List<CucumberFeature> features = getFeatures();
      List<Object[]> scenarioList = new ArrayList<Object[]>(features.size());
      for (CucumberFeature feature : features) {
        for (CucumberTagStatement element : feature.getFeatureElements()) {
          scenarioList.add(new Object[]{new Pair<>(element, new CucumberFeatureWrapperImpl(feature))});
        }
      }
      return scenarioList.toArray(new Object[][]{});
    } catch (CucumberException ex) {
      return new Object[][]{new Object[]{new Pair<>(null, new CucumberExceptionWrapper(ex))}};
    }
  }
}
