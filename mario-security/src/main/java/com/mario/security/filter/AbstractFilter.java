package com.mario.security.filter;

import com.mario.common.pattern.PatternMatcher;
import com.mario.common.pattern.ServletPathMatcher;
import com.mario.common.util.StringUtil;
import com.mario.security.servlet.ServletContextSupport;
import com.mario.security.util.WebUtils;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractFilter extends ServletContextSupport implements Filter {

  private static transient final Logger log = LoggerFactory.getLogger(AbstractFilter.class);

  protected FilterConfig filterConfig;

  protected String contextPath;

  public static final String PARAM_NAME_EXCLUSIONS = "exclusions";

  /**
   * PatternMatcher used in determining which paths to react to for a given request.
   */
  protected PatternMatcher pathExclusionMatcher = ServletPathMatcher.getInstance();

  private Set<String> excludesPattern;

  public FilterConfig getFilterConfig() {
    return filterConfig;
  }

  public void setFilterConfig(FilterConfig filterConfig) {
    this.filterConfig = filterConfig;
    this.contextPath = WebUtils.getContextPath(filterConfig.getServletContext());
    setServletContext(filterConfig.getServletContext());
    initExcludesPattern(filterConfig);
  }

  public boolean isExclusion(ServletRequest request) {
    if (excludesPattern == null || excludesPattern.isEmpty()) {
      return false;
    }

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    String requestURI = getRequestURI(httpRequest);

    if (contextPath != null && requestURI.startsWith(contextPath)) {
      requestURI = requestURI.substring(contextPath.length());
      if (!requestURI.startsWith("/")) {
        requestURI = "/" + requestURI;
      }
    }

    for (String pattern : excludesPattern) {
      if (getPathExclusionMatcher().matches(pattern, requestURI)) {
        return true;
      }
    }

    return false;
  }

  public String getRequestURI(HttpServletRequest request) {
    return request.getRequestURI();
  }

  protected void initExcludesPattern(FilterConfig filterConfig) {
    String exclusions = filterConfig.getInitParameter(PARAM_NAME_EXCLUSIONS);
    if (exclusions != null && exclusions.trim().length() != 0) {
      excludesPattern = new HashSet<>(Arrays.asList(exclusions.split("\\s*,\\s*")));
    }
  }

  protected String getInitParam(String paramName) {
    FilterConfig config = getFilterConfig();
    if (config != null) {
      return StringUtil.trimToEmpty(config.getInitParameter(paramName));
    }
    return null;
  }

  @Override
  public final void init(FilterConfig filterConfig) throws ServletException {
    setFilterConfig(filterConfig);
    try {
      onFilterConfigSet();
    } catch (Exception e) {
      if (e instanceof ServletException) {
        throw (ServletException) e;
      } else {
        if (log.isErrorEnabled()) {
          log.error("Unable to start Filter: [" + e.getMessage() + "].", e);
        }
        throw new ServletException(e);
      }
    }
  }

  protected void onFilterConfigSet() throws Exception {
  }

  @Override
  public void destroy() {
  }

  public String getContextPath() {
    return contextPath;
  }

  public void setPathExclusionMatcher(PatternMatcher pathExclusionMatcher) {
    this.pathExclusionMatcher = pathExclusionMatcher;
  }

  public PatternMatcher getPathExclusionMatcher() {
    return pathExclusionMatcher;
  }

  public void setExcludesPattern(Set<String> excludesPattern) {
    this.excludesPattern = excludesPattern;
  }

  public Set<String> getExcludesPattern() {
    return excludesPattern;
  }
}
