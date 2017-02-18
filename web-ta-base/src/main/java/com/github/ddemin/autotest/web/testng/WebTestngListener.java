package com.github.ddemin.autotest.web.testng;

import static com.codeborne.selenide.WebDriverRunner.*;

import com.github.ddemin.autotest.base.util.*;

import lombok.extern.slf4j.*;
import org.openqa.selenium.*;
import org.testng.*;
import ru.yandex.qatools.allure.*;
import ru.yandex.qatools.ashot.*;

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
        AttachHelper.attachImg(
            result.getThrowable().getMessage(),
            new AShot().takeScreenshot(getWebDriver()).getImage()
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
