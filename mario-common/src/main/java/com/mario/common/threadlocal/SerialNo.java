package com.mario.common.threadlocal;


import com.mario.common.enums.AppNameBase;
import com.mario.common.util.SerialNoUtil;

public class SerialNo {

  private static final InheritableThreadLocal<String> SERIALNO_THREAD_LOCAL = new InheritableThreadLocal();

  public SerialNo() {
  }

  public static String getSerialNo() {
    return (String) SERIALNO_THREAD_LOCAL.get();
  }

  public static void setSerialNo(String serialNo) {
    SERIALNO_THREAD_LOCAL.set(serialNo);
  }

  public static String init(AppNameBase appName) {
    String serialNo = SerialNoUtil.generateSerialNo(appName);
    SERIALNO_THREAD_LOCAL.set(serialNo);
    return serialNo;
  }

  public static void clear() {
    SERIALNO_THREAD_LOCAL.remove();
  }
}