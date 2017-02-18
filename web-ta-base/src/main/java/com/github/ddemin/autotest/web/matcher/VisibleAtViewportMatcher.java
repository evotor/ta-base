package com.github.ddemin.autotest.web.matcher;

import static com.codeborne.selenide.Selenide.*;
import static java.lang.Math.*;

import org.hamcrest.*;
import org.openqa.selenium.*;

public class VisibleAtViewportMatcher extends TypeSafeMatcher<WebElement> {

  @Override
  public void describeTo(final Description description) {
    description.appendText("element fully visible at current viewport");
  }

  @Override
  public boolean matchesSafely(final WebElement element) {
    return isVisibleAtViewport(element);
  }

  public static boolean isVisibleAtViewport(final WebElement element) {
    if (!element.isDisplayed()) {
      return false;
    }

    java.awt.Rectangle browserRect = new java.awt.Rectangle(
        toIntExact(executeJavaScript("return window.pageXOffset;")),
        toIntExact(executeJavaScript("return window.pageYOffset;")),
        toIntExact(executeJavaScript("return window.innerWidth;")),
        toIntExact(executeJavaScript("return window.innerHeight;"))
    );

    Dimension elementDimension = element.getSize();
    Point elementLocation = element.getLocation();
    java.awt.Rectangle elementRect = new java.awt.Rectangle(
        elementLocation.getX(),
        elementLocation.getY(),
        elementDimension.getWidth(),
        elementDimension.getHeight()
    );

    return browserRect.contains(elementRect);
  }

  public static VisibleAtViewportMatcher visibleAtViewport() {
    return new VisibleAtViewportMatcher();
  }
}