package com.github.ddemin.autotest.base.cucumber;

import com.github.ddemin.autotest.base.util.ObjectsController;
import cucumber.runtime.StepDefinitionMatch;
import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.Background;
import gherkin.formatter.model.Examples;
import gherkin.formatter.model.ExamplesTableRow;
import gherkin.formatter.model.Feature;
import gherkin.formatter.model.Match;
import gherkin.formatter.model.Result;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.ScenarioOutline;
import gherkin.formatter.model.Step;
import gherkin.formatter.model.Tag;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.qatools.allure.Allure;
import ru.yandex.qatools.allure.annotations.Description;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Issue;
import ru.yandex.qatools.allure.annotations.Issues;
import ru.yandex.qatools.allure.annotations.Severity;
import ru.yandex.qatools.allure.annotations.Stories;
import ru.yandex.qatools.allure.annotations.TestCaseId;
import ru.yandex.qatools.allure.config.AllureModelUtils;
import ru.yandex.qatools.allure.events.MakeAttachmentEvent;
import ru.yandex.qatools.allure.events.StepCanceledEvent;
import ru.yandex.qatools.allure.events.StepFailureEvent;
import ru.yandex.qatools.allure.events.StepFinishedEvent;
import ru.yandex.qatools.allure.events.StepStartedEvent;
import ru.yandex.qatools.allure.events.TestCaseCanceledEvent;
import ru.yandex.qatools.allure.events.TestCaseFailureEvent;
import ru.yandex.qatools.allure.events.TestCaseFinishedEvent;
import ru.yandex.qatools.allure.events.TestCaseStartedEvent;
import ru.yandex.qatools.allure.events.TestSuiteFinishedEvent;
import ru.yandex.qatools.allure.events.TestSuiteStartedEvent;
import ru.yandex.qatools.allure.model.DescriptionType;
import ru.yandex.qatools.allure.model.SeverityLevel;
import ru.yandex.qatools.allure.utils.AnnotationManager;

/**
 * EPAM AllureReporter which was modified and adapted for multi-threading.
 * NEEDS   REFACTORING !!!
 */
// TODO Refactoring
@Slf4j(topic = "cucumber")
public class AllureReporter implements Reporter, Formatter {

  private static final String SCENARIO_OUTLINE = "Scenario Outline";
  private static final String FAILED = "failed";
  private static final String SKIPPED = "skipped";
  private static final String PASSED = "passed";

  private static final Pattern PATTERN_SEVERITY = Pattern.compile("@SeverityLevel\\.(.+)");
  private static final Pattern PATTERN_ISSUE = Pattern.compile("@Issue\\(\"+?([^\"]+)\"+?\\)");
  private static final Pattern PATTER_TC_ID = Pattern.compile("@TestCaseId\\(\"+?([^\"]+)\"+?\\)");
  private static final Pattern PATTERN_STORIES_ID = Pattern
      .compile("@Stories\\(\"+?([^\"]+)\"+?\\)");

  private static final ThreadLocal<Feature> FEATURE = ThreadLocal.withInitial(() -> (Feature) null);
  private static final ThreadLocal<List<Stories>> STORIES_FEATURE = ThreadLocal
      .withInitial(ArrayList::new);
  private static final ThreadLocal<List<Stories>> STORIES_SCENARIO = ThreadLocal
      .withInitial(ArrayList::new);
  private static final ThreadLocal<StepDefinitionMatch> STEP_MATCH =
      ThreadLocal.withInitial(() -> (StepDefinitionMatch) null);
  private static final ThreadLocal<LinkedList<Step>> STEPS =
      ThreadLocal.withInitial(LinkedList::new);
  private static final ThreadLocal<Boolean> FLAG_STEPS_ALLOWED =
      ThreadLocal.withInitial(() -> false);
  private static final ThreadLocal<Queue<ExamplesTableRow>> EXAMPLES_ROWS =
      ThreadLocal.withInitial(LinkedList::new);
  private static final ThreadLocal<String> UID = ThreadLocal.withInitial(() -> null);
  private static final ThreadLocal<String> STATUS = ThreadLocal.withInitial(() -> null);
  private static final ThreadLocal<Long> CNT = ThreadLocal.withInitial(() -> 0L);

  @Override
  public void syntaxError(
      String state, String event, List<String> legalEvents, String uri, Integer line) {
  }

