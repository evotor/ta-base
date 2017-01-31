package com.github.dimand58.autotest.web.util;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.testng.Assert.fail;

import com.github.dimand58.autotest.base.util.AttachHelper;
import com.github.dimand58.autotest.base.util.SoftAssertHelper;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;

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
              AttachHelper.attachImg("Diff image", diffImg.getMarkedImage());
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
