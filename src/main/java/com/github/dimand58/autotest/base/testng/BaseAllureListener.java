package com.github.dimand58.autotest.base.testng;

import java.util.Stack;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.qatools.allure.Allure;
import ru.yandex.qatools.allure.events.StepEvent;
import ru.yandex.qatools.allure.events.StepFailureEvent;
import ru.yandex.qatools.allure.events.StepFinishedEvent;
import ru.yandex.qatools.allure.events.StepStartedEvent;
import ru.yandex.qatools.allure.experimental.LifecycleListener;

@Slf4j(topic = "allure")
public class BaseAllureListener extends LifecycleListener {

  public static final ThreadLocal<Throwable> EXCEPTION_LAST = ThreadLocal.withInitial(() -> null);
  protected static final ThreadLocal<Stack<String>> STEPS_STACK = ThreadLocal.withInitial(Stack::new);

  public static void execAndFireStep(String stepTitle, Runnable someStepActions) {
    fireStepStarted(stepTitle);
    someStepActions.run();
    fireStepFinished();
  }

  public static void fireStepStarted(String stepTitle) {
    if (EXCEPTION_LAST.get() != null
        && !STEPS_STACK.get().isEmpty()
        && stepTitle.equalsIgnoreCase(STEPS_STACK.get().peek())) {
      return;
    }
    EXCEPTION_LAST.set(null);
    Allure.LIFECYCLE.fire(new StepStartedEvent(stepTitle).withTitle(stepTitle));
  }

  public static void fireStepFinished() {
    Allure.LIFECYCLE.fire(new StepFinishedEvent());
    EXCEPTION_LAST.set(null);
  }

  public static void finishAllStepsAsFailed(Throwable th) {
    while (!STEPS_STACK.get().isEmpty()) {
      Allure.LIFECYCLE.fire(new StepFailureEvent().withThrowable(th));
      Allure.LIFECYCLE.fire(new StepFinishedEvent());
    }
  }

  public static void finishAllStepsAsPassed() {
    while (!STEPS_STACK.get().isEmpty()) {
      Allure.LIFECYCLE.fire(new StepFinishedEvent());
    }
  }

  public static void saveLastEx(Throwable tr) {
    EXCEPTION_LAST.set(tr);
  }

  @Override
  public void fire(StepStartedEvent event) {
    log.info("STARTED  - {}", event.getTitle() == null ? event.getName() : event.getTitle());
    STEPS_STACK.get().push(event.getName());
  }

  @Override
  public void fire(StepEvent event) {
    if (event instanceof StepFailureEvent) {
      log.warn("FAILED  - {}", STEPS_STACK.get().peek());
    }
  }

  @Override
  public void fire(StepFinishedEvent event) {
    log.info("FINISHED - {}", STEPS_STACK.get().peek());
    STEPS_STACK.get().pop();
    EXCEPTION_LAST.set(null);
  }

}
