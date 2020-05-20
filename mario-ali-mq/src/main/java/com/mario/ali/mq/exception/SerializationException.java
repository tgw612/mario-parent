package com.mario.ali.mq.exception;

import com.mario.common.enums.CommonErrCodeEnum;
import com.mario.common.exception.SystemException;

public class SerializationException extends SystemException {

  public SerializationException() {
    super(CommonErrCodeEnum.OBJECT_SERIALIZATION_ERROR);
  }

  public SerializationException(String msg) {
    super(CommonErrCodeEnum.OBJECT_SERIALIZATION_ERROR, msg);
  }

  public SerializationException(String msg, Throwable cause) {
    super(CommonErrCodeEnum.OBJECT_SERIALIZATION_ERROR, msg, cause);
  }

  public SerializationException(Throwable cause) {
    super(CommonErrCodeEnum.ERR_ALI_SEARCH_ERROR, cause);
  }
}
