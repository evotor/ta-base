package com.github.ddemin.autotest.base.testng;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.qatools.allure.Allure;
import ru.yandex.qatools.allure.events.StepEvent;
import ru.yandex.qatools.allure.events.StepFailureEvent;
import ru.yandex.qatools.allure.events.StepFinishedEvent;
import ru.yandex.qatools.allure.events.StepStartedEvent;
import ru.yandex.qatools.allure.events.TestCaseFinishedEvent;
import ru.yandex.qatools.allure.events.TestCaseStartedEvent;
import ru.yandex.qatools.allure.experimental.LifecycleListener;

// TODO refactor EXCEPTION_LAST
@Slf4j(topic = "allure")
public class BaseAllureListener extends LifecycleListener {

  public static final ThreadLocal<Throwable> EXCEPTION_LAST = ThreadLocal.withInitial(() -> null);
  protected static final ThreadLocal<Stack<AllureStep>> STEPS_STACK = ThreadLocal
      .withInitial(Stack::new);
  protected static final ThreadLocal<Stack<String>> TESTS_STACK = ThreadLocal
      .withInitial(Stack::new);

  public static void fireStepStarted(String stepTitle) {
    if (EXCEPTION_LAST.get() != null
        && !STEPS_STACK.get().empty()
        && stepTitle.equalsIgnoreCase(STEPS_STACK.get().peek().getTitle())) {
      return;
    }
    EXCEPTION_LAST.set(null);
    Allure.LIFECYCLE.fire(new StepStartedEvent(stepTitle).withTitle(stepTitle));
  }

  public static void fireStepFinished() {
    Allure.LIFECYCLE.fire(new StepFinishedEvent());
    EXCEPTION_LAST.set(null);
  }

  public static void fireStepFailed() {
    Allure.LIFECYCLE.fire(new StepFailureEvent());
  }

  public static void fireStepFailed(Throwable tr) {
    Allure.LIFECYCLE.fire(new StepFailureEvent().withThrowable(tr));
  }

  public static void finishAllStepsAsFailed(Throwable th) {
    while (!STEPS_STACK.get().isEmpty()) {
      fireStepFailed(th);
      fireStepFinished();
    }
  }

  public static void finishAllStepsAsPassed() {
    while (!STEPS_STACK.get().isEmpty()) {
      fireStepFinished();
    }
  }

  public static void saveLastEx(Throwable tr) {
    EXCEPTION_LAST.set(tr);
  }

  @Override
  public void fire(TestCaseStartedEvent event) {
    TESTS_STACK.get().push(event.getName());
  }

  @Override
  public void fire(TestCaseFinishedEvent event) {
    if (!TESTS_STACK.get().empty()) {
      TESTS_STACK.get().pop();
    }
  }

  @Override
  public void fire(StepStartedEvent event) {
    AllureStep newStep =
        AllureStep
            .builder()
            .title(event.getTitle() == null ? event.getName() : event.getTitle())
            .childSteps(new ArrayList<>())
            .build();
    log.info("STARTED  - {}", newStep.getTitle());
    if (!STEPS_STACK.get().empty()) {
      STEPS_STACK.get().peek().getChildSteps().add(newStep);
    }
    STEPS_STACK.get().push(newStep);
  }

  @Override
  public void fire(StepEvent event) {
    if (event instanceof StepFailureEvent && !STEPS_STACK.get().empty()) {
      log.warn("FAILED  - {}", STEPS_STACK.get().peek().getTitle());
      STEPS_STACK.get().peek().setFailed(true);
    }
  }

  @Override
  public void fire(StepFinishedEvent event) {
    log.info("FINISHED - {}", STEPS_STACK.get().peek().getTitle());
    if (STEPS_STACK.get().pop().isFailed()) {
      fireStepFailed();
    }
  }

  @Getter
  @Setter
  @Builder
  public static class AllureStep {

    private String title;
    private boolean isFailed;
    private List<AllureStep> childSteps = new ArrayList<>();
  }
}
