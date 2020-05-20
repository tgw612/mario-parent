package com.mario.common.constants;

import com.mario.common.constant.CommonApiConstants;
import com.mario.common.exception.DataBaseOperateException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CommonConstants extends CommonApiConstants {

  String UTF8 = "UTF-8";
  Charset UTF8_CHARSET = Charset.forName("UTF-8");
  byte[] EMPTY_BYTES = new byte[0];
  List EMPTY_LIST = Collections.EMPTY_LIST;
  Map EMPTY_MAP = Collections.EMPTY_MAP;
  Set EMPTY_SET = Collections.EMPTY_SET;
  Object EMPTY_OBJECT = new Object();
  String EMPTY_STRING = "";
  int DEFAULT_POLL_TIMEOUT = 100;
  DataBaseOperateException DATA_BASE_OPERATE_EXCEPTION = new DataBaseOperateException();
  String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  char DEFAULT_DELIMITER_CHAR = ',';
  String DEFAULT_DELIMITER_STRING = ",";
  char DEFAULT_ASTERISK_CHAR = '*';
  String[] EMPTY_STRING_ARRAY = new String[0];
}
