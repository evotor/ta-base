package com.github.ddemin.autotest.web.base;

import com.github.ddemin.autotest.web.webdriver.*;

import com.codeborne.selenide.*;

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