  @Synchronized
  @Override
  public void feature(Feature feature) {
    log.info("FEATURE - STARTED - {}", feature.getName());
    Thread.currentThread().setName(feature.getName() + " " + Thread.currentThread().getId());

    FEATURE.set(feature);
    UID.set(UUID.randomUUID().toString());

    Collection<Annotation> annotations = new ArrayList<>();
    annotations.add(getDescriptionAnnotation(feature.getDescription()));
    annotations.add(getFeaturesAnnotation(feature.getName()));

    for (Tag tag : feature.getTags()) {
      Matcher matcher = PATTERN_STORIES_ID.matcher(tag.getName());
      if (matcher.matches()) {
        final String story = matcher.group(1);
        STORIES_FEATURE.get().add(getStoriesAnnotation(story));
      }
    }

    AnnotationManager am = new AnnotationManager(annotations);
    TestSuiteStartedEvent event = new TestSuiteStartedEvent(UID.get(), feature.getName());
    am.update(event);
    Allure.LIFECYCLE.fire(event);
  }

  @Override
  public void examples(Examples examples) {
    EXAMPLES_ROWS.get().addAll(examples.getRows());
    EXAMPLES_ROWS.get().poll();
  }

  @Synchronized
  @Override
  public void startOfScenarioLifeCycle(Scenario scenario) {
    if (SCENARIO_OUTLINE.equals(scenario.getKeyword())) {
      STEPS.get().clear();
    } else {
      FLAG_STEPS_ALLOWED.set(true);
    }

    STATUS.set(PASSED);

    String scenarioName;
    if (EXAMPLES_ROWS.get().size() > 0) {
      List<String> cells = EXAMPLES_ROWS.get().poll().getCells();
      scenarioName = getParametized(cells, scenario.getName());
    } else {
      scenarioName = scenario.getName();
    }
    Thread.currentThread().setName(scenarioName);

    TestCaseStartedEvent event = new TestCaseStartedEvent(UID.get(), scenarioName);
    event.setTitle(scenarioName);

    Collection<Annotation> annotations = new ArrayList<>();
    SeverityLevel level = getSeverityLevel(scenario);
    if (level != null) {
      annotations.add(getSeverityAnnotation(level));
    }

    Issues issues = getIssuesAnnotation(scenario);
    if (issues != null) {
      annotations.add(issues);
    }

    TestCaseId testCaseId = getTestCaseIdAnnotation(scenario);
    if (testCaseId != null) {
      annotations.add(testCaseId);
    }

    if (!STORIES_FEATURE.get().isEmpty()) {
      STORIES_SCENARIO.get().addAll(STORIES_FEATURE.get());
    }
    if (getStoriesAnnotation(scenario) != null) {
      STORIES_SCENARIO.get().add(getStoriesAnnotation(scenario));
    }
    if (STORIES_SCENARIO.get().isEmpty()) {
      STORIES_SCENARIO.get().add(getStoriesAnnotation(scenario.getName().replace("%s", "").trim()));
    }

    annotations.add(getFeaturesAnnotation(FEATURE.get().getName()));
    annotations.addAll(STORIES_SCENARIO.get());
    annotations.add(getDescriptionAnnotation(scenario.getDescription()));

    AnnotationManager am = new AnnotationManager(annotations);
    am.update(event);

    event.withLabels(AllureModelUtils.createTestFrameworkLabel("CucumberJVM"));
    Allure.LIFECYCLE.fire(event);
  }

  @Override
  public void scenario(Scenario scenario) {
    FLAG_STEPS_ALLOWED.set(true);
  }

  @Override
  public void step(Step step) {
    if (FLAG_STEPS_ALLOWED.get()) {
      STEPS.get().add(step);
    }
  }

  @Synchronized
  @Override
  public void endOfScenarioLifeCycle(Scenario scenario) {
    while (STEPS.get().peek() != null) {
      fireCanceledStep(STEPS.get().remove());
    }
    Allure.LIFECYCLE.fire(new TestCaseFinishedEvent());
    FLAG_STEPS_ALLOWED.set(false);
    STORIES_SCENARIO.get().clear();
    ObjectsController.releaseObjects();
  }

  @Synchronized
  @Override
  public void eof() {
    Allure.LIFECYCLE.fire(new TestSuiteFinishedEvent(UID.get()));
    UID.set(null);
    EXAMPLES_ROWS.remove();
    STORIES_FEATURE.get().clear();
    BaseContextStore.clean();
    ObjectsController.releaseObjects();
  }

