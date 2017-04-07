package com.github.ddemin.autotest.base.util;

import static com.github.ddemin.autotest.base.util.AllureHelper.*;
import static org.awaitility.Awaitility.*;

import com.github.ddemin.autotest.base.conf.*;

import java.util.concurrent.*;

import lombok.extern.slf4j.*;
import org.awaitility.core.*;
import org.hamcrest.*;

@Slf4j
public class HamcrestWrapper {

  public static void repeatAssertion(String reason, Runnable someCodeWithAsserts) {
    repeatAssertion(
        reason,
        someCodeWithAsserts,
        BaseConfig.TESTING.getAssertTimeout(),
        BaseConfig.TESTING.getAssertPoll()
    );
  }

  public static void repeatAssertion(String reason, Runnable someCodeWithAsserts, long timeout, long poll) {
    execAsStep(
        String.format("REPEATABLE (%d ms): %s", timeout, reason),
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
        () -> MatcherAssert.assertThat(reason, actual, matcher)
    );
  }

  public static void assertAsStep(String reason, boolean assertion) {
    execAsStep(
        String.format("%s (expected %s)", reason, assertion),
        () -> MatcherAssert.assertThat(reason, assertion)
    );
  }
}
