package com.github.ddemin.autotest.base.testng;

import com.github.ddemin.autotest.base.conf.*;
import com.github.ddemin.autotest.base.util.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

import com.google.common.base.*;
import lombok.extern.slf4j.*;
import org.apache.commons.collections.list.*;
import org.apache.commons.io.*;
import org.slf4j.bridge.*;
import org.testng.*;
import ru.yandex.qatools.allure.*;

@Slf4j
public class BaseTestngListener implements ITestListener, ISuiteListener {

  private static final java.util.List<String> FAILED_SCENARIOS = SynchronizedList.decorate(new ArrayList<String>());
  private static final String FAILED_SCENARIOS_FILE_PATH = "build/failed_scenarios.html";

  static {
    SLF4JBridgeHandler.removeHandlersForRootLogger();
    SLF4JBridgeHandler.install();
    Allure.LIFECYCLE.addListener(new BaseAllureListener());
  }

  @Override
  public void onTestSuccess(ITestResult result) {
    BaseAllureListener.finishAllStepsAsPassed();
    ObjectsController.releaseObjects();
  }

  @Override
  public void onTestFailure(ITestResult result) {
    BaseAllureListener.finishAllStepsAsFailed(result.getThrowable());
    ObjectsController.releaseObjects();
    FAILED_SCENARIOS.add(result.getTestName());
  }

  @Override
  public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    BaseAllureListener.finishAllStepsAsFailed(result.getThrowable());
    ObjectsController.releaseObjects();
    FAILED_SCENARIOS.add(result.getTestName());
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

  @Override
  public void onStart(ISuite suite) {
  }

  @Override
  public void onFinish(ISuite suite) {
    if (FAILED_SCENARIOS.isEmpty()) {
      return;
    }

    File failedScenariosFile = new File(FAILED_SCENARIOS_FILE_PATH);
    FileUtils.deleteQuietly(failedScenariosFile);

    try {
      FileUtils.write(
          failedScenariosFile,
          String.format(
                "<header>"
                  + " <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" charset=\"UTF-8\"/>"
                  + "</header>"
                  + "<body>"
                  + " <h2>Failed tests separated by %s</h2>"
                  + " <p>%s<p>"
                  + "</body>",
              BaseConfig.TESTING.getScenariosDelimiter(),
              Joiner.on(BaseConfig.TESTING.getScenariosDelimiter()).join(FAILED_SCENARIOS)
          ),
          Charset.forName("UTF-8")
      );
    } catch (IOException ex) {
      log.error(ex.getMessage());
    }
  }
}
