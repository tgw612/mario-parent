package com.mario.common.exception;

import com.mario.common.enums.CommonErrCodeEnum;

public class RemoteAccessTimeOutException extends SystemException {

  private static final long serialVersionUID = 3673505993449685753L;

  public RemoteAccessTimeOutException() {
    super(CommonErrCodeEnum.ERR_REMOTE_ACCESS_TIMEOUT_ERROR);
  }

  public RemoteAccessTimeOutException(String msg) {
    super(CommonErrCodeEnum.ERR_REMOTE_ACCESS_TIMEOUT_ERROR, msg);
  }

  public RemoteAccessTimeOutException(String msg, Throwable cause) {
    super(CommonErrCodeEnum.ERR_REMOTE_ACCESS_TIMEOUT_ERROR, msg, cause);
  }

  public RemoteAccessTimeOutException(Throwable cause) {
    super(CommonErrCodeEnum.ERR_REMOTE_ACCESS_TIMEOUT_ERROR, cause);
  }
}
