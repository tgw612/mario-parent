package com.mario.common.model.request;

import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotNull;

public class CommonMuliPrimaryKeyRequest<T> extends CommonRequest implements
    MuliPrimaryKeyRequest<T>, Serializable {

  private static final long serialVersionUID = 7953105286146212685L;
  @NotNull(
      message = "ID集合不能为空"
  )
  private List<T> ids;

  public CommonMuliPrimaryKeyRequest() {
  }

  @Override
  public List<T> getIds() {
    return this.ids;
  }

  public void setIds(List<T> ids) {
    this.ids = ids;
  }

  @Override
  public String toString() {
    return "CommonMuliPrimaryKeyRequest(super=" + super.toString() + ", ids=" + this.getIds() + ")";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (!(o instanceof CommonMuliPrimaryKeyRequest)) {
      return false;
    } else {
      CommonMuliPrimaryKeyRequest<?> other = (CommonMuliPrimaryKeyRequest) o;
      if (!other.canEqual(this)) {
        return false;
      } else if (!super.equals(o)) {
        return false;
      } else {
        Object this$ids = this.getIds();
        Object other$ids = other.getIds();
        if (this$ids == null) {
          if (other$ids != null) {
            return false;
          }
        } else if (!this$ids.equals(other$ids)) {
          return false;
        }

        return true;
      }
    }
  }

  @Override
  protected boolean canEqual(Object other) {
    return other instanceof CommonMuliPrimaryKeyRequest;
  }

  @Override
  public int hashCode() {
    int result = 1;
    result = result * 59 + super.hashCode();
    Object $ids = this.getIds();
    result = result * 59 + ($ids == null ? 43 : $ids.hashCode());
    return result;
  }
}
