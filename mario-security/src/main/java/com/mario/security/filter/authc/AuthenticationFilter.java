package com.mario.security.filter.authc;

import com.mario.security.filter.AccessControlFilter;
import com.mario.security.subject.Subject;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AuthenticationFilter extends AccessControlFilter {

  @Override
  protected boolean isAccessAllowed(ServletRequest request, ServletResponse response,
      Object mappedValue) {
    Subject subject = getSubject(getHttpServletRequest(request), getHttpServletResponse(response));
    return subject != null && subject.isAuthenticated();
  }

  protected abstract Subject getSubject(HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse);

}
