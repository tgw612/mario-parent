package com.mario.common.util;

import com.google.common.base.Throwables;
import com.mario.common.constants.CommonConstants;
import com.mario.common.exception.DataBaseOperateException;
import com.mario.common.exception.RemoteAccessTimeOutException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nullable;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

public abstract class ExceptionUtil {

  private static final StackTraceElement[] EMPTY_STACK_TRACE = new StackTraceElement[0];

  public ExceptionUtil() {
  }

  public static Throwable getRootCause(Throwable throwable) {
    Throwable cause;
    while ((cause = throwable.getCause()) != null) {
      throwable = cause;
    }

    return throwable;
  }

  public static List<Throwable> getCausalChain(Throwable throwable) {
    ArrayList causes;
    for (causes = new ArrayList(4); throwable != null; throwable = throwable.getCause()) {
      causes.add(throwable);
    }

    return Collections.unmodifiableList(causes);
  }

  public static String getAsString(Throwable throwable) {
    StringWriter stringWriter = new StringWriter(50);
    throwable.printStackTrace(new PrintWriter(stringWriter));
    return stringWriter.toString();
  }

  public static DataBaseOperateException translateDataBaseException() {
    return CommonConstants.DATA_BASE_OPERATE_EXCEPTION;
  }

  public static void throwIfTimeoutException(Throwable e) {
    while (e != null) {
      if (e instanceof SocketTimeoutException) {
        throw new RemoteAccessTimeOutException();
      }

      e = e.getCause();
    }

  }


  public static String buildMessage(String message, Throwable cause) {
    if (cause != null) {
      StringBuilder sb = new StringBuilder();
      if (message != null) {
        sb.append(message).append("; ");
      }

      sb.append("nested exception is ").append(cause);
      return sb.toString();
    } else {
      return message;
    }
  }

  public static RuntimeException unchecked(Throwable t) {
    if (t instanceof RuntimeException) {
      throw (RuntimeException) t;
    } else if (t instanceof Error) {
      throw (Error) t;
    } else {
      throw new ExceptionUtil.UncheckedException(t);
    }
  }

  public static Throwable unwrap(Throwable t) {
    return !(t instanceof ExecutionException) && !(t instanceof InvocationTargetException)
        && !(t instanceof ExceptionUtil.UncheckedException) ? t : t.getCause();
  }

  public static RuntimeException uncheckedAndWrap(Throwable t) {
    Throwable unwrapped = unwrap(t);
    if (unwrapped instanceof RuntimeException) {
      throw (RuntimeException) unwrapped;
    } else if (unwrapped instanceof Error) {
      throw (Error) unwrapped;
    } else {
      throw new ExceptionUtil.UncheckedException(unwrapped);
    }
  }

  public static String stackTraceText(Throwable t) {
    return Throwables.getStackTraceAsString(t);
  }

  public static boolean isCausedBy(Throwable t,
      Class<? extends Exception>... causeExceptionClasses) {
    for (Throwable cause = t; cause != null; cause = cause.getCause()) {
      Class[] var3 = causeExceptionClasses;
      int var4 = causeExceptionClasses.length;

      for (int var5 = 0; var5 < var4; ++var5) {
        Class<? extends Exception> causeClass = var3[var5];
        if (causeClass.isInstance(cause)) {
          return true;
        }
      }
    }

    return false;
  }

  public static String toStringWithShortName(@Nullable Throwable t) {
    return ExceptionUtils.getMessage(t);
  }

  public static String toStringWithRootCause(@Nullable Throwable t) {
    if (t == null) {
      return "";
    } else {
      String clsName = ClassUtils.getShortClassName(t, (String) null);
      String message = StringUtils.defaultString(t.getMessage());
      Throwable cause = getRootCause(t);
      StringBuilder sb = (new StringBuilder(128)).append(clsName).append(": ").append(message);
      if (cause != t) {
        sb.append("; <---").append(toStringWithShortName(cause));
      }

      return sb.toString();
    }
  }

  public static <T extends Throwable> T setStackTrace(T exception, Class<?> throwClass,
      String throwClazz) {
    exception.setStackTrace(new StackTraceElement[]{
        new StackTraceElement(throwClass.getName(), throwClazz, (String) null, -1)});
    return exception;
  }

  public static <T extends Throwable> T clearStackTrace(T exception) {
    for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
      cause.setStackTrace(EMPTY_STACK_TRACE);
    }

    return exception;
  }

  public static class CloneableRuntimeException extends RuntimeException implements Cloneable {

    private static final long serialVersionUID = 3984796576627959400L;
    protected String message;

    public CloneableRuntimeException() {
      super((Throwable) null);
    }

    public CloneableRuntimeException(String message) {
      super((Throwable) null);
      this.message = message;
    }

    public CloneableRuntimeException(String message, Throwable cause) {
      super(cause);
      this.message = message;
    }

    @Override
    public ExceptionUtil.CloneableRuntimeException clone() {
      try {
        return (ExceptionUtil.CloneableRuntimeException) super.clone();
      } catch (CloneNotSupportedException var2) {
        return null;
      }
    }

    @Override
    public String getMessage() {
      return this.message;
    }

    public ExceptionUtil.CloneableRuntimeException setStackTrace(Class<?> throwClazz,
        String throwMethod) {
      ExceptionUtil.setStackTrace(this, throwClazz, throwMethod);
      return this;
    }

    public ExceptionUtil.CloneableRuntimeException clone(String message) {
      ExceptionUtil.CloneableRuntimeException newException = this.clone();
      newException.setMessage(message);
      return newException;
    }

    public ExceptionUtil.CloneableRuntimeException setMessage(String message) {
      this.message = message;
      return this;
    }
  }

  public static class CloneableException extends Exception implements Cloneable {

    private static final long serialVersionUID = -6270471689928560417L;
    protected String message;

    public CloneableException() {
      super((Throwable) null);
    }

    public CloneableException(String message) {
      super((Throwable) null);
      this.message = message;
    }

    public CloneableException(String message, Throwable cause) {
      super(cause);
      this.message = message;
    }

    @Override
    public ExceptionUtil.CloneableException clone() {
      try {
        return (ExceptionUtil.CloneableException) super.clone();
      } catch (CloneNotSupportedException var2) {
        return null;
      }
    }

    @Override
    public String getMessage() {
      return this.message;
    }

    public ExceptionUtil.CloneableException setStackTrace(Class<?> throwClazz, String throwMethod) {
      ExceptionUtil.setStackTrace(this, throwClazz, throwMethod);
      return this;
    }

    public ExceptionUtil.CloneableException clone(String message) {
      ExceptionUtil.CloneableException newException = this.clone();
      newException.setMessage(message);
      return newException;
    }

    public ExceptionUtil.CloneableException setMessage(String message) {
      this.message = message;
      return this;
    }
  }

  public static class UncheckedException extends RuntimeException {

    private static final long serialVersionUID = 4140223302171577501L;

    public UncheckedException(Throwable cause) {
      super(cause);
    }

    @Override
    public String getMessage() {
      return super.getCause().getMessage();
    }
  }
}
