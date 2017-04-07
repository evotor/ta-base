package com.github.ddemin.autotest.web.testng;

import com.github.ddemin.autotest.web.webdriver.*;

import com.codeborne.selenide.*;

public class BaseWebTests {

  static {
    Configuration.screenshots = false;
    Configuration.savePageSource = false;
    WebDriverRunner.addListener(new SelenideElementListener());
  }
}
