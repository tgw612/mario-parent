package com.mario.common.model.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CommonPageResult<PD> implements Serializable {

  private static final long serialVersionUID = -6790824298186846296L;
  private long totalCount = 0L;
  private long totalPage = 0L;
  private List<PD> data = new ArrayList();

  @Override
  public String toString() {
    String dataTemp = null;
    if (this.getData() != null) {
      if (this.getData().size() <= 20) {
        dataTemp = this.getData().toString();
      } else {
        dataTemp = "[...]";
      }
    }

    return "CommonPageResult(totalCount=" + this.getTotalCount() + ", data=" + dataTemp + ")";
  }

  public CommonPageResult() {
  }

  public long getTotalCount() {
    return this.totalCount;
  }

  public long getTotalPage() {
    return this.totalPage;
  }

  public List<PD> getData() {
    return this.data;
  }

  public void setTotalCount(long totalCount) {
    this.totalCount = totalCount;
  }

  public void setTotalPage(long totalPage) {
    this.totalPage = totalPage;
  }

  public void setData(List<PD> data) {
    this.data = data;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (!(o instanceof CommonPageResult)) {
      return false;
    } else {
      CommonPageResult<?> other = (CommonPageResult) o;
      if (!other.canEqual(this)) {
        return false;
      } else if (this.getTotalCount() != other.getTotalCount()) {
        return false;
      } else if (this.getTotalPage() != other.getTotalPage()) {
        return false;
      } else {
        Object this$data = this.getData();
        Object other$data = other.getData();
        if (this$data == null) {
          if (other$data == null) {
            return true;
          }
        } else if (this$data.equals(other$data)) {
          return true;
        }

        return false;
      }
    }
  }

  protected boolean canEqual(Object other) {
    return other instanceof CommonPageResult;
  }

  @Override
  public int hashCode() {
    int result = 1;
    long $totalCount = this.getTotalCount();
    result = result * 59 + (int) ($totalCount >>> 32 ^ $totalCount);
    long $totalPage = this.getTotalPage();
    result = result * 59 + (int) ($totalPage >>> 32 ^ $totalPage);
    Object $data = this.getData();
    result = result * 59 + ($data == null ? 43 : $data.hashCode());
    return result;
  }
}