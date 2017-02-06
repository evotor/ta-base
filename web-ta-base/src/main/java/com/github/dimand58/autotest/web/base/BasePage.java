package com.github.dimand58.autotest.web.base;

import static com.codeborne.selenide.Selenide.$;

import java.util.List;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementsCollection;
import lombok.AllArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

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