  //@Synchronized
  @Override
  public void result(Result result) {
    if (STEP_MATCH.get() != null) {
      if (FAILED.equals(result.getStatus())) {
        Allure.LIFECYCLE.fire(new StepFailureEvent().withThrowable(result.getError()));
        Allure.LIFECYCLE.fire(new TestCaseFailureEvent().withThrowable(result.getError()));
        STATUS.set(FAILED);
      } else if (SKIPPED.equals(result.getStatus())) {
        Allure.LIFECYCLE.fire(new StepCanceledEvent());
        if (PASSED.equals(STATUS.get())) {
          //not to change FAILED status to CANCELED in the report
          Allure.LIFECYCLE.fire(new TestCaseCanceledEvent());
          STATUS.set(SKIPPED);
        }
      }
      Allure.LIFECYCLE.fire(new StepFinishedEvent());
      STEP_MATCH.set(null);
    }
  }

  @Synchronized
  @Override
  public void match(Match match) {
    if (match instanceof StepDefinitionMatch) {
      STEP_MATCH.set((StepDefinitionMatch) match);

      Step step = extractStep(STEP_MATCH.get());

      while (STEPS.get().peek() != null && !isEqualSteps(step, STEPS.get().peek())) {
        fireCanceledStep(STEPS.get().remove());
      }

      if (isEqualSteps(step, STEPS.get().peek())) {
        STEPS.get().remove();
      }

      String name = STEP_MATCH.get().getStepLocation().getMethodName();
      Allure.LIFECYCLE.fire(new StepStartedEvent(name).withTitle(name));
    }
  }

  @Synchronized
  @Override
  public void embedding(String mimeType, byte[] data) {
    Allure.LIFECYCLE.fire(new MakeAttachmentEvent(data, "attachment" + CNT.get(), mimeType));
    CNT.set(CNT.get() + 1);
  }

  @Synchronized
  @Override
  public void write(String text) {
    byte[] bytes;
    try {
      bytes = text.getBytes("UTF-8");
    } catch (UnsupportedEncodingException ex) {
      throw new RuntimeException(ex);
    }
    Allure.LIFECYCLE.fire(new MakeAttachmentEvent(bytes, "message" + CNT.get(), "text/plain"));
    CNT.set(CNT.get() + 1);
  }

  @Override
  public void uri(String uri) {
  }

  @Override
  public void scenarioOutline(ScenarioOutline scenarioOutline) {
  }

  @Override
  public void background(Background background) {
  }

  @Override
  public void done() {
  }

  @Override
  public void close() {
  }

  @Override
  public void before(Match match, Result result) {

  }

  @Override
  public void after(Match match, Result result) {
  }

  private Step extractStep(StepDefinitionMatch match) {
    try {
      Field step = match.getClass().getDeclaredField("step");
      step.setAccessible(true);
      return (Step) step.get(match);
    } catch (ReflectiveOperationException ex) {
      log.error(ex.getMessage(), ex);
      throw new RuntimeException(ex);
    }
  }

  private boolean isEqualSteps(Step step, Step gherkinStep) {
    return Objects.equals(step.getLine(), gherkinStep == null ? null : gherkinStep.getLine());
  }

  private SeverityLevel getSeverityLevel(Scenario scenario) {
    SeverityLevel level = null;
    List<SeverityLevel> severityLevels =
        Arrays.asList(
            SeverityLevel.BLOCKER,
            SeverityLevel.CRITICAL,
            SeverityLevel.NORMAL,
            SeverityLevel.MINOR,
            SeverityLevel.TRIVIAL
        );
    for (Tag tag : scenario.getTags()) {
      Matcher matcher = PATTERN_SEVERITY.matcher(tag.getName());
      if (matcher.matches()) {
        SeverityLevel levelTmp;
        String levelString = matcher.group(1);
        try {
          levelTmp = SeverityLevel.fromValue(levelString.toLowerCase());
        } catch (IllegalArgumentException ex) {
          log.warn(
              String.format(
                  "Unexpected Severity level [%s]. SeverityLevel.NORMAL will be used instead",
                  levelString));
          levelTmp = SeverityLevel.NORMAL;
        }

        if (level == null || severityLevels.indexOf(levelTmp) < severityLevels.indexOf(level)) {
          level = levelTmp;
        }
      }
    }
    return level;
  }

