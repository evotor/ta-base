package com.github.ddemin.autotest.base.testng;

import org.testng.*;
import ru.yandex.qatools.allure.*;

public class BaseTestngListener implements ITestListener {

  static {
    Allure.LIFECYCLE.addListener(new BaseAllureListener());
  }

  @Override
  public void onTestSuccess(ITestResult result) {
    BaseAllureListener.finishAllStepsAsPassed();
  }

  @Override
  public void onTestFailure(ITestResult result) {
    BaseAllureListener.finishAllStepsAsFailed(result.getThrowable());
  }

  @Override
  public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    BaseAllureListener.finishAllStepsAsFailed(result.getThrowable());
  }

  @Override
  public void onTestStart(ITestResult result) {
    Thread.currentThread().setName(result.getMethod().getConstructorOrMethod().getName());
  }

  @Override
  public void onTestSkipped(ITestResult result) {
  }

  @Override
  public void onStart(ITestContext context) {
    Thread.currentThread().setName(context.getName());
  }

  @Override
  public void onFinish(ITestContext context) {
  }
}
