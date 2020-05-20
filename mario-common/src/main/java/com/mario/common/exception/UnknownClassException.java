package com.mario.common.exception;

import com.mario.common.enums.CommonErrCodeEnum;

public class UnknownClassException extends SystemException {

  private static final long serialVersionUID = 7434775285630684831L;

  public UnknownClassException() {
    super(CommonErrCodeEnum.ERR_UNKNOWN_CLASS_ERROR);
  }

  public UnknownClassException(String msg) {
    super(CommonErrCodeEnum.ERR_UNKNOWN_CLASS_ERROR, msg);
  }

  public UnknownClassException(String msg, Throwable cause) {
    super(CommonErrCodeEnum.ERR_UNKNOWN_CLASS_ERROR, msg, cause);
  }

  public UnknownClassException(Throwable cause) {
    super(CommonErrCodeEnum.ERR_UNKNOWN_CLASS_ERROR, cause);
  }
}