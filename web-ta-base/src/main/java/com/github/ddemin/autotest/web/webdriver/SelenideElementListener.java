package com.github.ddemin.autotest.web.webdriver;

import static com.codeborne.selenide.WebDriverRunner.*;
import static com.github.ddemin.autotest.base.testng.BaseAllureListener.*;

import com.github.ddemin.autotest.web.util.*;

import java.util.*;
import java.util.regex.*;

import lombok.*;
import lombok.extern.slf4j.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.events.*;

@Slf4j(topic = "selenide")
public class SelenideElementListener implements WebDriverEventListener {

  private static final ThreadLocal<String> LOCATOR_LAST = ThreadLocal.withInitial(() -> "");
  private static final Pattern REGEX_DRIVER = Pattern.compile("^\\[.*Driver: (.*)\\)] -> ");

  @Override
  @Synchronized
  public void beforeNavigateTo(String url, WebDriver driver) {
    fireStepStarted("Navigate to " + url);
    getWebDriver().manage().window().maximize();
    SeleniumHelper.waitWhileAjax();
  }

  @Override
  @Synchronized
  public void afterNavigateTo(String url, WebDriver driver) {
    SeleniumHelper.waitWhileAjax();
    fireStepFinished();
  }

  @Override
  public void beforeNavigateBack(WebDriver driver) {
    fireStepStarted("Navigate back");
  }

  @Override
  public void afterNavigateBack(WebDriver driver) {
    SeleniumHelper.waitWhileAjax();
    fireStepFinished();
  }

  @Override
  public void beforeNavigateForward(WebDriver driver) {
    fireStepStarted("Navigate forward");
  }

  @Override
  public void afterNavigateForward(WebDriver driver) {
    SeleniumHelper.waitWhileAjax();
    fireStepFinished();
  }

  @Override
  public void beforeNavigateRefresh(WebDriver driver) {
    fireStepStarted("Refresh page " + driver.getCurrentUrl());
  }

  @Override
  public void afterNavigateRefresh(WebDriver driver) {
    SeleniumHelper.waitWhileAjax();
    fireStepFinished();
  }

  @Override
  public void beforeFindBy(By by, WebElement element, WebDriver driver) {
    String parentLocator = "null";
    if (element != null) {
      parentLocator = element.toString().replaceFirst(REGEX_DRIVER.pattern(), "");
    }

    fireStepStarted(String.format("Search %s -> %s", parentLocator, by));

    if (LOCATOR_LAST.get().equalsIgnoreCase(by.toString())) {
      return;
    } else if (!LOCATOR_LAST.get().isEmpty()
        && parentLocator.contains(LOCATOR_LAST.get().substring(LOCATOR_LAST.get().indexOf(": ")))) {
      return;
    }
    LOCATOR_LAST.set(by.toString());
    SeleniumHelper.waitWhileAjax();
  }

  @Override
  public void afterFindBy(By by, WebElement element, WebDriver driver) {
    fireStepFinished();
  }

  @Override
  public void beforeClickOn(WebElement element, WebDriver driver) {
    fireStepStarted(String.format("Click on %s", element.toString().replaceFirst(REGEX_DRIVER.pattern(), "")));
  }

  @Override
  public void afterClickOn(WebElement element, WebDriver driver) {
    fireStepFinished();
  }

  @Override
  public void beforeChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysToSend) {
    fireStepStarted(String.format("Change %s to %s",
        element.toString().replaceFirst(REGEX_DRIVER.pattern(), ""),
        Objects.toString(keysToSend, "")));
  }

  @Override
  public void afterChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysToSend) {
    fireStepFinished();
  }

  @Override
  public void beforeScript(String script, WebDriver driver) {
    if (script.contains("readyState") || script.contains("jsErrors")) {
      return;
    }
    fireStepStarted(String.format("Execute script: %s ",
        script.substring(0, script.length() > 32 ? 32 : script.length() - 1).replace('\r', ' ').replace('\n', ' ')));
  }

  @Override
  public void afterScript(String script, WebDriver driver) {
    if (script.contains("readyState") || script.contains("jsErrors")) {
      return;
    }
    fireStepFinished();
  }

  @Override
  public void onException(Throwable throwable, WebDriver driver) {
    saveLastEx(throwable);
  }

}