  @Synchronized
  private void fireCanceledStep(Step unimplementedStep) {
    String name = unimplementedStep.getName();
    Allure.LIFECYCLE.fire(new StepStartedEvent(name).withTitle(name));
    Allure.LIFECYCLE.fire(new StepCanceledEvent());
    Allure.LIFECYCLE.fire(new StepFinishedEvent());
    if (PASSED.equals(STATUS.get())) {
      Allure.LIFECYCLE.fire(
          new TestCaseCanceledEvent() {

            @Override
            protected String getMessage() {
              return "Unimplemented steps were found";
            }
          });
      STATUS.set(SKIPPED);
    }
  }

  @SuppressWarnings("BadAnnotationImplementation")
  private Description getDescriptionAnnotation(final String description) {
    return new Description() {

      @Override
      public String value() {
        return description;
      }

      @Override
      public DescriptionType type() {
        return DescriptionType.TEXT;
      }

      @Override
      public Class<? extends Annotation> annotationType() {
        return Description.class;
      }
    };
  }

  @SuppressWarnings("BadAnnotationImplementation")
  private Features getFeaturesAnnotation(final String value) {
    return new Features() {

      @Override
      public String[] value() {
        return new String[]{value};
      }

      @Override
      public Class<Features> annotationType() {
        return Features.class;
      }
    };
  }

  @SuppressWarnings("BadAnnotationImplementation")
  private Severity getSeverityAnnotation(final SeverityLevel value) {
    return new Severity() {

      @Override
      public SeverityLevel value() {
        return value;
      }

      @Override
      public Class<Severity> annotationType() {
        return Severity.class;
      }
    };
  }

  @SuppressWarnings("BadAnnotationImplementation")
  private Issues getIssuesAnnotation(Scenario scenario) {
    List<String> issues = new ArrayList<>();
    for (Tag tag : scenario.getTags()) {
      Matcher matcher = PATTERN_ISSUE.matcher(tag.getName());
      if (matcher.matches()) {
        issues.add(matcher.group(1));
      }
    }
    return issues.size() > 0 ? getIssuesAnnotation(issues) : null;
  }

  @SuppressWarnings("BadAnnotationImplementation")
  private Issues getIssuesAnnotation(List<String> issues) {
    final Issue[] values = createIssuesArray(issues);
    return new Issues() {

      @Override
      public Issue[] value() {
        return values;
      }

      @Override
      public Class<Issues> annotationType() {
        return Issues.class;
      }
    };
  }

  @SuppressWarnings("BadAnnotationImplementation")
  private Issue[] createIssuesArray(List<String> issues) {
    ArrayList<Issue> values = new ArrayList<>();
    for (final String issue : issues) {
      values.add(
          new Issue() {

            @Override
            public Class<Issue> annotationType() {
              return Issue.class;
            }

            @Override
            public String value() {
              return issue;
            }
          });
    }

    return values.toArray(new Issue[values.size()]);
  }

  @SuppressWarnings("BadAnnotationImplementation")
  private TestCaseId getTestCaseIdAnnotation(Scenario scenario) {
    for (Tag tag : scenario.getTags()) {
      Matcher matcher = PATTER_TC_ID.matcher(tag.getName());
      if (matcher.matches()) {
        final String testCaseId = matcher.group(1);
        return new TestCaseId() {

          @Override
          public String value() {
            return testCaseId;
          }

          @Override
          public Class<TestCaseId> annotationType() {
            return TestCaseId.class;
          }
        };
      }
    }

    return null;
  }

  @SuppressWarnings("BadAnnotationImplementation")
  private Stories getStoriesAnnotation(final String value) {
    return new Stories() {

      @Override
      public String[] value() {
        return new String[]{value};
      }

      @Override
      public Class<Stories> annotationType() {
        return Stories.class;
      }
    };
  }

  @SuppressWarnings("BadAnnotationImplementation")
  private Stories getStoriesAnnotation(Scenario scenario) {
    for (Tag tag : scenario.getTags()) {
      Matcher matcher = PATTERN_STORIES_ID.matcher(tag.getName());
      if (matcher.matches()) {
        final String story = matcher.group(1);
        return new Stories() {
          @Override
          public Class<Stories> annotationType() {
            return Stories.class;
          }

          @Override
          public String[] value() {
            return new String[]{story};
          }
        };
      }
    }
    return null;
  }

  private String getParametized(List<String> cells, String originalValue) {
    String expression = originalValue;
    for (int i = 0; i < cells.size(); i++) {
      expression = expression.replace("{" + i + "}", cells.get(i));
    }
    return expression;
  }
}
