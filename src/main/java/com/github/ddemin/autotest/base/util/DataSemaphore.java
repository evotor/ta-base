package com.github.ddemin.autotest.base.util;

import static org.awaitility.Awaitility.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.*;

import com.github.ddemin.autotest.base.conf.*;

import java.util.*;
import java.util.concurrent.*;

import lombok.extern.slf4j.*;

@Slf4j(topic = "data_semaphore")
public class DataSemaphore {

  private static final Map<Object, Long> lockedDataMap = new HashMap();

  public static <T> T tryToLock(T someData) {
    return tryToLockOneOf(Arrays.asList(someData));
  }

  public static <T> T tryToLockOneOf(Collection<T> someDataCollection) {
    if (someDataCollection.contains(null)) {
      log.error("Test data contains NULL value");
      return null;
    }

    long currentThreadId = Thread.currentThread().getId();
    String currentThreadName = Thread.currentThread().getName();
    int interval = new Random().nextInt(2000) + 500;

    return
        await("Try to lock data from " + someDataCollection)
            .pollDelay(interval, TimeUnit.MILLISECONDS)
            .pollInterval(interval, TimeUnit.MILLISECONDS)
            .timeout(BaseConfig.TESTING.getTestDataTimeout(), TimeUnit.MILLISECONDS)
            .dontCatchUncaughtExceptions()
            .until(
                () -> {
                  synchronized (lockedDataMap) {
                    Thread.currentThread().setName("data locking for " + currentThreadName);

                    log.info("Try to lock data from {}", someDataCollection);

                    for (T it : someDataCollection) {
                      Map.Entry<Object, Long> rez = lockedDataMap.entrySet()
                          .stream()
                          .filter(entry -> entry.getKey() != null && entry.getKey().equals(it))
                          .findFirst()
                          .orElse(null);
                      if (rez == null || rez.getValue() == null) {
                        lockedDataMap.put(it, currentThreadId);
                        log.info("Data was locked successfully: {}", it);
                        return it;
                      } else if (rez.getValue() == currentThreadId) {
                        return it;
                      }
                    }
                    return null;
                  }
                },
                notNullValue()
            );
  }

  public static void releaseData() {
    synchronized (lockedDataMap) {
      for (Map.Entry<Object, Long> entry : lockedDataMap.entrySet()) {
        if (entry.getValue() != null && entry.getValue() == Thread.currentThread().getId()) {
          entry.setValue(null);
          log.info("Data was unlocked: {}", entry.getKey());
        }
      }
    }
  }

}
