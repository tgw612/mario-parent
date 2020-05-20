package com.mario.security.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class OncePerRequestFilter extends NameableFilter {

  private static final Logger log = LoggerFactory.getLogger(OncePerRequestFilter.class);

  public static final String ALREADY_FILTERED_SUFFIX = ".FILTERED";

  private boolean enabled = true; //most filters wish to execute when configured, so default to true

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  @Override
  public final void doFilter(ServletRequest request, ServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {

    if (isExclusion(request)) {
      filterChain.doFilter(request, response);
      return;
    }

    String alreadyFilteredAttributeName = getAlreadyFilteredAttributeName();
    if (request.getAttribute(alreadyFilteredAttributeName) != null) {
      log.trace("Filter '{}' already executed.  Proceeding without invoking this filter.",
          getName());
      filterChain.doFilter(request, response);
    } else {
      if (!isEnabled(request, response)) {
        log.debug(
            "Filter '{}' is not enabled for the current request.  Proceeding without invoking this filter.",
            getName());
        filterChain.doFilter(request, response);
      } else {
        log.trace("Filter '{}' not yet executed.  Executing now.", getName());
        request.setAttribute(alreadyFilteredAttributeName, Boolean.TRUE);

        try {
          doFilterInternal(request, response, filterChain);
        } finally {
          request.removeAttribute(alreadyFilteredAttributeName);
        }
      }
    }

  }

  @SuppressWarnings({"UnusedParameters"})
  protected boolean isEnabled(ServletRequest request, ServletResponse response)
      throws ServletException, IOException {
    return isEnabled();
  }

  protected String getAlreadyFilteredAttributeName() {
    String name = getName();
    if (name == null) {
      name = getClass().getName();
    }
    return name + ALREADY_FILTERED_SUFFIX;
  }

  protected abstract void doFilterInternal(ServletRequest request, ServletResponse response,
      FilterChain chain)
      throws ServletException, IOException;

  protected HttpServletRequest getHttpServletRequest(ServletRequest request) {
    return (HttpServletRequest) request;
  }

  protected HttpServletResponse getHttpServletResponse(ServletResponse response) {
    return (HttpServletResponse) response;
  }
}
