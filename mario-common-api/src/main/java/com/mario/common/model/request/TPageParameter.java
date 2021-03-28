package com.mario.common.model.request;

import java.io.Serializable;
import java.util.Objects;

public class TPageParameter implements Serializable {
    private static final long serialVersionUID = -2050903899112211502L;
    public static final int DEFAULT_PAGE_SIZE = 10;
    private int pageSize;
    private int currentPage;
    private int totalPage;
    private int totalCount;

    public TPageParameter() {
        this.currentPage = 1;
        this.pageSize = 10;
    }

    public TPageParameter(int currentPage, int pageSize) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    public TPageParameter(int pageSize, int currentPage, int totalPage, int totalCount) {
        this.pageSize = pageSize;
        this.currentPage = currentPage;
        this.totalPage = totalPage;
        this.totalCount = totalCount;
    }

    public int getCurrentPage() {
        return this.currentPage <= 0 ? 1 : this.currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return this.pageSize <= 0 ? 10 : Math.min(this.pageSize, 100);
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return this.totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getTotalCount() {
        return this.totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            TPageParameter that = (TPageParameter)o;
            return this.pageSize == that.pageSize && this.currentPage == that.currentPage && this.totalPage == that.totalPage && this.totalCount == that.totalCount;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.pageSize, this.currentPage, this.totalPage, this.totalCount});
    }

    public String toString() {
        return "TPageParameter{pageSize=" + this.pageSize + ", currentPage=" + this.currentPage + ", totalPage=" + this.totalPage + ", totalCount=" + this.totalCount + '}';
    }

    public static TPageParameter.TPageParameterBuilder builder() {
        return new TPageParameter.TPageParameterBuilder();
    }

    public static class TPageParameterBuilder {
        private int pageSize;
        private int currentPage;
        private int totalPage;
        private int totalCount;

        TPageParameterBuilder() {
        }

        public TPageParameter.TPageParameterBuilder pageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public TPageParameter.TPageParameterBuilder currentPage(int currentPage) {
            this.currentPage = currentPage;
            return this;
        }

        public TPageParameter.TPageParameterBuilder totalPage(int totalPage) {
            this.totalPage = totalPage;
            return this;
        }

        public TPageParameter.TPageParameterBuilder totalCount(int totalCount) {
            this.totalCount = totalCount;
            return this;
        }

        public TPageParameter build() {
            return new TPageParameter(this.pageSize, this.currentPage, this.totalPage, this.totalCount);
        }

        public String toString() {
            return "TPageParameter.TPageParameterBuilder(pageSize=" + this.pageSize + ", currentPage=" + this.currentPage + ", totalPage=" + this.totalPage + ", totalCount=" + this.totalCount + ")";
        }
    }
}
