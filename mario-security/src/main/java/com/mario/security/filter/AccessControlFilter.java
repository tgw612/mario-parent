package com.mario.security.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public abstract class AccessControlFilter extends PathMatchingFilter {

  protected abstract boolean isAccessAllowed(ServletRequest request, ServletResponse response,
      Object mappedValue) throws Exception;

  protected boolean onAccessDenied(ServletRequest request, ServletResponse response,
      Object mappedValue) throws Exception {
    return onAccessDenied(request, response);
  }

  protected abstract boolean onAccessDenied(ServletRequest request, ServletResponse response)
      throws Exception;

  @Override
  public boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue)
      throws Exception {
    return isAccessAllowed(request, response, mappedValue) || onAccessDenied(request, response,
        mappedValue);
  }
}
