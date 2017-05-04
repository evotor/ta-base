package com.github.ddemin.autotest.base.util;

import static com.github.ddemin.autotest.base.util.AllureHelper.execAsStep;
import static com.github.ddemin.autotest.base.util.HamcrestWrapper.assertAsStep;

import com.github.ddemin.autotest.base.testng.BaseAllureListener;
import com.google.common.base.Joiner;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hamcrest.Matcher;

public class SoftAssertHelper {

  private static final ThreadLocal<Set<AssertionError>> ASSERTIONS_SET = ThreadLocal
      .withInitial(LinkedHashSet::new);

  public static <T> void softAssert(String reason, T actual, Matcher<? super T> matcher) {
    softAssert(reason, () -> assertAsStep(reason, actual, matcher));
  }

  public static void softAssert(String checkName, Runnable code) {
    execAsStep(
        checkName,
        () -> {
          try {
            code.run();
          } catch (AssertionError assertionError) {
            ASSERTIONS_SET.get().add(assertionError);
            BaseAllureListener.fireStepFailed(new AssertionError(checkName));
            AllureHelper
                .attachText("Assertion trace", ExceptionUtils.getStackTrace(assertionError));
          }
        }
    );
  }

  public static void assertAll() {
    if (ASSERTIONS_SET.get().isEmpty()) {
      return;
    }

    String combinedTrace = Joiner.on(System.lineSeparator())
        .join(ASSERTIONS_SET.get()
            .stream()
            .map(ExceptionUtils::getStackTrace)
            .collect(Collectors.toList())
        );
    AllureHelper.attachText("Soft assertions trace", combinedTrace);

    String combinedMsg = System.lineSeparator()
        + Joiner.on(System.lineSeparator())
        .join(ASSERTIONS_SET.get()
            .stream()
            .map(Throwable::getMessage)
            .collect(Collectors.toList())
        );

    reset();

    throw new AssertionError(combinedMsg.replace("java.lang.AssertionError: ", ""));
  }

  public static void reset() {
    ASSERTIONS_SET.remove();
  }

}
