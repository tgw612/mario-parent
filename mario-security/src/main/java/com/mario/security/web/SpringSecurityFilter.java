package com.mario.security.web;

import com.mario.security.filter.FilterChainResolver;
import com.mario.security.servlet.AbstractSecurityFilter;

public class SpringSecurityFilter extends AbstractSecurityFilter {

  protected SpringSecurityFilter(FilterChainResolver resolver) {
    super();
    if (resolver != null) {
      setFilterChainResolver(resolver);
    }
  }
}
