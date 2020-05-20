package com.mario.common.util;

import com.mario.common.threadlocal.SerialNo;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LocalHostUtil {

  private static final Logger log = LoggerFactory.getLogger(LocalHostUtil.class);
  private static String localHostAddress = "";
  private static String ipCode = "";

  public LocalHostUtil() {
  }

  public static String getIpCode() {
    if (ipCode != null && !ipCode.equals("")) {
      return ipCode;
    } else {
      Class var0 = LocalHostUtil.class;
      synchronized (LocalHostUtil.class) {
        if (ipCode != null && !ipCode.equals("")) {
          return ipCode;
        } else {
          String ip = getLocalHostAddress();
          String[] ipArray = ip.split("\\.");

          for (int j = 0; j < 4; ++j) {
            for (int i = ipArray[j].length(); i < 3; ++i) {
              ipCode = ipCode + "0";
            }

            ipCode = ipCode + ipArray[j];
          }

          return ipCode;
        }
      }
    }
  }

  public static String getLocalHostAddress() {
    if (!localHostAddress.equals("")) {
      return localHostAddress;
    } else {
      String ip = "";
      String ipBak = "";
      InetAddress inetAddress = null;

      try {
        inetAddress = InetAddress.getLocalHost();
        ip = inetAddress.getHostAddress();
      } catch (Throwable var10) {
      }

      if (StringUtil.isNotBlank(ip) && !"127.0.0.1".equals(ip) && ip.indexOf(58) < 0) {
        localHostAddress = ip;
        return ip;
      } else {
        Enumeration netInterfaces = null;

        try {
          netInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (Throwable var9) {
          log.error("[{}] [{}] Finish handling .\nSome Exception Occur:[{}]",
              new Object[]{SerialNo.getSerialNo(), SerialNoUtil.class.getName(),
                  ExceptionUtil.getAsString(var9)});
        }

        InetAddress iAddress = null;

        try {
          label147:
          while (netInterfaces.hasMoreElements()) {
            NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();

            for (Enumeration inetAddresses = ni.getInetAddresses(); inetAddresses.hasMoreElements();
                iAddress = null) {
              iAddress = (InetAddress) inetAddresses.nextElement();
              if (!iAddress.isSiteLocalAddress() && !iAddress.isLoopbackAddress()
                  && iAddress.getHostAddress().indexOf(58) == -1) {
                ip = iAddress.getHostAddress();
                break label147;
              }

              ip = iAddress.getHostAddress();
              if (!ip.equals("127.0.0.1") && ip.split("\\.").length == 4 && ip.indexOf(58) < 0) {
                ipBak = ip;
              }

              ip = "";
            }
          }
        } catch (Throwable var12) {
          log.error("[{}] [{}] Finish handling .\nSome Exception Occur:[{}]",
              new Object[]{SerialNo.getSerialNo(), SerialNoUtil.class.getName(),
                  ExceptionUtil.getAsString(var12)});
        }

        if (!ip.equals("127.0.0.1") && ip.split("\\.").length == 4 && ip.indexOf(58) < 0) {
          localHostAddress = ip;
          return ip;
        } else {
          try {
            label154:
            {
              Enumeration e1 = NetworkInterface.getNetworkInterfaces();

              NetworkInterface ni;
              do {
                if (!e1.hasMoreElements()) {
                  break label154;
                }

                ni = (NetworkInterface) e1.nextElement();
              } while (!ni.getName().equals("eth0") && !ni.getName().equals("eth1") && !ni.getName()
                  .equals("bond0"));

              Enumeration e2 = ni.getInetAddresses();

              while (e2.hasMoreElements()) {
                InetAddress ia = (InetAddress) e2.nextElement();
                if (!(ia instanceof Inet6Address)) {
                  ip = ia.getHostAddress();
                  if (!ia.isSiteLocalAddress() && !ip.equals("127.0.0.1")
                      && ip.split("\\.").length == 4 && ip.indexOf(58) < 0) {
                    localHostAddress = ip;
                    return ip;
                  }

                  if (ni.getName().equals("eth1") && !ia.isSiteLocalAddress() && !ip
                      .equals("127.0.0.1") && ip.split("\\.").length == 4 && ip.indexOf(58) < 0) {
                    ipBak = ip;
                    ip = "";
                  }
                }
              }
            }
          } catch (Throwable var11) {
          }

          if (!ip.equals("127.0.0.1") && ip.split("\\.").length == 4 && ip.indexOf(58) < 0) {
            localHostAddress = ip;
            return ip;
          } else if (!ipBak.equals("127.0.0.1") && ipBak.split("\\.").length == 4
              && ipBak.indexOf(58) < 0) {
            localHostAddress = ipBak;
            return ipBak;
          } else {
            localHostAddress = ip;
            return ip;
          }
        }
      }
    }
  }
}
