package com.github.ddemin.autotest.web.base;

import com.codeborne.selenide.*;
import lombok.*;

public abstract class BasePage<T extends BasePage> implements IWebBrowserArea<T> {

  public BasePage() {
    waitUntilLoaded();
  }

  public BasePage(long timeout) {
    waitUntilLoaded(timeout);
  }

}
