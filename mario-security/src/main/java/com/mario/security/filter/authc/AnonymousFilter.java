package com.mario.security.filter.authc;

import com.mario.security.filter.PathMatchingFilter;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class AnonymousFilter extends PathMatchingFilter {

  /**
   * Always returns <code>true</code> allowing unchecked access to the underlying path or resource.
   *
   * @return <code>true</code> always, allowing unchecked access to the underlying path or resource.
   */
  @Override
  protected boolean onPreHandle(ServletRequest request, ServletResponse response,
      Object mappedValue) {
    // Always return true since we allow access to anyone
    return true;
  }

}
