package com.mario.security.util;

import com.mario.common.util.StringUtil;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebUtils {

  private static final Logger log = LoggerFactory.getLogger(WebUtils.class);

  public static final String INCLUDE_REQUEST_URI_ATTRIBUTE = "javax.servlet.include.request_uri";
  public static final String INCLUDE_CONTEXT_PATH_ATTRIBUTE = "javax.servlet.include.context_path";

  public static final String DEFAULT_CHARACTER_ENCODING = "ISO-8859-1";

  public static String getPathWithinApplication(HttpServletRequest request) {
    String contextPath = getContextPath(request);
    String requestUri = getRequestUri(request);
    if (StringUtil.startsWithIgnoreCase(requestUri, contextPath)) {
      // Normal case: URI contains context path.
      String path = requestUri.substring(contextPath.length());
      return (StringUtil.hasText(path) ? path : "/");
    } else {
      // Special case: rather unusual.
      return requestUri;
    }
  }

  public static String getRequestUri(HttpServletRequest request) {
    String uri = (String) request.getAttribute(INCLUDE_REQUEST_URI_ATTRIBUTE);
    if (uri == null) {
      uri = request.getRequestURI();
    }
    return normalize(decodeAndCleanUriString(request, uri));
  }

  public static String normalize(String path) {
    return normalize(path, true);
  }

  private static String normalize(String path, boolean replaceBackSlash) {

    if (path == null) {
      return null;
    }

    // Create a place for the normalized path
    String normalized = path;

    if (replaceBackSlash && normalized.indexOf('\\') >= 0) {
      normalized = normalized.replace('\\', '/');
    }

    if (normalized.equals("/.")) {
      return "/";
    }

    // Add a leading "/" if necessary
    if (!normalized.startsWith("/")) {
      normalized = "/" + normalized;
    }

    // Resolve occurrences of "//" in the normalized path
    while (true) {
      int index = normalized.indexOf("//");
      if (index < 0) {
        break;
      }
      normalized = normalized.substring(0, index) +
          normalized.substring(index + 1);
    }

    // Resolve occurrences of "/./" in the normalized path
    while (true) {
      int index = normalized.indexOf("/./");
      if (index < 0) {
        break;
      }
      normalized = normalized.substring(0, index) +
          normalized.substring(index + 2);
    }

    // Resolve occurrences of "/../" in the normalized path
    while (true) {
      int index = normalized.indexOf("/../");
      if (index < 0) {
        break;
      }
      if (index == 0) {
        return (null);  // Trying to go outside our context
      }
      int index2 = normalized.lastIndexOf('/', index - 1);
      normalized = normalized.substring(0, index2) +
          normalized.substring(index + 3);
    }

    // Return the normalized path that we have completed
    return (normalized);

  }

  private static String decodeAndCleanUriString(HttpServletRequest request, String uri) {
    uri = decodeRequestString(request, uri);
    int semicolonIndex = uri.indexOf(';');
    return (semicolonIndex != -1 ? uri.substring(0, semicolonIndex) : uri);
  }

  public static String getContextPath(HttpServletRequest request) {
    String contextPath = (String) request.getAttribute(INCLUDE_CONTEXT_PATH_ATTRIBUTE);
    if (contextPath == null) {
      contextPath = request.getContextPath();
    }
    if ("/".equals(contextPath)) {
      // Invalid case, but happens for includes on Jetty: silently adapt it.
      contextPath = "";
    }
    return decodeRequestString(request, contextPath);
  }

  @SuppressWarnings({"deprecation"})
  public static String decodeRequestString(HttpServletRequest request, String source) {
    String enc = determineEncoding(request);
    try {
      return URLDecoder.decode(source, enc);
    } catch (UnsupportedEncodingException ex) {
      if (log.isWarnEnabled()) {
        log.warn("Could not decode request string [" + source + "] with encoding '" + enc +
            "': falling back to platform default encoding; exception message: " + ex.getMessage());
      }
      return URLDecoder.decode(source);
    }
  }

  protected static String determineEncoding(HttpServletRequest request) {
    String enc = request.getCharacterEncoding();
    if (enc == null) {
      enc = DEFAULT_CHARACTER_ENCODING;
    }
    return enc;
  }

  public static HttpServletRequest toHttp(ServletRequest request) {
    return (HttpServletRequest) request;
  }

  public static HttpServletResponse toHttp(ServletResponse response) {
    return (HttpServletResponse) response;
  }

  public static boolean isTrue(ServletRequest request, String paramName) {
    String value = getCleanParam(request, paramName);
    return value != null &&
        (value.equalsIgnoreCase("true") ||
            value.equalsIgnoreCase("t") ||
            value.equalsIgnoreCase("1") ||
            value.equalsIgnoreCase("enabled") ||
            value.equalsIgnoreCase("y") ||
            value.equalsIgnoreCase("yes") ||
            value.equalsIgnoreCase("on"));
  }

  public static String getCleanParam(ServletRequest request, String paramName) {
    return StringUtil.clean(request.getParameter(paramName));
  }

  public static String getRemoteAddr(HttpServletRequest request) {
    String ip = request.getHeader("x-forwarded-for");
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }

    return ip;
  }

  private static String getContextPath_2_5(ServletContext context) {
    String contextPath = context.getContextPath();

    if (contextPath == null || contextPath.length() == 0) {
      contextPath = "/";
    }

    return contextPath;
  }

  public static String getContextPath(ServletContext context) {
    if (context.getMajorVersion() == 2 && context.getMinorVersion() < 5) {
      return null;
    }

    try {
      return getContextPath_2_5(context);
    } catch (NoSuchMethodError error) {
      return null;
    }
  }
}
