package com.github.ddemin.autotest.base.util;

import static com.github.ddemin.autotest.base.util.AllureHelper.execAsStep;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;

import com.github.ddemin.autotest.base.conf.BaseConfig;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.core.ConditionTimeoutException;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

@Slf4j
public class HamcrestWrapper {

  public static <T> void repeatAssertion(String reason, T actual, Matcher<? super T> matcher) {
    repeatAssertion(
        reason,
        actual,
        matcher,
        BaseConfig.TESTING.getAssertTimeout(),
        BaseConfig.TESTING.getAssertPoll()
    );
  }

  public static <T> void repeatAssertion(String reason, T actual, Matcher<? super T> matcher,
      long timeout, long poll) {
    execAsStep(
        String.format(
            "REPEATABLE (%d ms): %s (expected %s)",
            timeout,
            reason,
            new StringDescription().appendDescriptionOf(matcher)
        ),
        () -> {
          try {
            await(reason)
                .with().timeout(timeout, TimeUnit.MILLISECONDS)
                .and().pollInterval(poll, TimeUnit.MILLISECONDS)
                .and()
                .until(() -> assertThat(actual, matcher));
          } catch (ConditionTimeoutException ex) {
            throw new AssertionError(reason + System.lineSeparator() + ex.getMessage());
          }
        }
    );
  }

  public static void repeatAssertion(String reason, Runnable someCodeWithAsserts) {
    repeatAssertion(
        reason,
        someCodeWithAsserts,
        BaseConfig.TESTING.getAssertTimeout(),
        BaseConfig.TESTING.getAssertPoll()
    );
  }

  public static void repeatAssertion(String reason, Runnable someCodeWithAsserts, long timeout,
      long poll) {
    execAsStep(
        String.format(
            "REPEATABLE (%d ms): %s",
            timeout,
            reason
        ),
        () -> {
          try {
            await(reason)
                .with().timeout(timeout, TimeUnit.MILLISECONDS)
                .and().pollInterval(poll, TimeUnit.MILLISECONDS)
                .and()
                .until(someCodeWithAsserts);
          } catch (ConditionTimeoutException ex) {
            throw new AssertionError(reason + System.lineSeparator() + ex.getMessage());
          }
        }
    );
  }

  public static <T> void delayAssertion(String reason, T actual, Matcher<? super T> matcher) {
    delayAssertion(
        reason,
        actual,
        matcher,
        BaseConfig.TESTING.getAssertDelay()
    );
  }

  public static <T> void delayAssertion(String reason, T actual, Matcher<? super T> matcher,
      long delay) {
    try {
      Thread.sleep(delay);
    } catch (InterruptedException ex) {
      log.debug(ex.getMessage());
    }
    assertAsStep(
        String.format("DELAYED (%d ms): %s ", delay, reason),
        actual,
        matcher
    );
  }

  public static void delayAssertion(String reason, Runnable someCodeWithAsserts) {
    delayAssertion(reason, someCodeWithAsserts, BaseConfig.TESTING.getAssertDelay());
  }

  public static void delayAssertion(String reason, Runnable someCodeWithAsserts, long delay) {
    try {
      Thread.sleep(delay);
    } catch (InterruptedException ex) {
      log.debug(ex.getMessage());
    }
    execAsStep(
        String.format("DELAYED (%d ms): %s ", delay, reason),
        someCodeWithAsserts
    );
  }

  public static <T> void assertAsStep(String reason, T actual, Matcher<? super T> matcher) {
    execAsStep(String.format("%s (expected %s)",
        reason,
        new StringDescription().appendDescriptionOf(matcher)),
        () -> assertThat(actual, matcher)
    );
  }
}
