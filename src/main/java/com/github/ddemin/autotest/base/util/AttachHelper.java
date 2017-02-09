package com.github.ddemin.autotest.base.util;

import java.awt.image.*;
import java.io.*;
import java.nio.charset.*;
import javax.imageio.*;

import com.cedarsoftware.util.io.*;
import lombok.extern.slf4j.*;
import ru.yandex.qatools.allure.annotations.*;

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
