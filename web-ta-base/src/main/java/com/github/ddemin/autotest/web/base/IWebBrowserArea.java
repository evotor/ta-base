package com.github.ddemin.autotest.web.base;

import java.util.*;
import java.util.concurrent.*;

import com.codeborne.selenide.*;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.*;

public interface IWebBrowserArea {

  List<WebElement> getKeyElements();

  default void execSomeCodeDuringWaiting() {
  }

  default boolean isDisplayed() {
    return getKeyElements().stream().allMatch(WebElement::isDisplayed);
  }

  default boolean isNonDisplayed() {
    return getKeyElements().stream().noneMatch(WebElement::isDisplayed);
  }

  default void waitUntilLoaded() {
    waitUntilLoaded(Configuration.timeout);
  }

  default void waitUntilLoaded(long msTimeout) {
    try {
      Selenide
          .Wait()
          .withMessage("All key elements should be loaded")
          .withTimeout(msTimeout, TimeUnit.MILLISECONDS)
          .until(
              (WebDriver wd) -> {
                execSomeCodeDuringWaiting();
                return this.isDisplayed();
              }
          );
    } catch (TimeoutException ex) {
      throw new AssertionError(ex);
    }
  }

  default void waitUntilDisappeared() {
    waitUntilDisappeared(Configuration.timeout);
  }

  default void waitUntilDisappeared(long msTimeout) {
    try {
      Selenide.Wait()
          .withMessage("All key elements should be disappeared")
          .withTimeout(msTimeout, TimeUnit.MILLISECONDS)
          .until((WebDriver wd) -> this.isNonDisplayed());
    } catch (TimeoutException ex) {
      throw new AssertionError(ex);
    }
  }
}
