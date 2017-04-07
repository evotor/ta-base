package com.github.ddemin.autotest.base.cucumber;

import com.github.ddemin.autotest.base.conf.*;

import java.util.*;
import java.util.stream.*;

import com.google.common.base.*;
import cucumber.api.testng.*;
import cucumber.runtime.*;
import cucumber.runtime.Runtime;
import cucumber.runtime.io.*;
import cucumber.runtime.model.*;
import gherkin.formatter.model.*;
import javafx.util.*;
import org.testng.*;

public class ScenarioTestNGCucumberRunner extends TestNGCucumberRunner {
  private static final String TAGS_PROPERTY = BaseConfig.TESTING.getTags();

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

  private static boolean isScenarioAllowed(CucumberTagStatement statement) {
    if (TAGS_PROPERTY == null || TAGS_PROPERTY.isEmpty()) {
      return true;
    }

    List<String> requiredTags = new ArrayList<>();
    List<String> scenarioTags = statement.getGherkinModel().getTags()
        .stream()
        .map(Tag::getName)
        .collect(Collectors.toList());

    if (TAGS_PROPERTY.matches("\\[.*\\]")) {
      requiredTags = Splitter.on(',').splitToList(TAGS_PROPERTY.replaceAll("[\\[\\]]+", ""));
    } else {
      requiredTags.add(TAGS_PROPERTY);
    }

    if (requiredTags.size() > 1) {
      scenarioTags.containsAll(requiredTags);
    } else {
      requiredTags = Splitter.on(',').splitToList(TAGS_PROPERTY);
      return requiredTags.stream().anyMatch(scenarioTags::contains);
    }

    return false;
  }

  public void runCucumber(Pair<CucumberTagStatement, CucumberFeatureWrapperImpl> scenarioWrapper) {
    if (scenarioWrapper == null) {
      throw new SkipException("Test skipped");
    }

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
      List<Object[]> scenarioList = new ArrayList<>(features.size());
      for (CucumberFeature feature : features) {
        for (CucumberTagStatement element : feature.getFeatureElements()) {
          if (!isScenarioAllowed(element)) {
            continue;
          }
          scenarioList.add(new Object[]{new Pair<>(element, new CucumberFeatureWrapperImpl(feature))});
        }
      }
      return scenarioList.toArray(new Object[][]{});
    } catch (CucumberException ex) {
      return new Object[][]{new Object[]{new Pair<>(null, new CucumberExceptionWrapper(ex))}};
    }
  }
}
