package com.github.ddemin.autotest.web.matcher;

import com.github.ddemin.autotest.base.util.*;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

import lombok.extern.slf4j.*;
import org.hamcrest.*;
import ru.yandex.qatools.ashot.comparison.*;

@Slf4j
public class ScreenshotDiffMatcher extends TypeSafeMatcher<BufferedImage> {
  private static final Color COLOR_IGNORED = Color.MAGENTA;
  private static final ImageDiffer DIFFER = new ImageDiffer().withIgnoredColor(COLOR_IGNORED);
  private final String pathToStandart;

  public ScreenshotDiffMatcher(String pathToStandart) {
    this.pathToStandart = pathToStandart;
  }

  public static boolean hasDiff(final BufferedImage actualScreenshot, final String pathToStandart) {
    try {
      BufferedImage standartImg = ImageIO.read(ScreenshotDiffMatcher.class.getResourceAsStream(pathToStandart));
      ImageDiff diffImg = DIFFER.makeDiff(standartImg, actualScreenshot);
      if (diffImg.hasDiff()) {
        AttachHelper.attachImg("Standard image", standartImg);
        AttachHelper.attachImg("Actual image", actualScreenshot);
        AttachHelper.attachImg("Diff image", diffImg.getTransparentMarkedImage());
      }
      return diffImg.hasDiff();
    } catch (IOException ex) {
      throw new AssertionError("Some IOException occurred", ex);
    }
  }

  public static ScreenshotDiffMatcher hasDiffWithTemplate(String pathToStandart) {
    return new ScreenshotDiffMatcher(pathToStandart);
  }

  @Override
  public void describeTo(final Description description) {
    description.appendText(
        String.format("screenshot fully match the template %s ignoring %s pixels",
            pathToStandart,
            COLOR_IGNORED.toString()
        ));
  }

  @Override
  public boolean matchesSafely(final BufferedImage actualScreenshot) {
    return hasDiff(actualScreenshot, pathToStandart);
  }
}
