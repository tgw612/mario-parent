package com.mario.common.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author 陈志杭
 * @qq 279397942@qq.com
 * @createdDate 2016年10月27日
 */
public class CodecUtils {

  public static String md5(String data) {
    return DigestUtils.md5Hex(data);
  }

  public static byte[] md5B(String data) {
    return DigestUtils.md5(data);
  }

  public static String encodeBase64(String data) {
    return Base64.encodeBase64String(StringUtils.getBytesUtf8(data));
  }

  public static String encodeBase64(byte[] data) {
    return Base64.encodeBase64String(data);
  }

  public static String decodeBase64(String data) {
    return new String(Base64.decodeBase64(data));
  }

  public static byte[] decodeBase64Byte(String data) {
    return Base64.decodeBase64(data);
  }
}
