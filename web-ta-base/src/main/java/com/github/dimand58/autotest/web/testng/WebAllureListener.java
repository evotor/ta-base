package com.github.dimand58.autotest.web.testng;


import static com.codeborne.selenide.WebDriverRunner.*;

import com.github.dimand58.autotest.base.testng.*;
import com.github.dimand58.autotest.base.util.*;

import lombok.extern.slf4j.*;
import org.openqa.selenium.*;
import ru.yandex.qatools.allure.events.*;

@Slf4j(topic = "allure")
public class WebAllureListener extends BaseAllureListener {

  @Override
  public void fire(StepEvent event) {
    if (event instanceof StepFailureEvent
        && EXCEPTION_LAST.get() != null
        && !STEPS_STACK.get().isEmpty()
        && hasWebDriverStarted()) {
      try {
        AttachHelper.attachPng(EXCEPTION_LAST.get().getMessage(),
            ((TakesScreenshot) getWebDriver()).getScreenshotAs(OutputType.BYTES));
      } catch (WebDriverException ex) {
        log.error(ex.getMessage(), ex);
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
