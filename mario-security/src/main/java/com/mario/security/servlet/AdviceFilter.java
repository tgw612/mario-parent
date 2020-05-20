package com.mario.security.servlet;

import com.mario.security.filter.OncePerRequestFilter;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdviceFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(AdviceFilter.class);

  protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
    return true;
  }

  @SuppressWarnings({"UnusedDeclaration"})
  protected void postHandle(ServletRequest request, ServletResponse response) throws Exception {
  }

  @SuppressWarnings({"UnusedDeclaration"})
  public void afterCompletion(ServletRequest request, ServletResponse response, Exception exception)
      throws Exception {
  }

  protected void executeChain(ServletRequest request, ServletResponse response, FilterChain chain)
      throws Exception {
    chain.doFilter(request, response);
  }

  @Override
  public void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    Exception exception = null;

    try {

      boolean continueChain = preHandle(request, response);
      if (log.isTraceEnabled()) {
        log.trace("Invoked preHandle method.  Continuing chain?: [" + continueChain + "]");
      }

      if (continueChain) {
        executeChain(request, response, chain);
      }

      postHandle(request, response);
      if (log.isTraceEnabled()) {
        log.trace("Successfully invoked postHandle method");
      }

    } catch (Exception e) {
      exception = e;
    } finally {
      cleanup(request, response, exception);
    }
  }

  protected void cleanup(ServletRequest request, ServletResponse response, Exception existing)
      throws ServletException, IOException {
    Exception exception = existing;
    try {
      afterCompletion(request, response, exception);
      if (log.isTraceEnabled()) {
        log.trace("Successfully invoked afterCompletion method.");
      }
    } catch (Exception e) {
      if (exception == null) {
        exception = e;
      } else {
        log.debug("afterCompletion implementation threw an exception.  This will be ignored to " +
            "allow the original source exception to be propagated.", e);
      }
    }
    if (exception != null) {
      if (exception instanceof ServletException) {
        throw (ServletException) exception;
      } else if (exception instanceof IOException) {
        throw (IOException) exception;
      } else {
        if (log.isDebugEnabled()) {
          String msg = "Filter execution resulted in an unexpected Exception " +
              "(not IOException or ServletException as the Filter API recommends).  " +
              "Wrapping in ServletException and propagating.";
          log.debug(msg);
        }
        throw new ServletException(exception);
      }
    }
  }
}
