package com.github.ddemin.autotest.web.util;

import static com.codeborne.selenide.WebDriverRunner.*;
import static org.testng.Assert.*;

import com.github.ddemin.autotest.base.util.*;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

import lombok.extern.slf4j.*;
import ru.yandex.qatools.ashot.*;
import ru.yandex.qatools.ashot.comparison.*;

@Slf4j
public class ImageDiffHelper {
  private static final ImageDiffer DIFFER = new ImageDiffer().withIgnoredColor(Color.MAGENTA);

  public static void softAssertThatScreenMatches(String checkName, String pathToStandart) {
    SeleniumHelper.waitWhileAjax();

    try {
      BufferedImage standartImg = ImageIO.read(ImageDiffHelper.class.getResourceAsStream(pathToStandart));
      BufferedImage actualImg = new AShot().takeScreenshot(getWebDriver()).getImage();
      ImageDiff diffImg = DIFFER.makeDiff(standartImg, actualImg);
      SoftAssertHelper.assertAndSave(checkName,
          () -> {
            if (diffImg.hasDiff()) {
              AttachHelper.attachImg("Standard image", standartImg);
              AttachHelper.attachImg("Actual image", actualImg);
              AttachHelper.attachImg("Diff image", diffImg.getTransparentMarkedImage());
              String errMsg = "Screenshot should be equal to standard: " + pathToStandart;
              fail(errMsg);
            }
          }
      );
    } catch (IOException ex) {
      log.error(ex.getMessage(), ex);
    }
  }
}
