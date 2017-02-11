package com.github.ddemin.autotest.web.base;

import com.codeborne.selenide.*;
import lombok.*;

@AllArgsConstructor
public abstract class BasePage implements IWebBrowserArea {

  public abstract String getExpectedUrl();

  public void open() {
    open(Configuration.timeout);
  }

  public void open(long msTimeout) {
    Selenide.open(getExpectedUrl());
    waitUntilLoaded(msTimeout);
  }

}
