package com.mario.common.exception;

import com.mario.common.enums.CommonErrCodeEnum;

public class DataBaseOperateException extends SystemException {

  public DataBaseOperateException() {
    super(CommonErrCodeEnum.DATASOURCE_OPERTE_ERROR);
  }

  public DataBaseOperateException(String msg) {
    super(CommonErrCodeEnum.DATASOURCE_OPERTE_ERROR, msg);
  }

  public DataBaseOperateException(String msg, Throwable cause) {
    super(CommonErrCodeEnum.DATASOURCE_OPERTE_ERROR, msg, cause);
  }

  public DataBaseOperateException(Throwable cause) {
    super(CommonErrCodeEnum.DATASOURCE_OPERTE_ERROR, cause);
  }
}
