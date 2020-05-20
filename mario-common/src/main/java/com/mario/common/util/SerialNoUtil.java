package com.mario.common.util;

import com.mario.common.enums.AppNameBase;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerialNoUtil {

  private static final Logger log = LoggerFactory.getLogger(SerialNoUtil.class);
  private static Object lock = new Object();
  private static int index = 0;

  public SerialNoUtil() {
  }

  public static String generateSerialNo(AppNameBase appName) {
    StringBuilder stringBuilder = new StringBuilder(38);
    stringBuilder.append(appName.getCodeNumber());
    stringBuilder.append(LocalHostUtil.getIpCode());
    stringBuilder.append(DateUtil.formatDate(new Date(), "yyMMddHHmmssSSS"));
    String indexV = "000";
    synchronized (lock) {
      ++index;
      if (index > 999) {
        index = 0;
      }

      indexV = "" + index;
    }

    if (indexV.length() < 2) {
      indexV = "00" + indexV;
    } else if (indexV.length() < 3) {
      indexV = "0" + indexV;
    }

    stringBuilder.append(indexV);
    return stringBuilder.toString();
  }
}
