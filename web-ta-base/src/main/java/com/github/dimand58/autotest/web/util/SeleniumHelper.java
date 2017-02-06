package com.github.dimand58.autotest.web.util;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import lombok.Synchronized;
import org.openqa.selenium.UnhandledAlertException;

public class SeleniumHelper {

  private static final String JS_AJAX_WAITING
      = "return (typeof jQuery == 'undefined' || jQuery.active == 0) && document.readyState == 'complete';";
  private static final ThreadLocal<Long> LAST_CHECK_TIMESTAMP
      = ThreadLocal.withInitial(() -> System.currentTimeMillis());


  public static void waitWhileAjax() {
    long curTime = System.currentTimeMillis();
    if (curTime - LAST_CHECK_TIMESTAMP.get() < 100) {
      return;
    }

    int success = 0;
    int repeats = 3;
    long tmt = Configuration.timeout;

    Selenide.sleep(Configuration.pollingInterval);
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
    while (success < repeats && tmt > 0L);
  }
}
