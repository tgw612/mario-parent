package com.mario.common.util;

import javax.servlet.http.HttpServletRequest;

public class IpUtil {
  /**
   * 获取用户真实IP地址，不使用request.getRemoteAddr();的原因是有可能用户使用了代理软件方式避免真实IP地址,
   * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，究竟哪个才是真正的用户端的真实IP呢？
   * 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串。
   * @param request
   * @return
   */
  public static String getIpAddress(HttpServletRequest request) {
    String ip = request.getHeader("x-forwarded-for");
    if (isUnknownIP(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (isUnknownIP(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (isUnknownIP(ip)) {
      ip = request.getHeader("HTTP_CLIENT_IP");
    }
    if (isUnknownIP(ip)) {
      ip = request.getHeader("HTTP_X_FORWARDED_FOR");
    }
    if (isUnknownIP(ip)) {
      ip = request.getRemoteAddr();
      // 如果是本机IP，不需要根据网卡获取ip，速度较慢
    }
    return ip;
  }

  private static boolean isUnknownIP(String ip) {
    return ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip);
  }
}
