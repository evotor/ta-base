package com.github.ddemin.autotest.web.base;

import static com.codeborne.selenide.Selenide.*;

import java.util.*;

import com.codeborne.selenide.*;
import lombok.*;
import org.openqa.selenium.*;

@AllArgsConstructor
public abstract class BasePage {

  public abstract List<WebElement> getKeyElements();

  public abstract String getExpectedUrl();

  public void open() {
    open(Configuration.timeout);
  }

  public void open(long msTimeout) {
    Selenide.open(getExpectedUrl());
    waitUntilLoaded(msTimeout);
  }

  public void waitUntilLoaded() {
    waitUntilLoaded(Configuration.timeout);
  }

  public void waitUntilLoaded(long msTimeout) {
    getKeyElements().forEach(it -> $(it).waitUntil(Condition.visible, msTimeout));
  }

  public void waitUntilDisappeared() {
    waitUntilDisappeared(Configuration.timeout);
  }

  public void waitUntilDisappeared(long msTimeout) {
    getKeyElements().forEach(it -> $(it).waitWhile(Condition.visible, msTimeout));
  }

}
