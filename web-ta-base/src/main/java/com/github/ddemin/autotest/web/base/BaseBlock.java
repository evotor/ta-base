package com.github.ddemin.autotest.web.base;

import com.codeborne.selenide.*;
import lombok.*;
import org.openqa.selenium.*;

@AllArgsConstructor
public abstract class BaseBlock implements IWebBrowserArea {

  private SelenideElement root;

  public SelenideElement getRoot() {
    return root;
  }

  public SelenideElement getChild(String cssLocator) {
    return getRoot().$(cssLocator);
  }

  public SelenideElement getChild(By byLocator) {
    return getRoot().$(byLocator);
  }

  public ElementsCollection getChilds(String cssLocator) {
    return getRoot().$$(cssLocator);
  }

  public ElementsCollection getChilds(By byLocator) {
    return getRoot().$$(byLocator);
  }

  public BaseBlock hover() {
    getRoot().hover();
    return this;
  }

  public void click() {
    getRoot().click();
  }
}
