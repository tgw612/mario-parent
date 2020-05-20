package com.mario.common.exception.code;

import com.mario.common.error.CommonError;
import lombok.Getter;

public enum BussinessErrCodeEnum implements CommonError {
  SIGNATURE_ERR(3001, "获取签名失败"),
  POST_TENCENT_CLOUD(3002, "回调腾讯云失败"),
  ;

  @Getter
  private int errorCode;
  @Getter
  private String errorDesc;

  BussinessErrCodeEnum(int errorCode, String errorDesc) {
    this.errorCode = errorCode;
    this.errorDesc = errorDesc;
  }

  @Override
  public int getErrorCode() {
    return 0;
  }

  @Override
  public String getErrorDesc() {
    return null;
  }
}
