package com.mario.common.model.response;

public class HttpResult<T> {

  private Integer status;
  private T data;
  private boolean success;
  private static final HttpResult TEMP_HTTPRESULT = create(0, (Object) null);

  public HttpResult() {
  }

  private HttpResult(Integer status, T data) {
    this.status = status;
    this.data = data;
    this.success = status != null && status == 200;
  }

  public static <T> HttpResult<T> create(Integer status, T data) {
    return new HttpResult(status, data);
  }

  public static <T> HttpResult<T> failure() {
    return TEMP_HTTPRESULT;
  }

  public Integer getStatus() {
    return this.status;
  }

  public T getData() {
    return this.data;
  }

  public boolean isSuccess() {
    return this.success;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public void setData(T data) {
    this.data = data;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (!(o instanceof HttpResult)) {
      return false;
    } else {
      HttpResult<?> other = (HttpResult) o;
      if (!other.canEqual(this)) {
        return false;
      } else {
        label39:
        {
          Object this$status = this.getStatus();
          Object other$status = other.getStatus();
          if (this$status == null) {
            if (other$status == null) {
              break label39;
            }
          } else if (this$status.equals(other$status)) {
            break label39;
          }

          return false;
        }

        Object this$data = this.getData();
        Object other$data = other.getData();
        if (this$data == null) {
          if (other$data != null) {
            return false;
          }
        } else if (!this$data.equals(other$data)) {
          return false;
        }

        if (this.isSuccess() != other.isSuccess()) {
          return false;
        } else {
          return true;
        }
      }
    }
  }

  protected boolean canEqual(Object other) {
    return other instanceof HttpResult;
  }

  @Override
  public int hashCode() {
    int result = 1;
    Object $status = this.getStatus();
    result = result * 59 + ($status == null ? 43 : $status.hashCode());
    Object $data = this.getData();
    result = result * 59 + ($data == null ? 43 : $data.hashCode());
    result = result * 59 + (this.isSuccess() ? 79 : 97);
    return result;
  }

  @Override
  public String toString() {
    return "HttpResult(status=" + this.getStatus() + ", data=" + this.getData() + ", success="
        + this.isSuccess() + ")";
  }
}