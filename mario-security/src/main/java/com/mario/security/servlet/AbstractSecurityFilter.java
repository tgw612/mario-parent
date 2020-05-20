package com.mario.security.servlet;

import com.mario.security.filter.FilterChainResolver;
import com.mario.security.filter.OncePerRequestFilter;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractSecurityFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(AbstractSecurityFilter.class);

  // Used to determine which chain should handle an incoming request/response
  private FilterChainResolver filterChainResolver;

  protected AbstractSecurityFilter() {
  }

  public FilterChainResolver getFilterChainResolver() {
    return filterChainResolver;
  }

  public void setFilterChainResolver(FilterChainResolver filterChainResolver) {
    this.filterChainResolver = filterChainResolver;
  }

  @Override
  protected final void onFilterConfigSet() throws Exception {
    init();
  }

  public void init() throws Exception {
  }

  @SuppressWarnings({"UnusedDeclaration"})
  protected ServletRequest prepareServletRequest(ServletRequest request, ServletResponse response,
      FilterChain chain) {
    ServletRequest toUse = request;
        /*if (request instanceof HttpServletRequest) {
            HttpServletRequest http = (HttpServletRequest) request;
            toUse = wrapServletRequest(http);
        }*/
    return toUse;
  }

  @SuppressWarnings({"UnusedDeclaration"})
  protected ServletResponse prepareServletResponse(ServletRequest request, ServletResponse response,
      FilterChain chain) {
    ServletResponse toUse = response;
       /* if (!isHttpSessions() && (request instanceof ShiroHttpServletRequest) &&
                (response instanceof HttpServletResponse)) {
            //the ShiroHttpServletResponse exists to support URL rewriting for session ids.  This is only needed if
            //using Shiro sessions (i.e. not simple HttpSession based sessions):
            toUse = wrapServletResponse((HttpServletResponse) response, (ShiroHttpServletRequest) request);
        }*/
    return toUse;
  }

  @SuppressWarnings({"UnusedDeclaration"})
  protected void updateSessionLastAccessTime(ServletRequest request, ServletResponse response) {
        /*if (!isHttpSessions()) { //'native' sessions
            Subject subject = SecurityUtils.getSubject();
            //Subject should never _ever_ be null, but just in case:
            if (subject != null) {
                Session session = subject.getSession(false);
                if (session != null) {
                    try {
                        session.touch();
                    } catch (Throwable t) {
                        log.error("session.touch() method invocation has failed.  Unable to update" +
                                "the corresponding session's last access time based on the incoming request.", t);
                    }
                }
            }
        }*/
  }

  @Override
  protected void doFilterInternal(ServletRequest servletRequest, ServletResponse servletResponse,
      final FilterChain chain)
      throws ServletException, IOException {

    Throwable t = null;

    try {
      final ServletRequest request = prepareServletRequest(servletRequest, servletResponse, chain);
      final ServletResponse response = prepareServletResponse(request, servletResponse, chain);

      updateSessionLastAccessTime(request, response);
      executeChain(request, response, chain);
    } catch (Exception ex) {
      t = ex.getCause();
    } catch (Throwable throwable) {
      t = throwable;
    }

    if (t != null) {
      if (t instanceof ServletException) {
        throw (ServletException) t;
      }
      if (t instanceof IOException) {
        throw (IOException) t;
      }
      String msg = "Filtered request failed.";
      throw new ServletException(msg, t);
    }
  }

  protected FilterChain getExecutionChain(ServletRequest request, ServletResponse response,
      FilterChain origChain) {
    FilterChain chain = origChain;

    FilterChainResolver resolver = getFilterChainResolver();
    if (resolver == null) {
      log.debug("No FilterChainResolver configured.  Returning original FilterChain.");
      return origChain;
    }

    FilterChain resolved = resolver.getChain(request, response, origChain);
    if (resolved != null) {
      log.trace("Resolved a configured FilterChain for the current request.");
      chain = resolved;
    } else {
      log.trace("No FilterChain configured for the current request.  Using the default.");
    }

    return chain;
  }

  protected void executeChain(ServletRequest request, ServletResponse response,
      FilterChain origChain)
      throws IOException, ServletException {
    FilterChain chain = getExecutionChain(request, response, origChain);
    chain.doFilter(request, response);
  }
}

