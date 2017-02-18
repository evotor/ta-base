package com.github.ddemin.autotest.web.base;

import com.github.ddemin.autotest.web.matcher.*;

import java.util.*;

import com.codeborne.selenide.*;
import lombok.*;
import org.openqa.selenium.*;

@AllArgsConstructor
public abstract class BaseBlock implements IWebBrowserArea, WebElement {

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

  public boolean isVisibleAtViewport() {
    return VisibleAtViewportMatcher.isVisibleAtViewport(getRoot())
        && getKeyElements().stream().allMatch(el -> VisibleAtViewportMatcher.isVisibleAtViewport(el));
  }

  @Override
  public void click() {
    getRoot().click();
  }

  @Override
  public void submit() {
    getRoot().submit();
  }

  @Override
  public void sendKeys(CharSequence... keysToSend) {
    getRoot().sendKeys(keysToSend);
  }

  @Override
  public void clear() {
    getRoot().clear();
  }

  @Override
  public String getTagName() {
    return getRoot().getTagName();
  }

  @Override
  public String getAttribute(String name) {
    return getRoot().getAttribute(name);
  }

  @Override
  public boolean isDisplayed() {
    return getRoot().isDisplayed()
        && getKeyElements().stream().allMatch(WebElement::isDisplayed);
  }

  @Override
  public boolean isSelected() {
    return getRoot().isSelected();
  }

  @Override
  public boolean isEnabled() {
    return getRoot().isEnabled();
  }

  @Override
  public String getText() {
    return getRoot().getText();
  }

  @Override
  public List<WebElement> findElements(By by) {
    return getRoot().findElements(by);
  }

  @Override
  public WebElement findElement(By by) {
    return getRoot().findElement(by);
  }

  @Override
  public Point getLocation() {
    return getRoot().getLocation();
  }

  @Override
  public Dimension getSize() {
    return getRoot().getSize();
  }

  @Override
  public Rectangle getRect() {
    return getRoot().getRect();
  }

  @Override
  public String getCssValue(String propertyName) {
    return getRoot().getCssValue(propertyName);
  }

  @Override
  public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
    return getRoot().getScreenshotAs(target);
  }
}
