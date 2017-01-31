package com.github.dimand58.autotest.base.util;

import com.github.dimand58.autotest.base.testng.BaseAllureListener;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.StringDescription;

public class HamcrestWrapper {

  public static <T> void wrapAssertion(String reason, T actual, Matcher<? super T> matcher) {
    BaseAllureListener.execAndFireStep(String.format("ASSERTION: %s (%s)",
        reason,
        new StringDescription().appendDescriptionOf(matcher)),
        () -> MatcherAssert.assertThat(reason, actual, matcher)
    );
  }

  public static void wrapAssertion(String reason, boolean assertion) {
    BaseAllureListener.execAndFireStep(
        "ASSERTION: " + reason,
        () -> MatcherAssert.assertThat(reason, assertion)
    );
  }
}
