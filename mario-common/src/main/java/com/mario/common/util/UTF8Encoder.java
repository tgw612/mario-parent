package com.mario.common.util;

import com.mario.common.constants.CommonConstants;
import java.io.UnsupportedEncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UTF8Encoder {

  private static final Logger log = LoggerFactory.getLogger(UTF8Encoder.class);

  public UTF8Encoder() {
  }

  public static byte[][] encodeMany(String... strs) {
    byte[][] many = new byte[strs.length][];

    for (int i = 0; i < strs.length; ++i) {
      many[i] = encode(strs[i]);
    }

    return many;
  }

  public static byte[] encode(String str) {
    if (str != null) {
      try {
        return str.getBytes("UTF-8");
      } catch (UnsupportedEncodingException var2) {
        log.error("encode the string [{}] Some Exception Occur:[{}]", str,
            ExceptionUtil.getAsString(var2));
      }
    }

    return CommonConstants.EMPTY_BYTES;
  }

  public static String decode(byte[] data) {
    if (data != null && data.length != 0) {
      try {
        return new String(data, "UTF-8");
      } catch (UnsupportedEncodingException var2) {
        log.error("decode the bytes, Some Exception Occur:[{}]", ExceptionUtil.getAsString(var2));
      }
    }

    return "";
  }
}
