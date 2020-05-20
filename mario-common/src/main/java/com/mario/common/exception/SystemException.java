package com.mario.common.exception;

import com.mario.common.enums.CommonErrCodeEnum;
import com.mario.common.error.CommonError;

public class SystemException extends CommonRuntimeException {

  private static final long serialVersionUID = -8998707909342242357L;
  private int errCode;
  private String errReason;

  public SystemException() {
    super("[" + CommonErrCodeEnum.ERR_UNKNOW_ERROR.getErrorCode() + "]"
        + CommonErrCodeEnum.ERR_UNKNOW_ERROR.getErrorDesc());
    this.errCode = CommonErrCodeEnum.ERR_UNKNOW_ERROR.getErrorCode();
    this.errReason = "";
    this.errCode = CommonErrCodeEnum.ERR_UNKNOW_ERROR.getErrorCode();
    this.errReason = CommonErrCodeEnum.ERR_UNKNOW_ERROR.getErrorDesc();
  }

  public SystemException(String msg) {
    super(msg);
    this.errCode = CommonErrCodeEnum.ERR_UNKNOW_ERROR.getErrorCode();
    this.errReason = "";
    this.errCode = CommonErrCodeEnum.ERR_UNKNOW_ERROR.getErrorCode();
    this.errReason = CommonErrCodeEnum.ERR_UNKNOW_ERROR.getErrorDesc();
  }

  public SystemException(String msg, Throwable cause) {
    super(msg, cause);
    this.errCode = CommonErrCodeEnum.ERR_UNKNOW_ERROR.getErrorCode();
    this.errReason = "";
    this.errCode = CommonErrCodeEnum.ERR_UNKNOW_ERROR.getErrorCode();
    this.errReason = CommonErrCodeEnum.ERR_UNKNOW_ERROR.getErrorDesc();
  }

  public SystemException(Throwable cause) {
    super(cause);
    this.errCode = CommonErrCodeEnum.ERR_UNKNOW_ERROR.getErrorCode();
    this.errReason = "";
    this.errCode = CommonErrCodeEnum.ERR_UNKNOW_ERROR.getErrorCode();
    this.errReason = CommonErrCodeEnum.ERR_UNKNOW_ERROR.getErrorDesc();
  }

  public SystemException(int errCode, String errReason) {
    super("[" + errCode + "]" + errReason);
    this.errCode = CommonErrCodeEnum.ERR_UNKNOW_ERROR.getErrorCode();
    this.errReason = "";
    this.errCode = errCode;
    this.errReason = errReason;
  }

  public SystemException(int errCode, String errReason, String msg) {
    super(msg);
    this.errCode = CommonErrCodeEnum.ERR_UNKNOW_ERROR.getErrorCode();
    this.errReason = "";
    this.errCode = errCode;
    this.errReason = errReason;
  }

  public SystemException(int errCode, String errReason, String msg, Throwable cause) {
    super(msg, cause);
    this.errCode = CommonErrCodeEnum.ERR_UNKNOW_ERROR.getErrorCode();
    this.errReason = "";
    this.errCode = errCode;
    this.errReason = errReason;
  }

  public SystemException(int errCode, String errReason, Throwable cause) {
    super(cause);
    this.errCode = CommonErrCodeEnum.ERR_UNKNOW_ERROR.getErrorCode();
    this.errReason = "";
    this.errCode = errCode;
    this.errReason = errReason;
  }

  public SystemException(CommonError commonError) {
    this(commonError.getErrorCode(), commonError.getErrorDesc());
  }

  public SystemException(CommonError commonError, String msg) {
    this(commonError.getErrorCode(), commonError.getErrorDesc(), msg);
  }

  public SystemException(CommonError commonError, String msg, Throwable cause) {
    this(commonError.getErrorCode(), commonError.getErrorDesc(), msg, cause);
  }

  public SystemException(CommonError commonError, Throwable cause) {
    this(commonError.getErrorCode(), commonError.getErrorDesc(), cause);
  }

  @Override
  public String getMessage() {
    return String.format("[%s]%s.", this.getErrCode(), this.getErrReason());
  }

  public int getErrCode() {
    return this.errCode;
  }

  public String getErrReason() {
    return this.errReason;
  }
}
