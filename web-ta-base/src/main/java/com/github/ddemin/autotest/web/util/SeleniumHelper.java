package com.github.ddemin.autotest.web.util;

import com.codeborne.selenide.*;
import org.openqa.selenium.*;

public class SeleniumHelper {

  // TODO move to config
  private static final int REPEATS = 2;
  private static final String JS_AJAX_WAITING
      = "return (typeof jQuery == 'undefined' || jQuery.active == 0) && document.readyState == 'complete';";
  private static final ThreadLocal<Long> LAST_CHECK_TIMESTAMP
      = ThreadLocal.withInitial(System::currentTimeMillis);
  private static final ThreadLocal<By> LOCATOR_LAST = ThreadLocal.withInitial(() -> null);

  public static void waitWhileAjax() {
    waitWhileAjax(null);
  }

  public static void waitWhileAjax(By element) {
    if (element != null && LOCATOR_LAST.get() != null && LOCATOR_LAST.get().equals(element)
        || System.currentTimeMillis() - LAST_CHECK_TIMESTAMP.get() < Configuration.timeout) {
      return;
    }
    LOCATOR_LAST.set(element);

    Selenide.sleep(Configuration.pollingInterval);

    int success = 0;
    long tmt = Configuration.timeout;
    long curTime = System.currentTimeMillis();
    do {
      Selenide.sleep(Configuration.pollingInterval);
      try {
        if (Selenide.executeJavaScript(JS_AJAX_WAITING)) {
          success++;
        }
      } catch (UnhandledAlertException ex) {
        break;
      } catch (Throwable ex) {
        if (ex.getMessage() != null
            && (ex.getMessage().contains("not reachable")
            || ex.getMessage().contains("from renderer"))) {
          throw ex;
        }
        success = 0;
      } finally {
        LAST_CHECK_TIMESTAMP.set(System.currentTimeMillis());
      }
      tmt -= System.currentTimeMillis() - curTime;
      curTime = System.currentTimeMillis();
    }
    while (success < REPEATS && tmt > 0L);
  }
}
