package com.github.ddemin.autotest.base.util;

import static com.github.ddemin.autotest.base.util.AllureHelper.*;
import static com.github.ddemin.autotest.base.util.HamcrestWrapper.assertAsStep;
import static org.hamcrest.MatcherAssert.*;

import com.github.ddemin.autotest.base.testng.*;

import java.util.*;
import java.util.stream.*;

import com.google.common.base.*;
import org.apache.commons.lang3.exception.*;
import org.hamcrest.*;

public class SoftAssertHelper {

  private static final ThreadLocal<Set<AssertionError>> ASSERTIONS_SET = ThreadLocal.withInitial(LinkedHashSet::new);

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
            AllureHelper.attachText("Assertion trace", ExceptionUtils.getStackTrace(assertionError));
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
