package com.mario.es.exception;

import com.mario.common.enums.CommonErrCodeEnum;
import com.mario.common.exception.SystemException;

public class CustomSearchException extends SystemException {

  public CustomSearchException() {
    super(CommonErrCodeEnum.ERR_ALI_SEARCH_ERROR);
  }

  public CustomSearchException(String msg) {
    super(CommonErrCodeEnum.ERR_ALI_SEARCH_ERROR, msg);
  }

  public CustomSearchException(String msg, Throwable cause) {
    super(CommonErrCodeEnum.ERR_ALI_SEARCH_ERROR, msg, cause);
  }

  public CustomSearchException(Throwable cause) {
    super(CommonErrCodeEnum.ERR_ALI_SEARCH_ERROR, cause);
  }
}
