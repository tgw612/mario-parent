package com.mario.common.assertions;

import javax.annotation.Nullable;

public class MoreValidate {

  public MoreValidate() {
  }

  public static int positive(@Nullable String role, int x) {
    if (x <= 0) {
      throw new IllegalArgumentException(role + " (" + x + ") must be > 0");
    } else {
      return x;
    }
  }

  public static Integer positive(@Nullable String role, Integer x) {
    if (x <= 0) {
      throw new IllegalArgumentException(role + " (" + x + ") must be > 0");
    } else {
      return x;
    }
  }

  public static long positive(@Nullable String role, long x) {
    if (x <= 0L) {
      throw new IllegalArgumentException(role + " (" + x + ") must be > 0");
    } else {
      return x;
    }
  }

  public static Long positive(@Nullable String role, Long x) {
    if (x <= 0L) {
      throw new IllegalArgumentException(role + " (" + x + ") must be > 0");
    } else {
      return x;
    }
  }

  public static double positive(@Nullable String role, double x) {
    if (x <= 0.0D) {
      throw new IllegalArgumentException(role + " (" + x + ") must be >= 0");
    } else {
      return x;
    }
  }

  public static int nonNegative(@Nullable String role, int x) {
    if (x < 0) {
      throw new IllegalArgumentException(role + " (" + x + ") must be >= 0");
    } else {
      return x;
    }
  }

  public static Integer nonNegative(@Nullable String role, Integer x) {
    if (x < 0) {
      throw new IllegalArgumentException(role + " (" + x + ") must be >= 0");
    } else {
      return x;
    }
  }

  public static long nonNegative(@Nullable String role, long x) {
    if (x < 0L) {
      throw new IllegalArgumentException(role + " (" + x + ") must be >= 0");
    } else {
      return x;
    }
  }

  public static Long nonNegative(@Nullable String role, Long x) {
    if (x < 0L) {
      throw new IllegalArgumentException(role + " (" + x + ") must be >= 0");
    } else {
      return x;
    }
  }

  public static double nonNegative(@Nullable String role, double x) {
    if (x < 0.0D) {
      throw new IllegalArgumentException(role + " (" + x + ") must be >= 0");
    } else {
      return x;
    }
  }
}
