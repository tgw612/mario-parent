package com.mario.rds;

import java.util.function.Supplier;

public class RDS {

  private static final byte FLAG_NORMAL = 0;
  private static final byte FLAG_MASTER = 1;
  private static final byte FLAG_SLAVE = 2;
  private static final ThreadLocal<Byte> HINT_FLAG_LOCAL = ThreadLocal.withInitial(() -> {
    return null;
  });

  public RDS() {
  }

  public static <T> T selectFromMaster(Supplier<T> selectWrapper) {
    return select(selectWrapper, (byte) 1);
  }

  public static <T> T selectFromSlave(Supplier<T> selectWrapper) {
    return select(selectWrapper, (byte) 2);
  }

  private static <T> T select(Supplier<T> selectWrapper, byte flag) {
    Object retVal = null;

    try {
      HINT_FLAG_LOCAL.set(flag);
      retVal = selectWrapper.get();
    } finally {
      HINT_FLAG_LOCAL.remove();
    }

    return (T) retVal;
  }

  public static String getCurrentHintSql() {
    byte hintFlag = getHintFlag();
    if (isHintMaster(hintFlag)) {
      return "/*FORCE_MASTER*/";
    } else {
      return isHintSlave(hintFlag) ? "/*FORCE_SLAVE*/" : "";
    }
  }

  public static boolean isHintMaster() {
    return isHintMaster(getHintFlag());
  }

  public static boolean isHintMaster(byte flag) {
    return flag == 1;
  }

  public static boolean isHintSlave() {
    return isHintMaster(getHintFlag());
  }

  public static boolean isHintSlave(byte flag) {
    return flag == 2;
  }

  public static byte getHintFlag() {
    return (Byte) HINT_FLAG_LOCAL.get();
  }

  public static void main(String[] args) {
    Object orderMapper = null;
    Object returnVal = selectFromMaster(() -> {
      return null;
    });
    returnVal = selectFromSlave(() -> {
      return null;
    });
  }
}
