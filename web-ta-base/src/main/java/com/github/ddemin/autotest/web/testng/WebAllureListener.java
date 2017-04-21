package com.github.ddemin.autotest.web.testng;


import static com.codeborne.selenide.WebDriverRunner.*;

import com.github.ddemin.autotest.base.testng.*;
import com.github.ddemin.autotest.base.util.*;

import java.lang.reflect.*;
import java.util.*;

import com.google.common.primitives.*;
import lombok.extern.slf4j.*;
import org.openqa.selenium.*;
import ru.yandex.qatools.allure.events.*;

@Slf4j(topic = "allure")
public class WebAllureListener extends BaseAllureListener {

  private byte[] lastScreenshotBytes = null;

  @Override
  public void fire(StepEvent event) {
    if (event instanceof StepFailureEvent) {
      if (hasWebDriverStarted()) {
        try {
          byte[] currentScreenshotBytes = (((TakesScreenshot) getWebDriver()).getScreenshotAs(OutputType.BYTES));
          if (!Arrays.equals(currentScreenshotBytes, lastScreenshotBytes)) {
            lastScreenshotBytes = currentScreenshotBytes;
            AllureHelper.attachPng(
                ((StepFailureEvent) event).getThrowable().getMessage(),
                currentScreenshotBytes
            );
          }
        } catch (WebDriverException ex) {
          log.error(ex.getMessage(), ex);
          lastScreenshotBytes = null;
        }
      } else {
        lastScreenshotBytes = null;
      }
    }
  }

  @Override
  public void fire(StepFinishedEvent event) {
  }

  @Override
  public void fire(StepStartedEvent event) {
  }

}
