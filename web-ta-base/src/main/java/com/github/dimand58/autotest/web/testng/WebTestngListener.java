package com.github.dimand58.autotest.web.testng;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.hasWebDriverStarted;

import com.github.dimand58.autotest.base.util.AttachHelper;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriverException;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import ru.yandex.qatools.allure.Allure;
import ru.yandex.qatools.ashot.AShot;

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
