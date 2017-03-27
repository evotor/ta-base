package com.github.ddemin.autotest.web.webdriver;

import static com.github.ddemin.autotest.base.testng.BaseAllureListener.*;

import com.github.ddemin.autotest.web.util.*;

import java.util.*;
import java.util.regex.*;

import lombok.*;
import lombok.extern.slf4j.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.events.*;

// TODO Improve extraction of By description from WebElement
@Slf4j(topic = "selenide")
public class SelenideElementListener extends AbstractWebDriverEventListener {

  private static final Pattern REGEX_DRIVER = Pattern.compile("^\\[.*Driver: (.*)\\)] -> ");

  @Override
  @Synchronized
  public void beforeNavigateTo(String url, WebDriver driver) {
    fireStepStarted("Navigate TO " + url);
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
    SeleniumHelper.waitWhileAjax();
  }

  @Override
  public void afterNavigateBack(WebDriver driver) {
    fireStepFinished();
  }

  @Override
  public void beforeNavigateForward(WebDriver driver) {
    fireStepStarted("Navigate forward");
    SeleniumHelper.waitWhileAjax();
  }

  @Override
  public void afterNavigateForward(WebDriver driver) {
    fireStepFinished();
  }

  @Override
  public void beforeNavigateRefresh(WebDriver driver) {
    fireStepStarted("Refresh page");
    SeleniumHelper.waitWhileAjax();
  }

  @Override
  public void afterNavigateRefresh(WebDriver driver) {
    fireStepFinished();
  }

  @Override
  public void beforeFindBy(By by, WebElement element, WebDriver driver) {
    SeleniumHelper.waitWhileAjax(by);
    log.info(
        "Find {}{}",
        element == null
            ? ""
            : String.format("PARENT: %s -> CHILD: ", element.toString().replaceFirst(REGEX_DRIVER.pattern(), "")),
        by.toString().replaceFirst(REGEX_DRIVER.pattern(), "")
    );
  }

  @Override
  public void afterFindBy(By by, WebElement element, WebDriver driver) {
  }

  @Override
  public void beforeClickOn(WebElement element, WebDriver driver) {
    fireStepStarted(String.format("Click ON: %s", element.toString().replaceFirst(REGEX_DRIVER.pattern(), "")));
  }

  @Override
  public void afterClickOn(WebElement element, WebDriver driver) {
    fireStepFinished();
  }

  @Override
  public void beforeChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysToSend) {
    fireStepStarted(String.format("Change ELEMENT: %s TO: \"%s\"",
        element.toString().replaceFirst(REGEX_DRIVER.pattern(), ""),
        Objects.toString(Arrays.toString(keysToSend), "")));
  }

  @Override
  public void afterChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysToSend) {
    fireStepFinished();
  }

  @Override
  public void beforeScript(String script, WebDriver driver) {
  }

  @Override
  public void afterScript(String script, WebDriver driver) {
  }

  @Override
  public void onException(Throwable throwable, WebDriver driver) {
    saveLastEx(throwable);
  }

}
