package com.mario.security.mgt;

import com.mario.common.pattern.AntPathMatcher;
import com.mario.common.pattern.PatternMatcher;
import com.mario.security.filter.FilterChainResolver;
import com.mario.security.util.WebUtils;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathMatchingFilterChainResolver implements FilterChainResolver {

  private static transient final Logger log = LoggerFactory
      .getLogger(PathMatchingFilterChainResolver.class);

  private FilterChainManager filterChainManager;

  private PatternMatcher pathMatcher;

  public PathMatchingFilterChainResolver() {
    this.pathMatcher = new AntPathMatcher();
    this.filterChainManager = new DefaultFilterChainManager();
  }

  public PathMatchingFilterChainResolver(FilterConfig filterConfig) {
    this.pathMatcher = new AntPathMatcher();
    this.filterChainManager = new DefaultFilterChainManager(filterConfig);
  }

  public PatternMatcher getPathMatcher() {
    return pathMatcher;
  }

  public void setPathMatcher(PatternMatcher pathMatcher) {
    this.pathMatcher = pathMatcher;
  }

  public FilterChainManager getFilterChainManager() {
    return filterChainManager;
  }

  @SuppressWarnings({"UnusedDeclaration"})
  public void setFilterChainManager(FilterChainManager filterChainManager) {
    this.filterChainManager = filterChainManager;
  }

  @Override
  public FilterChain getChain(ServletRequest request, ServletResponse response,
      FilterChain originalChain) {
    FilterChainManager filterChainManager = getFilterChainManager();
    if (!filterChainManager.hasChains()) {
      return null;
    }

    String requestURI = getPathWithinApplication(request);

    for (String pathPattern : filterChainManager.getChainNames()) {

      if (pathMatches(pathPattern, requestURI)) {
        if (log.isTraceEnabled()) {
          log.trace(
              "Matched path pattern [" + pathPattern + "] for requestURI [" + requestURI + "].  " +
                  "Utilizing corresponding filter chain...");
        }
        return filterChainManager.proxy(originalChain, pathPattern);
      }
    }

    return null;
  }

  protected boolean pathMatches(String pattern, String path) {
    PatternMatcher pathMatcher = getPathMatcher();
    return pathMatcher.matches(pattern, path);
  }

  protected String getPathWithinApplication(ServletRequest request) {
    return WebUtils.getPathWithinApplication(WebUtils.toHttp(request));
  }
}
