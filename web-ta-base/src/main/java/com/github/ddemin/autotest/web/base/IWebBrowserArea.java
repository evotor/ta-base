package com.github.ddemin.autotest.web.base;

import java.util.*;
import java.util.concurrent.*;

import com.codeborne.selenide.*;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.*;

public interface IWebBrowserArea<T extends IWebBrowserArea> {

  List<WebElement> getKeyElements();

  default void execSomeCodeDuringWaiting() {
  }

  default boolean isDisplayed() {
    return getKeyElements().stream().allMatch(WebElement::isDisplayed);
  }

  default boolean isNonDisplayed() {
    return getKeyElements().stream().noneMatch(WebElement::isDisplayed);
  }

  default T waitUntilLoaded() {
    return waitUntilLoaded(Configuration.timeout);
  }

  default T waitUntilLoaded(long msTimeout) {
    try {
      Selenide.Wait()
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
    return (T) this;
  }

  default T waitUntilDisappeared() {
    return waitUntilDisappeared(Configuration.timeout);
  }

  default T waitUntilDisappeared(long msTimeout) {
    try {
      Selenide.Wait()
          .withMessage("All key elements should be disappeared")
          .withTimeout(msTimeout, TimeUnit.MILLISECONDS)
          .until((WebDriver wd) -> this.isNonDisplayed());
    } catch (TimeoutException ex) {
      throw new AssertionError(ex);
    }
    return (T) this;
  }
}
