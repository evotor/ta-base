package com.github.dimand58.autotest.web.testng;


import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.hasWebDriverStarted;

import com.github.dimand58.autotest.base.testng.BaseAllureListener;
import com.github.dimand58.autotest.base.util.AttachHelper;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriverException;
import ru.yandex.qatools.allure.events.StepEvent;
import ru.yandex.qatools.allure.events.StepFailureEvent;
import ru.yandex.qatools.allure.events.StepFinishedEvent;
import ru.yandex.qatools.allure.events.StepStartedEvent;

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
