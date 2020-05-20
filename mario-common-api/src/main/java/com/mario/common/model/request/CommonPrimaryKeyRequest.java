package com.mario.common.model.request;

import java.io.Serializable;
import javax.validation.constraints.NotNull;

public class CommonPrimaryKeyRequest<T> extends CommonRequest implements PrimaryKeyRequest<T>,
    Serializable {

  private static final long serialVersionUID = 1932645516819856826L;
  @NotNull(
      message = "ID不能为空"
  )
  private T id;

  public CommonPrimaryKeyRequest() {
  }

  @Override
  public T getId() {
    return this.id;
  }

  public void setId(T id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "CommonPrimaryKeyRequest(super=" + super.toString() + ", id=" + this.getId() + ")";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (!(o instanceof CommonPrimaryKeyRequest)) {
      return false;
    } else {
      CommonPrimaryKeyRequest<?> other = (CommonPrimaryKeyRequest) o;
      if (!other.canEqual(this)) {
        return false;
      } else if (!super.equals(o)) {
        return false;
      } else {
        Object this$id = this.getId();
        Object other$id = other.getId();
        if (this$id == null) {
          if (other$id != null) {
            return false;
          }
        } else if (!this$id.equals(other$id)) {
          return false;
        }

        return true;
      }
    }
  }

  @Override
  protected boolean canEqual(Object other) {
    return other instanceof CommonPrimaryKeyRequest;
  }

  @Override
  public int hashCode() {
    int result = 1;
    result = result * 59 + super.hashCode();
    Object $id = this.getId();
    result = result * 59 + ($id == null ? 43 : $id.hashCode());
    return result;
  }
}

