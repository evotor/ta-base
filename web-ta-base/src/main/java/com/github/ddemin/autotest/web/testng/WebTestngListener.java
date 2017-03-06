package com.github.ddemin.autotest.web.testng;

import lombok.extern.slf4j.*;
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
