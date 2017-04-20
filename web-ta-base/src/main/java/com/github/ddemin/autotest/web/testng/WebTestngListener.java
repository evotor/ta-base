package com.github.ddemin.autotest.web.testng;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.hasWebDriverStarted;

import com.github.ddemin.autotest.base.util.*;

import lombok.extern.slf4j.*;
import org.openqa.selenium.*;
import org.testng.*;
import ru.yandex.qatools.allure.*;

@Slf4j(topic = "testng")
public class WebTestngListener implements ITestListener {

  static {
    Allure.LIFECYCLE.addListener(new WebAllureListener());
  }

  @Override
  public void onStart(ITestContext context) {
    Thread.currentThread().setName(context.getName());
  }


  @Override
  public void onTestFailure(ITestResult result) {
    if (hasWebDriverStarted()) {
      try {
        AllureHelper.attachPng(
            result.getThrowable().getMessage(),
            (((TakesScreenshot) getWebDriver()).getScreenshotAs(OutputType.BYTES))
        );
      } catch (WebDriverException ex) {
        log.error(ex.getMessage(), ex);
      }
    }
  }

  @Override
  public void onTestSuccess(ITestResult result) {
  }

  @Override
  public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    this.onTestFailure(result);
  }

  @Override
  public void onTestStart(ITestResult result) {
  }

  @Override
  public void onTestSkipped(ITestResult result) {
  }

  @Override
  public void onFinish(ITestContext context) {
  }
}
