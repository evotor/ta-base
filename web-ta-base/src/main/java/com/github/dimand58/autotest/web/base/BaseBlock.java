package com.github.dimand58.autotest.web.base;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.AllArgsConstructor;
import org.openqa.selenium.By;

@AllArgsConstructor
public abstract class BaseBlock extends BasePage {

  private SelenideElement root;

  public SelenideElement getRoot() {
    return root;
  }

  public SelenideElement iam() {
    return getRoot();
  }

  public SelenideElement getChild(String cssLocator) {
    return iam().$(cssLocator);
  }

  public SelenideElement getChild(By byLocator) {
    return iam().$(byLocator);
  }

  public ElementsCollection getChilds(String cssLocator) {
    return iam().$$(cssLocator);
  }

  public ElementsCollection getChilds(By byLocator) {
    return iam().$$(byLocator);
  }

  public BaseBlock hover() {
    iam().hover();
    return this;
  }

  public void click() {
    iam().click();
  }
}
