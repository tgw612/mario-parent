package com.mario.common.model.request;

import java.io.Serializable;

public class PageParameter implements Serializable {
    private static final long serialVersionUID = 7778954621301304548L;
    public static final int DEFAULT_PAGE_SIZE = 10;
    private int pageSize;
    private int currentPage;
    private int totalPage;
    private int totalCount;

    public PageParameter() {
        this.currentPage = 1;
        this.pageSize = 10;
    }

    public PageParameter(int currentPage, int pageSize) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    public PageParameter(int pageSize, int currentPage, int totalPage, int totalCount) {
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
            PageParameter that = (PageParameter) o;
            if (this.pageSize != that.pageSize) {
                return false;
            } else if (this.currentPage != that.currentPage) {
                return false;
            } else if (this.totalPage != that.totalPage) {
                return false;
            } else {
                return this.totalCount == that.totalCount;
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        int result = this.pageSize;
        result = 31 * result + this.currentPage;
        result = 31 * result + this.totalPage;
        result = 31 * result + this.totalCount;
        return result;
    }

    public String toString() {
        return "PageParameter{pageSize=" + this.pageSize + ", currentPage=" + this.currentPage + ", totalPage=" + this.totalPage + ", totalCount=" + this.totalCount + '}';
    }

    public static PageParameter.PageParameterBuilder builder() {
        return new PageParameter.PageParameterBuilder();
    }

    public static class PageParameterBuilder {
        private int pageSize;
        private int currentPage;
        private int totalPage;
        private int totalCount;

        PageParameterBuilder() {
        }

        public PageParameter.PageParameterBuilder pageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public PageParameter.PageParameterBuilder currentPage(int currentPage) {
            this.currentPage = currentPage;
            return this;
        }

        public PageParameter.PageParameterBuilder totalPage(int totalPage) {
            this.totalPage = totalPage;
            return this;
        }

        public PageParameter.PageParameterBuilder totalCount(int totalCount) {
            this.totalCount = totalCount;
            return this;
        }

        public PageParameter build() {
            return new PageParameter(this.pageSize, this.currentPage, this.totalPage, this.totalCount);
        }

        public String toString() {
            return "PageParameter.PageParameterBuilder(pageSize=" + this.pageSize + ", currentPage=" + this.currentPage + ", totalPage=" + this.totalPage + ", totalCount=" + this.totalCount + ")";
        }
    }
}