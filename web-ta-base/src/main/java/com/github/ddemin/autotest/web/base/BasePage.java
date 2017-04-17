package com.github.ddemin.autotest.web.base;

import com.codeborne.selenide.*;
import lombok.*;

public abstract class BasePage<T extends BasePage> implements IWebBrowserArea<T> {

  public BasePage() {
    this(Configuration.timeout);
  }

  public BasePage(long timeout) {
    if (waitKeyElementsDuringConstruction()) {
      waitUntilLoaded(timeout);
    }
  }

}
