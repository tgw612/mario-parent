package com.mario.common.util;

import java.util.UUID;

public class DBPrimaryKeyCreate {

  /**
   * 获取UUID
   *
   * @return
   */
  public static String getUUID() {
    return UUID.randomUUID().toString();
  }

  public static void main(String[] args) {
    System.out.println(getUUID());
  }
}
