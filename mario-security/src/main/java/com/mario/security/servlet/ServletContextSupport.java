package com.mario.security.servlet;

import javax.servlet.ServletContext;

public class ServletContextSupport {

  //TODO - complete JavaDoc
  private ServletContext servletContext = null;

  public ServletContext getServletContext() {
    return servletContext;
  }

  public void setServletContext(ServletContext servletContext) {
    this.servletContext = servletContext;
  }

  @SuppressWarnings({"UnusedDeclaration"})
  protected String getContextInitParam(String paramName) {
    return getServletContext().getInitParameter(paramName);
  }

  private ServletContext getRequiredServletContext() {
    ServletContext servletContext = getServletContext();
    if (servletContext == null) {
      String msg = "ServletContext property must be set via the setServletContext method.";
      throw new IllegalStateException(msg);
    }
    return servletContext;
  }

  @SuppressWarnings({"UnusedDeclaration"})
  protected void setContextAttribute(String key, Object value) {
    if (value == null) {
      removeContextAttribute(key);
    } else {
      getRequiredServletContext().setAttribute(key, value);
    }
  }

  @SuppressWarnings({"UnusedDeclaration"})
  protected Object getContextAttribute(String key) {
    return getRequiredServletContext().getAttribute(key);
  }

  protected void removeContextAttribute(String key) {
    getRequiredServletContext().removeAttribute(key);
  }

  @Override
  public String toString() {
    return toStringBuilder().toString();
  }

  protected StringBuilder toStringBuilder() {
    return new StringBuilder(super.toString());
  }
}
