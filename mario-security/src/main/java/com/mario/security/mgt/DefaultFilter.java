package com.mario.security.mgt;

import com.mario.common.util.ClassUtil;
import com.mario.security.filter.authc.AnonymousFilter;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

public enum DefaultFilter {

  anon(AnonymousFilter.class),
//    authc(FormAuthenticationFilter.class),
  /*authcBasic(BasicHttpAuthenticationFilter.class),*/
//    logout(LogoutFilter.class),
    /*noSessionCreation(NoSessionCreationFilter.class),
    perms(PermissionsAuthorizationFilter.class),
    port(PortFilter.class),
    rest(HttpMethodPermissionFilter.class),
    roles(RolesAuthorizationFilter.class),
    ssl(SslFilter.class),
    user(UserFilter.class)*/;

  private final Class<? extends Filter> filterClass;

  private DefaultFilter(Class<? extends Filter> filterClass) {
    this.filterClass = filterClass;
  }

  public Filter newInstance() {
    return (Filter) ClassUtil.newInstance(this.filterClass);
  }

  public Class<? extends Filter> getFilterClass() {
    return this.filterClass;
  }

  public static Map<String, Filter> createInstanceMap(FilterConfig config) {
    Map<String, Filter> filters = new LinkedHashMap<String, Filter>(values().length);
    for (DefaultFilter defaultFilter : values()) {
      Filter filter = defaultFilter.newInstance();
      if (config != null) {
        try {
          filter.init(config);
        } catch (ServletException e) {
          String msg = "Unable to correctly init default filter instance of type " +
              filter.getClass().getName();
          throw new IllegalStateException(msg, e);
        }
      }
      filters.put(defaultFilter.name(), filter);
    }
    return filters;
  }
}
