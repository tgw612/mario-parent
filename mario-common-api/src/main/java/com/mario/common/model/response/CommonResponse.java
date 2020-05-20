package com.mario.common.model.response;

import com.mario.common.error.CommonError;
import java.io.Serializable;
import java.util.Collection;

public class CommonResponse<T> implements Serializable {

  private static final long serialVersionUID = -655403293051655566L;
  private T result;
  private int errorCode;
  private String errorMsg;
  private boolean success = false;

  public CommonResponse() {
  }

  public CommonResponse(T result) {
    this.setResult(result);
  }

  public CommonResponse(int errorCode, String errorMsg) {
    this.setErrorCode(errorCode);
    this.setErrorMsg(errorMsg);
  }

  public CommonResponse(T result, int errorCode, String errorMsg) {
    this.setErrorCode(errorCode);
    this.setErrorMsg(errorMsg);
    this.setResult(result);
  }

  public CommonResponse(T result, CommonError error) {
    this.setError(error);
    this.setResult(result);
  }

  public void setError(int errorCode, String errorMsg) {
    this.setErrorCode(errorCode);
    this.setErrorMsg(errorMsg);
  }

  public void setError(CommonError error) {
    this.setError(error.getErrorCode(), error.getErrorDesc());
  }

  public void setResult(T result) {
    if (result != null) {
      this.success = true;
      this.result = result;
    } else {
      this.success = false;
    }

  }

  public T getResult() {
    return this.result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if (o != null && this.getClass() == o.getClass()) {
      CommonResponse response = (CommonResponse) o;
      if (this.errorCode != response.errorCode) {
        return false;
      } else if (!this.errorMsg.equals(response.errorMsg)) {
        return false;
      } else {
        return this.result.equals(response.result);
      }
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    int result1 = 1;
    result1 = 31 * result1 + this.result.hashCode();
    result1 = 31 * result1 + this.errorCode;
    result1 = 31 * result1 + this.errorMsg.hashCode();
    return result1;
  }

  @Override
  public String toString() {
    String resultTemp = null;
    if (this.getResult() != null) {
      if (this.getResult() instanceof Collection && ((Collection) this.getResult()).size() > 20) {
        resultTemp = this.getResult().getClass().getSimpleName() + "[...]";
      } else {
        resultTemp = this.getResult().toString();
      }
    }

    return this.toSimpleString(resultTemp);
  }

  private String toSimpleString(String resultTemp) {
    return "CommonResponse{result=" + resultTemp + ", errorCode='" + this.errorCode + '\''
        + ", errorMsg='" + this.errorMsg + '\'' + ", success=" + this.success + '}';
  }

  public int getErrorCode() {
    return this.errorCode;
  }

  public void setErrorCode(int errorCode) {
    this.errorCode = errorCode;
  }

  public String getErrorMsg() {
    return this.errorMsg;
  }

  public void setErrorMsg(String errorMsg) {
    this.errorMsg = errorMsg;
  }

  public boolean isSuccess() {
    return this.success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }
}