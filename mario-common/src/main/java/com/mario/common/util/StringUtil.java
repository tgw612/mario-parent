package com.mario.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class StringUtil extends StringUtils {

  public static final String DEFAULT_CHAR = "*";
  public static final int CERT_NO_PREX_NUM = 4;
  public static final int CERT_NO_POST_NUM = 3;
  public static final int MOBILE_PREX_NUM = 3;
  public static final int MOBILE_POST_NUM = 4;
  public static final int BANKCARD_NO_PREX_NUM = 6;
  public static final int BANKCARD_NO_POST_NUM = 4;
  public static final Pattern REG_DECIMALS = Pattern.compile("\\-?[1-9]\\d+(\\.\\d+)?");

  public StringUtil() {
  }

  public static boolean isEmpty(Object obj) {
    return obj == null || "".equals(obj);
  }

  public static String replaceDefaultPwd(String source) {
    return replaceDefaultChar(source, 0, 0);
  }

  public static String replaceDefaultCertNo(String source) {
    return replaceChar(source, 4, 3, "*");
  }

  public static String replaceDefaultMobile(String source) {
    return replaceChar(source, 3, 4, "*");
  }

  public static String replaceDefaultBankCardNo(String source) {
    return replaceChar(source, 6, 4, "*");
  }

  public static String replaceDefaultChar(String source, int prexNum, int postNum) {
    return replaceChar(source, prexNum, postNum, "*");
  }

  public static boolean checkDecimals(String decimals) {
    return REG_DECIMALS.matcher(decimals).matches();
  }

  public static String replaceChar(String source, int prexNum, int postNum, String toString) {
    if (!StringUtils.isNotBlank(source)) {
      return "";
    } else {
      if (prexNum < 0) {
        prexNum = 0;
      }

      if (postNum < 0) {
        postNum = 0;
      }

      if (source.length() <= prexNum + postNum) {
        return source;
      } else {
        StringBuilder newStrBu = new StringBuilder(source.length());
        newStrBu.append(source.substring(0, prexNum));
        int end = source.length() - postNum;

        for (int i = prexNum; i < end; ++i) {
          newStrBu.append(toString);
        }

        newStrBu.append(source.substring(end));
        return newStrBu.toString();
      }
    }
  }

  public static boolean isNull(Object obj) {
    if (null == obj) {
      return true;
    } else if (obj instanceof String) {
      return "".equals(((String) obj).trim()) || "null".equals(((String) obj).trim().toLowerCase())
          || "NULL".equals(((String) obj).trim().toLowerCase());
    } else if (obj instanceof List) {
      return ((List) obj).isEmpty();
    } else if (obj instanceof HashSet) {
      return ((HashSet) obj).isEmpty();
    } else if (obj instanceof HashMap) {
      return ((HashMap) obj).isEmpty();
    } else if (obj instanceof Set) {
      return ((Set) obj).isEmpty();
    } else {
      return false;
    }
  }

  public static String getUUID() {
    String uuid = UUID.randomUUID().toString();
    return (new StringBuilder(32)).append(uuid.substring(0, 8)).append(uuid.substring(9, 13))
        .append(uuid.substring(14, 18)).append(uuid.substring(19, 23)).append(uuid.substring(24))
        .toString();
  }

  public static boolean isUUID(String uuid) {
    if (isNotBlank(uuid)) {
      int len = uuid.length();
      if (len == 32) {
        StringBuilder sb = new StringBuilder(64);
        sb.append(uuid.substring(0, 8));
        sb.append("-");
        sb.append(uuid.substring(8, 12));
        sb.append("-");
        sb.append(uuid.substring(12, 16));
        sb.append("-");
        sb.append(uuid.substring(16, 20));
        sb.append("-");
        sb.append(uuid.substring(20));
        return isUUIDFormatString(sb.toString());
      }

      if (len == 36) {
        return isUUIDFormatString(uuid);
      }
    }

    return false;
  }

  private static boolean isUUIDFormatString(String uuid) {
    try {
      UUID.fromString(uuid);
      return true;
    } catch (Exception var2) {
      return false;
    }
  }

  public static boolean isNotUUID(String uuid) {
    return !isUUID(uuid);
  }

  public static String rendomFour() {
    StringBuilder stringBuilder = new StringBuilder(4);

    for (int i = 0; i < 4; ++i) {
      stringBuilder.append(RandomUtil.nextInt(10));
    }

    return stringBuilder.toString();
  }

  public static String randomFive() {
    StringBuilder stringBuilder = new StringBuilder(5);

    for (int i = 0; i < 5; ++i) {
      stringBuilder.append(RandomUtil.nextInt(10));
    }

    return stringBuilder.toString();
  }

  public static String randomFifteen() {
    StringBuilder stringBuilder = new StringBuilder(15);

    for (int i = 0; i < 15; ++i) {
      stringBuilder.append(RandomUtil.nextInt(10));
    }

    return stringBuilder.toString();
  }

  public static String rendomSix() {
    StringBuilder stringBuilder = new StringBuilder(6);

    for (int i = 0; i < 6; ++i) {
      stringBuilder.append(RandomUtil.nextInt(10));
    }

    return stringBuilder.toString();
  }

  public static String append(String source, String prefix, String suffix) {
    if (isNotBlank(source)) {
      StringBuilder stringBuilder = new StringBuilder();
      if (!source.startsWith(prefix)) {
        stringBuilder.append(prefix);
      }

      stringBuilder.append(source);
      if (!source.endsWith(suffix)) {
        stringBuilder.append(suffix);
      }

      return stringBuilder.toString();
    } else {
      return source;
    }
  }

  public static String appendToStartAndEnd(String source, String str) {
    return append(source, str, str);
  }

  public static String trim(String source, String prefix, String suffix) {
    if (isNotBlank(source)) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(source);
      if (source.startsWith(prefix)) {
        stringBuilder.deleteCharAt(0);
      }

      if (source.endsWith(suffix)) {
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
      }

      return stringBuilder.toString();
    } else {
      return source;
    }
  }

  public static String trim(String source, String trimStr) {
    return trim(source, trimStr, trimStr);
  }

  public static String[] tokenizeToStringArray(String str, String delimiters) {
    return tokenizeToStringArray(str, delimiters, true, true);
  }

  public static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens,
      boolean ignoreEmptyTokens) {
    if (str == null) {
      return null;
    } else {
      StringTokenizer st = new StringTokenizer(str, delimiters);
      ArrayList tokens = new ArrayList();

      while (true) {
        String token;
        do {
          if (!st.hasMoreTokens()) {
            return toStringArray(tokens);
          }

          token = st.nextToken();
          if (trimTokens) {
            token = token.trim();
          }
        } while (ignoreEmptyTokens && token.length() <= 0);

        tokens.add(token);
      }
    }
  }

  public static String[] toStringArray(Collection collection) {
    return collection == null ? null
        : (String[]) ((String[]) collection.toArray(new String[collection.size()]));
  }

  public static String clean(String in) {
    String out = in;
    if (in != null) {
      out = in.trim();
      if (out.equals("")) {
        out = null;
      }
    }

    return out;
  }

  public static boolean hasLength(String str) {
    return str != null && str.length() > 0;
  }

  public static boolean hasText(String str) {
    if (!hasLength(str)) {
      return false;
    } else {
      int strLen = str.length();

      for (int i = 0; i < strLen; ++i) {
        if (!Character.isWhitespace(str.charAt(i))) {
          return true;
        }
      }

      return false;
    }
  }

  public static String[] split(String aLine, char delimiter, char beginQuoteChar, char endQuoteChar,
      boolean retainQuotes, boolean trimTokens) {
    String line = clean(aLine);
    if (line == null) {
      return null;
    } else {
      List<String> tokens = new ArrayList();
      StringBuilder sb = new StringBuilder();
      boolean inQuotes = false;

      for (int i = 0; i < line.length(); ++i) {
        char c = line.charAt(i);
        if (c == beginQuoteChar) {
          if (inQuotes && line.length() > i + 1 && line.charAt(i + 1) == beginQuoteChar) {
            sb.append(line.charAt(i + 1));
            ++i;
          } else {
            inQuotes = !inQuotes;
            if (retainQuotes) {
              sb.append(c);
            }
          }
        } else if (c == endQuoteChar) {
          inQuotes = !inQuotes;
          if (retainQuotes) {
            sb.append(c);
          }
        } else if (c == delimiter && !inQuotes) {
          String s = sb.toString();
          if (trimTokens) {
            s = s.trim();
          }

          tokens.add(s);
          sb = new StringBuilder();
        } else {
          sb.append(c);
        }
      }

      String s = sb.toString();
      if (trimTokens) {
        s = s.trim();
      }

      tokens.add(s);
      return (String[]) tokens.toArray(new String[tokens.size()]);
    }
  }

  public static String attributeNameToPropertyName(String attributeName) {
    if (!isEmpty(attributeName) && attributeName.contains("-")) {
      char[] chars = attributeName.toCharArray();
      char[] result = new char[chars.length - 1];
      int currPos = 0;
      boolean upperCaseNext = false;
      char[] var5 = chars;
      int var6 = chars.length;

      for (int var7 = 0; var7 < var6; ++var7) {
        char c = var5[var7];
        if (c == '-') {
          upperCaseNext = true;
        } else if (upperCaseNext) {
          result[currPos++] = Character.toUpperCase(c);
          upperCaseNext = false;
        } else {
          result[currPos++] = c;
        }
      }

      return new String(result, 0, currPos);
    } else {
      return attributeName;
    }
  }

  public static String propertyNameToAttributeName(String propertyName) {
    if (isEmpty(propertyName)) {
      return propertyName;
    } else {
      char[] chars = propertyName.trim().toCharArray();
      if (chars.length > 0) {
        char[] result = new char[chars.length + chars.length / 2];
        int currPos = 0;
        boolean preUpperCase = false;
        char[] var6 = chars;
        int var7 = chars.length;

        for (int var8 = 0; var8 < var7; ++var8) {
          char c = var6[var8];
          boolean currUpperCase = Character.isUpperCase(c);
          if (currUpperCase) {
            if (currPos != 0 && !preUpperCase && '-' != result[currPos - 1]) {
              result[currPos++] = '-';
            }

            result[currPos++] = Character.toLowerCase(c);
          } else {
            result[currPos++] = c;
          }

          preUpperCase = currUpperCase;
        }

        return new String(result, 0, currPos);
      } else {
        return propertyName;
      }
    }
  }

  public static void main(String[] args) {
    System.out.println(replaceDefaultMobile("13560280810"));
    System.out.println(replaceDefaultCertNo("44088119900110355X"));
    System.out.println(trim(",fsfds,", ",", ","));
    int count = 100000;
    long start = System.currentTimeMillis();
    int var4 = count;

    while (var4-- > 0) {
      getUUID();
    }

    long end = System.currentTimeMillis();
    System.out.println(end - start);
    start = System.currentTimeMillis();
    var4 = count;

    while (var4-- > 0) {
      getUUID();
    }

    end = System.currentTimeMillis();
    System.out.println(end - start);
  }
}
