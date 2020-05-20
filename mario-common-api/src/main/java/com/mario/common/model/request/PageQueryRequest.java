package com.mario.common.model.request;

import java.io.Serializable;

public class PageQueryRequest extends CommonRequest implements Serializable {

  private static final long serialVersionUID = 5750491879573083249L;
  private Integer currentPage;
  private Integer pageSize;

  public PageQueryRequest() {
  }

  public Integer getCurrentPage() {
    return this.currentPage != null && this.currentPage > 0 ? this.currentPage : 1;
  }

  public Integer getPageSize() {
    return this.pageSize != null && this.pageSize > 0 ? Math.min(this.pageSize, 100) : 10;
  }

  @Override
  public String toString() {
    return "PageQueryRequest(super=" + super.toString() + ", currentPage=" + this.getCurrentPage()
        + ", pageSize=" + this.getPageSize() + ")";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (!(o instanceof PageQueryRequest)) {
      return false;
    } else {
      PageQueryRequest other = (PageQueryRequest) o;
      if (!other.canEqual(this)) {
        return false;
      } else if (!super.equals(o)) {
        return false;
      } else {
        Object this$currentPage = this.getCurrentPage();
        Object other$currentPage = other.getCurrentPage();
        if (this$currentPage == null) {
          if (other$currentPage != null) {
            return false;
          }
        } else if (!this$currentPage.equals(other$currentPage)) {
          return false;
        }

        Object this$pageSize = this.getPageSize();
        Object other$pageSize = other.getPageSize();
        if (this$pageSize == null) {
          if (other$pageSize != null) {
            return false;
          }
        } else if (!this$pageSize.equals(other$pageSize)) {
          return false;
        }

        return true;
      }
    }
  }

  @Override
  protected boolean canEqual(Object other) {
    return other instanceof PageQueryRequest;
  }

  @Override
  public int hashCode() {
    int result = 1;
    result = result * 59 + super.hashCode();
    Object $currentPage = this.getCurrentPage();
    result = result * 59 + ($currentPage == null ? 43 : $currentPage.hashCode());
    Object $pageSize = this.getPageSize();
    result = result * 59 + ($pageSize == null ? 43 : $pageSize.hashCode());
    return result;
  }

  public void setCurrentPage(Integer currentPage) {
    this.currentPage = currentPage;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }
}
