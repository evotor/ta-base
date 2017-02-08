package com.github.dimand58.autotest.base.util;

import com.github.dimand58.autotest.base.testng.*;

import java.util.*;
import java.util.stream.*;

import com.google.common.base.*;
import org.apache.commons.lang3.exception.*;

public class SoftAssertHelper {

  private static final ThreadLocal<Set<AssertionError>> ASSERTIONS_SET = ThreadLocal.withInitial(LinkedHashSet::new);

  public static void assertAndSave(String checkName, Runnable code) {
    BaseAllureListener.execAndFireStep(checkName,
        () -> {
          try {
            code.run();
          } catch (AssertionError assertionError) {
            ASSERTIONS_SET.get().add(assertionError);
            AttachHelper.attachText("Assertion trace", ExceptionUtils.getStackTrace(assertionError));
          }
        }
    );
  }

  public static void reset() {
    ASSERTIONS_SET.remove();
  }

  public static void assertAll() {
    if (ASSERTIONS_SET.get().isEmpty()) {
      return;
    }

    String combinedTrace = System.lineSeparator()
        + Joiner.on(System.lineSeparator())
        .join(ASSERTIONS_SET.get()
            .stream()
            .map(ExceptionUtils::getStackTrace)
            .collect(Collectors.toList())
        );
    AttachHelper.attachText("Combined trace", combinedTrace);

    String combinedMsg = System.lineSeparator()
        + Joiner.on(System.lineSeparator())
        .join(ASSERTIONS_SET.get()
            .stream()
            .map(Throwable::getMessage)
            .collect(Collectors.toList())
        );
    ASSERTIONS_SET.remove();

    throw new AssertionError(combinedMsg.replace("java.lang.AssertionError: ", ""));
  }

}
