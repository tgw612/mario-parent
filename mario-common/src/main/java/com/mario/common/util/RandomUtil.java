package com.mario.common.util;

import com.mario.common.assertions.MoreValidate;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.Validate;

public class RandomUtil {

  private static final long DX = -227377152L;
  private static long lastUuid = System.currentTimeMillis() - -227377152L;

  public RandomUtil() {
  }

  /**
   * @deprecated
   */
  @Deprecated
  public static synchronized String genFixLengthRandom(int length) {
    String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    StringBuilder sb = new StringBuilder(length);

    for (int i = 0; i < length; ++i) {
      int number = ThreadLocalRandom.current().nextInt(62);
      sb.append(str.charAt(number));
    }

    return sb.toString();
  }

  /**
   * @deprecated
   */
  @Deprecated
  public static synchronized long random() {
    long uuid;
    for (uuid = System.currentTimeMillis() - -227377152L; uuid == lastUuid;
        uuid = System.currentTimeMillis() - -227377152L) {
    }

    lastUuid = uuid;
    return uuid;
  }

  public static Random threadLocalRandom() {
    return ThreadLocalRandom.current();
  }

  public static SecureRandom secureRandom() {
    try {
      return SecureRandom.getInstance("SHA1PRNG");
    } catch (NoSuchAlgorithmException var1) {
      return new SecureRandom();
    }
  }

  public static int nextInt() {
    return nextInt(ThreadLocalRandom.current());
  }

  public static int nextInt(Random random) {
    int n = random.nextInt();
    if (n == -2147483648) {
      n = 0;
    } else {
      n = Math.abs(n);
    }

    return n;
  }

  public static int nextInt(int max) {
    return nextInt(ThreadLocalRandom.current(), max);
  }

  public static int nextInt(Random random, int max) {
    return random.nextInt(max);
  }

  public static int nextInt(int min, int max) {
    return nextInt(ThreadLocalRandom.current(), min, max);
  }

  public static int nextInt(Random random, int min, int max) {
    Validate
        .isTrue(max >= min, "Start value must be smaller or equal to end value.", new Object[0]);
    MoreValidate.nonNegative("min", min);
    return min == max ? min : min + random.nextInt(max - min);
  }

  public static long nextLong() {
    return nextLong(ThreadLocalRandom.current());
  }

  public static long nextLong(Random random) {
    long n = random.nextLong();
    if (n == -9223372036854775808L) {
      n = 0L;
    } else {
      n = Math.abs(n);
    }

    return n;
  }

  public static long nextLong(long max) {
    return nextLong(ThreadLocalRandom.current(), 0L, max);
  }

  public static long nextLong(Random random, long max) {
    return nextLong(random, 0L, max);
  }

  public static long nextLong(long min, long max) {
    return nextLong(ThreadLocalRandom.current(), min, max);
  }

  public static long nextLong(Random random, long min, long max) {
    Validate
        .isTrue(max >= min, "Start value must be smaller or equal to end value.", new Object[0]);
    MoreValidate.nonNegative("min", min);
    return min == max ? min : (long) ((double) min + (double) (max - min) * random.nextDouble());
  }

  public static double nextDouble() {
    return nextDouble(ThreadLocalRandom.current(), 0.0D, 1.7976931348623157E308D);
  }

  public static double nextDouble(Random random) {
    return nextDouble(random, 0.0D, 1.7976931348623157E308D);
  }

  public static double nextDouble(double max) {
    return nextDouble(ThreadLocalRandom.current(), 0.0D, max);
  }

  public static double nextDouble(Random random, double max) {
    return nextDouble(random, 0.0D, max);
  }

  public static double nextDouble(double min, double max) {
    return nextDouble(ThreadLocalRandom.current(), min, max);
  }

  public static double nextDouble(Random random, double min, double max) {
    Validate
        .isTrue(max >= min, "Start value must be smaller or equal to end value.", new Object[0]);
    MoreValidate.nonNegative("min", min);
    return min == max ? min : min + (max - min) * random.nextDouble();
  }

  public static String randomNumStringFixLength(int length) {
    return RandomStringUtils.random(length, 0, 0, false, true, (char[]) null, threadLocalRandom());
  }

  public static String randomStringFixLength(int length) {
    return RandomStringUtils.random(length, 0, 0, true, true, (char[]) null, threadLocalRandom());
  }

  public static String randomStringFixLength(Random random, int length) {
    return RandomStringUtils.random(length, 0, 0, true, true, (char[]) null, random);
  }

  public static String randomStringRandomLength(int minLength, int maxLength) {
    return RandomStringUtils.random(nextInt(minLength, maxLength), 0, 0, true, true, (char[]) null,
        threadLocalRandom());
  }

  public static String randomStringRandomLength(Random random, int minLength, int maxLength) {
    return RandomStringUtils
        .random(nextInt(random, minLength, maxLength), 0, 0, true, true, (char[]) null, random);
  }

  public static String randomLetterFixLength(int length) {
    return RandomStringUtils.random(length, 0, 0, true, false, (char[]) null, threadLocalRandom());
  }

  public static String randomLetterFixLength(Random random, int length) {
    return RandomStringUtils.random(length, 0, 0, true, false, (char[]) null, random);
  }

  public static String randomLetterRandomLength(int minLength, int maxLength) {
    return RandomStringUtils.random(nextInt(minLength, maxLength), 0, 0, true, false, (char[]) null,
        threadLocalRandom());
  }

  public static String randomLetterRandomLength(Random random, int minLength, int maxLength) {
    return RandomStringUtils
        .random(nextInt(random, minLength, maxLength), 0, 0, true, false, (char[]) null, random);
  }

  public static String randomAsciiFixLength(int length) {
    return RandomStringUtils
        .random(length, 32, 127, false, false, (char[]) null, threadLocalRandom());
  }

  public static String randomAsciiFixLength(Random random, int length) {
    return RandomStringUtils.random(length, 32, 127, false, false, (char[]) null, random);
  }

  public static String randomAsciiRandomLength(int minLength, int maxLength) {
    return RandomStringUtils
        .random(nextInt(minLength, maxLength), 32, 127, false, false, (char[]) null,
            threadLocalRandom());
  }

  public static String randomAsciiRandomLength(Random random, int minLength, int maxLength) {
    return RandomStringUtils
        .random(nextInt(random, minLength, maxLength), 32, 127, false, false, (char[]) null,
            random);
  }

  public static void main(String[] args) {
    int i;
    for (i = 0; i < 100; ++i) {
      System.out.println(randomNumStringFixLength(6));
    }

    for (i = 0; i < 100; ++i) {
      System.out.println(nextInt(8));
    }

    Map numbers = new HashMap();
    long value = 0L;
    int max = 10000;
    long start = System.currentTimeMillis();

    for (int j = 0; j < max; ++j) {
      randomNumStringFixLength(6);
    }

    long end = System.currentTimeMillis();
    long time = end - start;
    System.out.println(String.format("生成%d，耗时%d毫秒", Integer.valueOf(max), time));
    Iterator iter = numbers.entrySet().iterator();

    while (iter.hasNext()) {
      Map.Entry entry = (Map.Entry) iter.next();
      Object key = entry.getKey();
      Object val = entry.getValue();
      System.out.println(key + "=" + val);
    }

  }
}