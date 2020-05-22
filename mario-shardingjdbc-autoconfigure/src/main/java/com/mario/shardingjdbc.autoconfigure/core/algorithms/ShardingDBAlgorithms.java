package com.mario.shardingjdbc.autoconfigure.core.algorithms;

import com.mario.common.exception.SystemException;
import com.mario.shardingjdbc.autoconfigure.core.interfaces.StringHashCoding;
import org.apache.commons.lang3.StringUtils;

public class ShardingDBAlgorithms {
  private static StringHashCoding dataBaseHashCoding;

  public ShardingDBAlgorithms() {
  }

  public static StringHashCoding getDataBaseHashCoding() {
    return dataBaseHashCoding;
  }

  public static void setDataBaseHashCoding(StringHashCoding dataBaseHashCoding) {
    ShardingDBAlgorithms.dataBaseHashCoding = dataBaseHashCoding;
  }

  public static Integer getRealNode(Object identity) {
    String s = String.valueOf(identity);
    if (StringUtils.isBlank(s)) {
      throw new SystemException("通过分库负载均衡一致性算法获取真实节点数时，identity 不能为null 或空字符串!");
    } else {
      return dataBaseHashCoding.hashFor(s);
    }
  }

  public static String getLocatorStr(String identity) {
    String locatorStr = Integer.toString(getRealNode(identity), 32).toUpperCase();
    if (locatorStr.length() == 1) {
      locatorStr = "0" + locatorStr;
    }

    return locatorStr;
  }

  public static Integer transferRealNodeByLocatorStr(String locatorStr) {
    if (StringUtils.isBlank(locatorStr)) {
      throw new RuntimeException("通过 locatorStr 转为真实节点时不能为空!");
    } else {
      String localIndex = locatorStr.substring(locatorStr.length() - 2);
      String left = localIndex.substring(0, 1);
      String right = localIndex.substring(1);
      if ("0".equals(left)) {
        localIndex = right;
      }

      return Integer.parseInt(localIndex, 32);
    }
  }
}

