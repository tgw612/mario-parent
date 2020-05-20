package com.mario.security.servlet;

import java.io.IOException;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxiedFilterChain implements FilterChain {

  //TODO - complete JavaDoc

  private static final Logger log = LoggerFactory.getLogger(ProxiedFilterChain.class);

  private FilterChain orig;
  private List<Filter> filters;
  private int index = 0;

  public ProxiedFilterChain(FilterChain orig, List<Filter> filters) {
    if (orig == null) {
      throw new NullPointerException("original FilterChain cannot be null.");
    }
    this.orig = orig;
    this.filters = filters;
    this.index = 0;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response)
      throws IOException, ServletException {
    if (this.filters == null || this.filters.size() == this.index) {
      //we've reached the end of the wrapped chain, so invoke the original one:
      if (log.isTraceEnabled()) {
        log.trace("Invoking original filter chain.");
      }
      this.orig.doFilter(request, response);
    } else {
      if (log.isTraceEnabled()) {
        log.trace("Invoking wrapped filter at index [" + this.index + "]");
      }
      this.filters.get(this.index++).doFilter(request, response, this);
    }
  }
}
