package com.github.ddemin.autotest.web.base;

import java.util.*;
import java.util.concurrent.*;

import com.codeborne.selenide.*;
import org.openqa.selenium.*;
import org.openqa.selenium.TimeoutException;

public interface IWebBrowserArea {

  List<WebElement> getKeyElements();

  default void waitUntilLoaded() {
    waitUntilLoaded(Configuration.timeout);
  }

  default void waitUntilLoaded(long msTimeout) {
    try {
      Selenide.Wait()
          .withMessage("Key page elements should be loaded")
          .withTimeout(msTimeout, TimeUnit.MILLISECONDS)
          .until((WebDriver wd) ->
              getKeyElements().stream().allMatch(WebElement::isDisplayed)
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
      Selenide
          .Wait()
          .withTimeout(msTimeout, TimeUnit.MILLISECONDS)
          .until(
              (WebDriver wd) ->
                  getKeyElements().stream().noneMatch(WebElement::isDisplayed)
        );
    } catch (TimeoutException ex) {
      throw new AssertionError(ex);
    }
  }
}
