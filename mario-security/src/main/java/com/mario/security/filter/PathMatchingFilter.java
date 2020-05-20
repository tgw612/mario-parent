package com.mario.security.filter;

import com.mario.common.pattern.AntPathMatcher;
import com.mario.common.pattern.PatternMatcher;
import com.mario.common.util.StringUtil;
import com.mario.security.util.WebUtils;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PathMatchingFilter extends AdviceFilter implements PathConfigProcessor {

  private static final Logger log = LoggerFactory.getLogger(PathMatchingFilter.class);

  protected PatternMatcher pathMatcher = AntPathMatcher.getInstance();

  protected Map<String, Object> appliedPaths = new LinkedHashMap<String, Object>();

  @Override
  public Filter processPathConfig(String path, String config) {
    String[] values = null;
    if (config != null) {
      values = StringUtil.split(config);
    }

    this.appliedPaths.put(path, values);
    return this;
  }

  protected String getPathWithinApplication(ServletRequest request) {
    return WebUtils.getPathWithinApplication(WebUtils.toHttp(request));
  }

  protected boolean pathsMatch(String path, ServletRequest request) {
    String requestURI = getPathWithinApplication(request);
    log.trace("Attempting to match pattern '{}' with current requestURI '{}'...", path, requestURI);
    return pathsMatch(path, requestURI);
  }

  protected boolean pathsMatch(String pattern, String path) {
    return getPathMatcher().matches(pattern, path);
  }

  @Override
  protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {

    if (this.appliedPaths == null || this.appliedPaths.isEmpty()) {
      if (log.isTraceEnabled()) {
        log.trace(
            "appliedPaths property is null or empty.  This Filter will passthrough immediately.");
      }
      return true;
    }

    for (String path : this.appliedPaths.keySet()) {
      // If the path does match, then pass on to the subclass implementation for specific checks
      //(first match 'wins'):
      if (pathsMatch(path, request)) {
        log.trace("Current requestURI matches pattern '{}'.  Determining filter chain execution...",
            path);
        Object config = this.appliedPaths.get(path);
        return isFilterChainContinued(request, response, path, config);
      }
    }

    //no path matched, allow the request to go through:
    return true;
  }

  /**
   * Simple method to abstract out logic from the preHandle implementation - it was getting a bit
   * unruly.
   *
   * @since 1.2
   */
  @SuppressWarnings({"JavaDoc"})
  private boolean isFilterChainContinued(ServletRequest request, ServletResponse response,
      String path, Object pathConfig) throws Exception {

    if (isEnabled(request, response, path, pathConfig)) { //isEnabled check added in 1.2
      if (log.isTraceEnabled()) {
        log.trace(
            "Filter '{}' is enabled for the current request under path '{}' with config [{}].  " +
                "Delegating to subclass implementation for 'onPreHandle' check.",
            new Object[]{getName(), path, pathConfig});
      }
      return onPreHandle(request, response, pathConfig);
    }

    if (log.isTraceEnabled()) {
      log.trace(
          "Filter '{}' is disabled for the current request under path '{}' with config [{}].  " +
              "The next element in the FilterChain will be called immediately.",
          new Object[]{getName(), path, pathConfig});
    }
    return true;
  }

  protected boolean onPreHandle(ServletRequest request, ServletResponse response,
      Object mappedValue) throws Exception {
    return true;
  }

  @SuppressWarnings({"UnusedParameters"})
  protected boolean isEnabled(ServletRequest request, ServletResponse response, String path,
      Object mappedValue)
      throws Exception {
    return isEnabled(request, response);
  }

  public PatternMatcher getPathMatcher() {
    return pathMatcher;
  }

  public void setPathMatcher(PatternMatcher pathMatcher) {
    this.pathMatcher = pathMatcher;
  }
}
