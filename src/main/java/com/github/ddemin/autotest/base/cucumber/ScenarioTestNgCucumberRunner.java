package com.github.ddemin.autotest.base.cucumber;

import com.github.ddemin.autotest.base.conf.BaseConfig;
import cucumber.api.testng.CucumberExceptionWrapper;
import cucumber.api.testng.CucumberFeatureWrapperImpl;
import cucumber.api.testng.FeatureResultListener;
import cucumber.api.testng.TestNGCucumberRunner;
import cucumber.runtime.ClassFinder;
import cucumber.runtime.CucumberException;
import cucumber.runtime.Runtime;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.RuntimeOptionsFactory;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import cucumber.runtime.model.CucumberFeature;
import cucumber.runtime.model.CucumberTagStatement;
import gherkin.formatter.model.Tag;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.testng.SkipException;

@Slf4j
public class ScenarioTestNgCucumberRunner extends TestNGCucumberRunner {

  private static final String TAGS_PROPERTY = BaseConfig.TESTING.getTags();

  private Runtime runtime;
  private RuntimeOptions runtimeOptions;
  private ResourceLoader resourceLoader;
  private FeatureResultListener resultListener;
  private ClassLoader classLoader;

  public ScenarioTestNgCucumberRunner(Class clazz) {
    super(clazz);

    classLoader = clazz.getClassLoader();
    resourceLoader = new MultiLoader(classLoader);

    RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(clazz);
    runtimeOptions = runtimeOptionsFactory.create();

    ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader);
    resultListener = new FeatureResultListener(runtimeOptions.reporter(classLoader),
        runtimeOptions.isStrict());
    runtime = new Runtime(resourceLoader, classFinder, classLoader, runtimeOptions);
  }

  private static boolean isScenarioAllowed(CucumberTagStatement statement) {
    if (TAGS_PROPERTY == null || TAGS_PROPERTY.isEmpty()) {
      return isScenarioAllowedByName(statement);
    } else {
      return isScenarioAllowedByName(statement) && isScenarioAllowedByTags(statement);
    }
  }

  private static boolean isScenarioAllowedByName(CucumberTagStatement statement) {
    if (BaseConfig.TESTING.getScenarios() != null && !BaseConfig.TESTING.getScenarios().isEmpty()) {
      List<String> scenariosToRun = Arrays.asList(
          BaseConfig.TESTING.getScenarios().split(BaseConfig.TESTING.getScenariosDelimiter()));
      return scenariosToRun.contains(statement.getGherkinModel().getName());
    } else {
      return true;
    }
  }

  private static boolean isScenarioAllowedByTags(CucumberTagStatement statement) {
    List<String> requiredTags;
    List<String> excludedTags;
    List<String> providedTags = new ArrayList<>();
    List<String> currentScenarioTags = statement.getGherkinModel().getTags()
        .stream()
        .map(Tag::getName)
        .collect(Collectors.toList());

    if (TAGS_PROPERTY.matches("\\[.*\\]")) {
      providedTags = Arrays.asList(TAGS_PROPERTY.replaceAll("[\\[\\]]+", "").split(", "));
    } else {
      providedTags.add(TAGS_PROPERTY);
    }

    if (providedTags.size() > 1) {
      requiredTags = providedTags.stream().filter(it -> !it.startsWith("~"))
          .collect(Collectors.toList());
      excludedTags = providedTags.stream()
          .filter(it -> it.startsWith("~"))
          .map(it -> it.substring(1))
          .collect(Collectors.toList());
      return (requiredTags.isEmpty() || currentScenarioTags.containsAll(requiredTags))
          && currentScenarioTags.stream().noneMatch(excludedTags::contains);
    } else {
      requiredTags = Splitter.on(',').splitToList(TAGS_PROPERTY)
          .stream()
          .filter(it -> !it.startsWith("~"))
          .collect(Collectors.toList());
      excludedTags = providedTags.stream()
          .filter(it -> it.startsWith("~"))
          .map(it -> it.substring(1))
          .collect(Collectors.toList());
      return
          (requiredTags.isEmpty() || requiredTags.stream().anyMatch(currentScenarioTags::contains))
              && currentScenarioTags.stream().noneMatch(excludedTags::contains);
    }
  }

  public void runCucumber(Pair<CucumberTagStatement, CucumberFeatureWrapperImpl> scenarioWrapper) {
    if (scenarioWrapper == null) {
      throw new SkipException("Test skipped");
    }

    resultListener.startFeature();

    runtimeOptions.formatter(classLoader)
        .uri(scenarioWrapper.getValue().getCucumberFeature().getPath());
    runtimeOptions.formatter(classLoader)
        .feature(scenarioWrapper.getValue().getCucumberFeature().getGherkinFeature());

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
          scenarioList
              .add(new Object[]{new Pair<>(element, new CucumberFeatureWrapperImpl(feature))});
        }
      }
      return scenarioList.toArray(new Object[][]{});
    } catch (CucumberException ex) {
      log.warn(ex.getMessage());
      return new Object[][]{new Object[]{new Pair<>(null, new CucumberExceptionWrapper(ex))}};
    }
  }
}
