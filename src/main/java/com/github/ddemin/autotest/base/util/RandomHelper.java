package com.github.ddemin.autotest.base.util;

import java.math.*;
import java.text.*;

import org.apache.commons.lang3.*;

public class RandomHelper {

  private static final String ALPHABET_RU = "абвгдеёж зийклмно прстуфх цшщъьэюя АБВГДЕЖ ЗКЛМНОП СТУФХЦЧЩ ШЪЬЭЮЯ";
  private static final String TEST_MARK_RU = "ат";
  private static final String TEST_MARK_EN = "at";
  private static final String EMAIL_DOMAIN = "@somedomain.com";
  private static final String TEST_PHONE_MARK = "7000";

  public static String randomRu(int lettersCount) {
    return TEST_MARK_RU + RandomStringUtils.random(lettersCount - TEST_MARK_EN.length(), ALPHABET_RU).trim();
  }

  public static String randomEn(int lettersCount) {
    return TEST_MARK_EN + RandomStringUtils.random(lettersCount - TEST_MARK_EN.length(), true, false);
  }

  public static String randomMail() {
    return TEST_MARK_EN
        + RandomStringUtils.random(8, true, false)
        + System.currentTimeMillis()
        + EMAIL_DOMAIN;
  }

  public static BigDecimal randomBigDecimal(float startInclusive, float endInclusive, String format) {
    return new BigDecimal(
        new DecimalFormat(format)
            .format(org.apache.commons.lang3.RandomUtils.nextFloat(startInclusive, endInclusive))
            .replace(',', '.'));
  }

  public static BigDecimal randomBigDecimal() {
    return randomBigDecimal(0.001f, 100f, "#.###");
  }

  public static BigDecimal randomBigDecimal(String format) {
    return randomBigDecimal(0.001f, 100f, format);
  }

  public static String randomPhone() {
    return TEST_PHONE_MARK + RandomStringUtils.randomNumeric(11 - TEST_PHONE_MARK.length());
  }

}
