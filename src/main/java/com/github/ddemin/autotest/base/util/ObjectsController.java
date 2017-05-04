package com.github.ddemin.autotest.base.util;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.core.IsNull.notNullValue;

import com.github.ddemin.autotest.base.conf.BaseConfig;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "data_controller")
public class ObjectsController {

  private static final Map<Object, Long> lockedObjectsMap = new HashMap<>();

  public static <T> T tryToLockOneOf(Collection<T> objectsCollection) {
    return tryToLock(objectsCollection, false);
  }

  public static <T> T tryToLockOneMore(Collection<T> objectsCollection) {
    return tryToLock(objectsCollection, true);
  }

  public static <T> T tryToLock(T object) {
    return tryToLockOneOf(Arrays.asList(object));
  }

  public static <T> T tryToLock(Collection<T> objectsCollection, boolean oneMoreRequired) {
    if (objectsCollection.contains(null)) {
      log.error("Objects collection contains NULL value");
      return null;
    }

    long currentThreadId = Thread.currentThread().getId();
    String currentThreadName = Thread.currentThread().getName();
    int interval = new Random().nextInt(2000) + 500;

    return
        await("Try to lock object from " + objectsCollection)
            .pollDelay(interval, TimeUnit.MILLISECONDS)
            .pollInterval(interval, TimeUnit.MILLISECONDS)
            .timeout(BaseConfig.TESTING.getTestDataTimeout(), TimeUnit.MILLISECONDS)
            .dontCatchUncaughtExceptions()
            .until(
                () -> {
                  synchronized (lockedObjectsMap) {
                    Thread.currentThread().setName("object locking for " + currentThreadName);

                    log.info("Try to lock object from {}", objectsCollection);

                    for (T it : objectsCollection) {
                      Map.Entry<Object, Long> rez = lockedObjectsMap.entrySet()
                          .stream()
                          .filter(entry -> entry.getKey() != null && entry.getKey().equals(it))
                          .findFirst()
                          .orElse(null);
                      if (rez == null || rez.getValue() == null) {
                        lockedObjectsMap.put(it, currentThreadId);
                        log.info("Object was locked: {}", it);
                        return it;
                      } else if (rez.getValue() == currentThreadId && !oneMoreRequired) {
                        return it;
                      }
                    }
                    return null;
                  }
                },
                notNullValue()
            );
  }

  public static void releaseObjects() {
    synchronized (lockedObjectsMap) {
      for (Map.Entry<Object, Long> entry : lockedObjectsMap.entrySet()) {
        if (entry.getValue() != null && entry.getValue() == Thread.currentThread().getId()) {
          entry.setValue(null);
          log.info("Object was released: {}", entry.getKey());
        }
      }
    }
  }

}
