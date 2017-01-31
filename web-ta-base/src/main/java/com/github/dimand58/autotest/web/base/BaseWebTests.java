package com.github.dimand58.autotest.web.base;

import static com.codeborne.selenide.WebDriverRunner.hasWebDriverStarted;

import com.github.dimand58.autotest.web.webdriver.SelenideElementListener;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.testng.annotations.AfterMethod;

public class BaseWebTests {

  static {
    Configuration.startMaximized = true;
    Configuration.screenshots = false;
    Configuration.savePageSource = false;
    Configuration.holdBrowserOpen = false;
    Configuration.reopenBrowserOnFail = false;
    WebDriverRunner.addListener(new SelenideElementListener());
  }

  @AfterMethod(alwaysRun = true)
  public void afterAnyTest() {
    if (hasWebDriverStarted()) {
      WebDriverRunner.getWebDriver().close();
    }
  }

}
