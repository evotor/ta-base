package com.github.ddemin.autotest.base.testng;

import com.github.ddemin.autotest.base.util.ObjectsController;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import ru.yandex.qatools.allure.Allure;

@Slf4j
public class BaseTestngListener implements ITestListener {

  static {
    SLF4JBridgeHandler.removeHandlersForRootLogger();
    SLF4JBridgeHandler.install();
    Allure.LIFECYCLE.addListener(new BaseAllureListener());
  }

  @Override
  public void onTestSuccess(ITestResult result) {
    if (result.getStatus() == ITestResult.SUCCESS) {
      BaseAllureListener.finishAllStepsAsPassed();
      ObjectsController.releaseObjects();
    } else {
      onTestFailure(result);
    }
  }

  @Override
  public void onTestFailure(ITestResult result) {
    BaseAllureListener.finishAllStepsAsFailed(result.getThrowable());
    ObjectsController.releaseObjects();
  }

  @Override
  public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    onTestFailure(result);
  }

  @Override
  public void onTestStart(ITestResult result) {
    Thread.currentThread().setName(result.getMethod().getConstructorOrMethod().getName());
  }

  @Override
  public void onTestSkipped(ITestResult result) {
    ObjectsController.releaseObjects();
  }

  @Override
  public void onStart(ITestContext context) {
    Thread.currentThread().setName(context.getName());
  }

  @Override
  public void onFinish(ITestContext context) {
  }
}
