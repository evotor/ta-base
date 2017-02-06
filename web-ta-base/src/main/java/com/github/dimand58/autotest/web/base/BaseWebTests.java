package com.github.dimand58.autotest.web.base;

import com.github.dimand58.autotest.web.webdriver.SelenideElementListener;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;

public class BaseWebTests {

  static {
    Configuration.startMaximized = true;
    Configuration.holdBrowserOpen = true;
    Configuration.screenshots = false;
    Configuration.savePageSource = false;
    Configuration.reopenBrowserOnFail = false;
    WebDriverRunner.addListener(new SelenideElementListener());
  }
}
