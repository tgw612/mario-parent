package com.mario.common.exception;

import com.mario.common.util.ExceptionUtil;
import java.io.PrintWriter;
import java.io.StringWriter;

public class CommonRuntimeException extends RuntimeException {

  private static final long serialVersionUID = 5439915454935047936L;

  public CommonRuntimeException() {
  }

  public CommonRuntimeException(String message) {
    super(message);
  }

  public CommonRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  public CommonRuntimeException(Throwable cause) {
    super(cause);
  }

  public CommonRuntimeException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public String transToString() {
    Throwable throwable = this.getCause();
    if (throwable != null) {
      StringWriter stringWriter = new StringWriter(50);
      throwable.printStackTrace(new PrintWriter(stringWriter));
      return stringWriter.toString();
    } else {
      return "";
    }
  }

  public String getMessage() {
    return ExceptionUtil.buildMessage(super.getMessage(), this.getCause());
  }

  public Throwable getRootCause() {
    Throwable rootCause = null;

    for (Throwable cause = this.getCause(); cause != null && cause != rootCause;
        cause = cause.getCause()) {
      rootCause = cause;
    }

    return rootCause;
  }

  public Throwable getMostSpecificCause() {
    Throwable rootCause = this.getRootCause();
    return (Throwable) (rootCause != null ? rootCause : this);
  }

  public boolean contains(Class exType) {
    if (exType == null) {
      return false;
    } else if (exType.isInstance(this)) {
      return true;
    } else {
      Throwable cause = this.getCause();
      if (cause == this) {
        return false;
      } else if (cause instanceof CommonRuntimeException) {
        return ((CommonRuntimeException) cause).contains(exType);
      } else {
        while (cause != null) {
          if (exType.isInstance(cause)) {
            return true;
          }

          if (cause.getCause() == cause) {
            break;
          }

          cause = cause.getCause();
        }

        return false;
      }
    }
  }

  static {
    ExceptionUtil.class.getName();
  }
}
