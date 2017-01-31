package com.github.dimand58.autotest.base.api.testng;

import com.github.dimand58.autotest.base.api.base.ApiRequestFactory;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ApiTestngListener implements ITestListener {

  @Override
  public void onTestStart(ITestResult testResult) {
    ApiRequestFactory.REQUEST_MODS.remove();
  }

  @Override
  public void onStart(ITestContext testContext) {
    ApiRequestFactory.REQUEST_MODS.remove();
  }

  @Override
  public void onFinish(ITestContext testContext) {
    ApiRequestFactory.REQUEST_MODS.remove();
  }

  @Override
  public void onTestSuccess(ITestResult testResult) {
  }

  @Override
  public void onTestFailure(ITestResult testResult) {
  }

  @Override
  public void onTestSkipped(ITestResult testResult) {
  }

  @Override
  public void onTestFailedButWithinSuccessPercentage(ITestResult testResult) {
  }

}
