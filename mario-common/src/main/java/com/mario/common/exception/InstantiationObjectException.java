package com.mario.common.exception;

import com.mario.common.enums.CommonErrCodeEnum;

public class InstantiationObjectException extends SystemException {

  private static final long serialVersionUID = -8320463974695997038L;

  public InstantiationObjectException() {
    super(CommonErrCodeEnum.ERR_INSTANTIATION_ERROR);
  }

  public InstantiationObjectException(String msg) {
    super(CommonErrCodeEnum.ERR_INSTANTIATION_ERROR, msg);
  }

  public InstantiationObjectException(String msg, Throwable cause) {
    super(CommonErrCodeEnum.ERR_INSTANTIATION_ERROR, msg, cause);
  }

  public InstantiationObjectException(Throwable cause) {
    super(CommonErrCodeEnum.ERR_INSTANTIATION_ERROR, cause);
  }
}
