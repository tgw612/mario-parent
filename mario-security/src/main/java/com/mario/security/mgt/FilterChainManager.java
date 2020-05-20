package com.mario.security.mgt;

import java.util.Map;
import java.util.Set;
import javax.naming.ConfigurationException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;

public interface FilterChainManager {

  Map<String, Filter> getFilters();

  NamedFilterList getChain(String chainName);

  boolean hasChains();

  Set<String> getChainNames();

  FilterChain proxy(FilterChain original, String chainName);

  void addFilter(String name, Filter filter);

  void addFilter(String name, Filter filter, boolean init);

  void createChain(String chainName, String chainDefinition) throws ConfigurationException;

  void addToChain(String chainName, String filterName);

  void addToChain(String chainName, String filterName, String chainSpecificFilterConfig)
      throws ConfigurationException;
}
