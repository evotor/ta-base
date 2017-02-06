package com.github.dimand58.autotest.base.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import javax.annotation.CheckReturnValue;
import javax.annotation.meta.When;
import javax.imageio.ImageIO;

import com.cedarsoftware.util.io.JsonWriter;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.qatools.allure.annotations.Attachment;
import ru.yandex.qatools.allure.annotations.Step;

@Slf4j
public class AttachHelper {

  @Step("{0}")
  public static void attachStep(String stepTitle, Runnable someCodeToExec) {
    someCodeToExec.run();
  }

  @Attachment(value = "{0}", type = "text/plain")
  public static byte[] attachText(String fileName, String body) {
    return body.getBytes(Charset.forName("UTF-8"));
  }

  @Attachment(value = "{0}", type = "text/html")
  public static byte[] attachHtml(String fileName, String body) {
    return body.getBytes(Charset.forName("UTF-8"));
  }

  @Attachment(value = "{0}", type = "image/png")
  public static byte[] attachPng(String fileName, byte[] body) {
    return body;
  }

  public static void attachImg(String fileName, BufferedImage img) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] imageInByte;
    try {
      ImageIO.write(img, "png", baos);
      baos.flush();
      imageInByte = baos.toByteArray();
      baos.close();
    } catch (IOException ex) {
      log.error(ex.getMessage(), ex);
      return;
    }

    attachPng(fileName, imageInByte);
  }

  public static void attachJson(String fileName, String body) {
    String bodyFormatted;
    try {
      bodyFormatted = JsonWriter.formatJson(body);
    } catch (Throwable ex) {
      bodyFormatted = body;
    }
    attachJsonImpl(fileName, bodyFormatted);
  }

  @Attachment(value = "{0}", type = "application/json")
  private static byte[] attachJsonImpl(String fileName, String body) {
    return body.getBytes(Charset.forName("UTF-8"));
  }
}
