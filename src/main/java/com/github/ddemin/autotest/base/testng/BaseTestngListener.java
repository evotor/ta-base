package com.github.ddemin.autotest.base.testng;

import com.github.ddemin.autotest.base.util.*;

import org.slf4j.bridge.*;
import org.testng.*;
import ru.yandex.qatools.allure.*;

public class BaseTestngListener implements ITestListener {

  static {
    SLF4JBridgeHandler.removeHandlersForRootLogger();
    SLF4JBridgeHandler.install();
    Allure.LIFECYCLE.addListener(new BaseAllureListener());
  }

  @Override
  public void onTestSuccess(ITestResult result) {
    BaseAllureListener.finishAllStepsAsPassed();
    DataSemaphore.releaseData();
  }

  @Override
  public void onTestFailure(ITestResult result) {
    BaseAllureListener.finishAllStepsAsFailed(result.getThrowable());
    DataSemaphore.releaseData();
  }

  @Override
  public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    BaseAllureListener.finishAllStepsAsFailed(result.getThrowable());
    DataSemaphore.releaseData();
  }

  @Override
  public void onTestStart(ITestResult result) {
    Thread.currentThread().setName(result.getMethod().getConstructorOrMethod().getName());
  }

  @Override
  public void onTestSkipped(ITestResult result) {
    DataSemaphore.releaseData();
  }

  @Override
  public void onStart(ITestContext context) {
    Thread.currentThread().setName(context.getName());
  }

  @Override
  public void onFinish(ITestContext context) {
  }

}
