package com.mario.security.mgt;

import com.mario.common.constants.CommonConstants;
import com.mario.common.util.CollectionUtil;
import com.mario.common.util.StringUtil;
import com.mario.security.filter.PathConfigProcessor;
import com.mario.security.util.Nameable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.naming.ConfigurationException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultFilterChainManager implements FilterChainManager {

  private static transient final Logger log = LoggerFactory
      .getLogger(DefaultFilterChainManager.class);

  private FilterConfig filterConfig;

  private Map<String, Filter> filters; //pool of filters available for creating chains

  private Map<String, NamedFilterList> filterChains; //key: chain name, value: chain

  public DefaultFilterChainManager() {
    this.filters = new LinkedHashMap<String, Filter>();
    this.filterChains = new LinkedHashMap<String, NamedFilterList>();
    addDefaultFilters(false);
  }

  public DefaultFilterChainManager(FilterConfig filterConfig) {
    this.filters = new LinkedHashMap<String, Filter>();
    this.filterChains = new LinkedHashMap<String, NamedFilterList>();
    setFilterConfig(filterConfig);
    addDefaultFilters(true);
  }

  public FilterConfig getFilterConfig() {
    return filterConfig;
  }

  public void setFilterConfig(FilterConfig filterConfig) {
    this.filterConfig = filterConfig;
  }

  @Override
  public Map<String, Filter> getFilters() {
    return filters;
  }

  @SuppressWarnings({"UnusedDeclaration"})
  public void setFilters(Map<String, Filter> filters) {
    this.filters = filters;
  }

  public Map<String, NamedFilterList> getFilterChains() {
    return filterChains;
  }

  @SuppressWarnings({"UnusedDeclaration"})
  public void setFilterChains(Map<String, NamedFilterList> filterChains) {
    this.filterChains = filterChains;
  }

  public Filter getFilter(String name) {
    return this.filters.get(name);
  }

  @Override
  public void addFilter(String name, Filter filter) {
    addFilter(name, filter, false);
  }

  @Override
  public void addFilter(String name, Filter filter, boolean init) {
    addFilter(name, filter, init, true);
  }

  @Override
  public void createChain(String chainName, String chainDefinition) {
    if (!StringUtil.hasText(chainName)) {
      throw new NullPointerException("chainName cannot be null or empty.");
    }
    if (!StringUtil.hasText(chainDefinition)) {
      throw new NullPointerException("chainDefinition cannot be null or empty.");
    }

    if (log.isDebugEnabled()) {
      log.debug(
          "Creating chain [" + chainName + "] from String definition [" + chainDefinition + "]");
    }

    String[] filterTokens = splitChainDefinition(chainDefinition);

    for (String token : filterTokens) {
      String[] nameConfigPair = new String[0];
      try {
        nameConfigPair = toNameConfigPair(token);
      } catch (ConfigurationException e) {
        e.printStackTrace();
      }

      //now we have the filter name, path and (possibly null) path-specific config.  Let's apply them:
      addToChain(chainName, nameConfigPair[0], nameConfigPair[1]);
    }
  }

  protected String[] splitChainDefinition(String chainDefinition) {
    return StringUtil
        .split(chainDefinition, CommonConstants.DEFAULT_DELIMITER_CHAR, '[', ']', true, true);
  }

  protected String[] toNameConfigPair(String token) throws ConfigurationException {

    try {
      String[] pair = token.split("\\[", 2);
      String name = StringUtil.clean(pair[0]);

      if (name == null) {
        throw new IllegalArgumentException(
            "Filter name not found for filter chain definition token: " + token);
      }
      String config = null;

      if (pair.length == 2) {
        config = StringUtil.clean(pair[1]);
        //if there was an open bracket, it assumed there is a closing bracket, so strip it too:
        config = config.substring(0, config.length() - 1);
        config = StringUtil.clean(config);

        if (config != null && config.startsWith("\"") && config.endsWith("\"")) {
          String stripped = config.substring(1, config.length() - 1);
          stripped = StringUtil.clean(stripped);

          if (stripped != null && stripped.indexOf('"') == -1) {
            config = stripped;
          }
        }
      }

      return new String[]{name, config};

    } catch (Exception e) {
      String msg = "Unable to parse filter chain definition token: " + token;
      throw new ConfigurationException(msg);
    }
  }

  protected void addFilter(String name, Filter filter, boolean init, boolean overwrite) {
    Filter existing = getFilter(name);
    if (existing == null || overwrite) {
      if (filter instanceof Nameable) {
        ((Nameable) filter).setName(name);
      }
      if (init) {
        initFilter(filter);
      }
      this.filters.put(name, filter);
    }
  }

  @Override
  public void addToChain(String chainName, String filterName) {
    addToChain(chainName, filterName, null);
  }

  @Override
  public void addToChain(String chainName, String filterName, String chainSpecificFilterConfig) {
    if (!StringUtil.hasText(chainName)) {
      throw new IllegalArgumentException("chainName cannot be null or empty.");
    }
    Filter filter = getFilter(filterName);
    if (filter == null) {
      throw new IllegalArgumentException("There is no filter with name '" + filterName +
          "' to apply to chain [" + chainName + "] in the pool of available Filters.  Ensure a " +
          "filter with that name/path has first been registered with the addFilter method(s).");
    }

    try {
      applyChainConfig(chainName, filter, chainSpecificFilterConfig);
    } catch (ConfigurationException e) {
      e.printStackTrace();
    }

    NamedFilterList chain = ensureChain(chainName);
    chain.add(filter);
  }

  protected void applyChainConfig(String chainName, Filter filter, String chainSpecificFilterConfig)
      throws ConfigurationException {
    if (log.isDebugEnabled()) {
      log.debug("Attempting to apply path [" + chainName + "] to filter [" + filter + "] " +
          "with config [" + chainSpecificFilterConfig + "]");
    }
    if (filter instanceof PathConfigProcessor) {
      ((PathConfigProcessor) filter).processPathConfig(chainName, chainSpecificFilterConfig);
    } else {
      if (StringUtil.hasText(chainSpecificFilterConfig)) {
        //they specified a filter configuration, but the Filter doesn't implement PathConfigProcessor
        //this is an erroneous config:
        String msg = "chainSpecificFilterConfig was specified, but the underlying " +
            "Filter instance is not an 'instanceof' " +
            PathConfigProcessor.class.getName() + ".  This is required if the filter is to accept "
            +
            "chain-specific configuration.";
        throw new ConfigurationException(msg);
      }
    }
  }

  protected NamedFilterList ensureChain(String chainName) {
    NamedFilterList chain = getChain(chainName);
    if (chain == null) {
      chain = new SimpleNamedFilterList(chainName);
      this.filterChains.put(chainName, chain);
    }
    return chain;
  }

  @Override
  public NamedFilterList getChain(String chainName) {
    return this.filterChains.get(chainName);
  }

  @Override
  public boolean hasChains() {
    return !CollectionUtil.isEmpty(this.filterChains);
  }

  @Override
  public Set<String> getChainNames() {
    //noinspection unchecked
    return this.filterChains != null ? this.filterChains.keySet() : Collections.EMPTY_SET;
  }

  @Override
  public FilterChain proxy(FilterChain original, String chainName) {
    NamedFilterList configured = getChain(chainName);
    if (configured == null) {
      String msg = "There is no configured chain under the name/key [" + chainName + "].";
      throw new IllegalArgumentException(msg);
    }
    return configured.proxy(original);
  }

  protected void initFilter(Filter filter) {
    FilterConfig filterConfig = getFilterConfig();
    if (filterConfig == null) {
      throw new IllegalStateException(
          "FilterConfig attribute has not been set.  This must occur before filter " +
              "initialization can occur.");
    }
    try {
      filter.init(filterConfig);
    } catch (ServletException e) {
//            throw new ConfigurationException(e.getMessage());
    }
  }

  protected void addDefaultFilters(boolean init) {
    for (DefaultFilter defaultFilter : DefaultFilter.values()) {
      addFilter(defaultFilter.name(), defaultFilter.newInstance(), init, false);
    }
  }
}
