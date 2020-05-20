package com.mario.security.web;

import com.mario.common.util.CollectionUtil;
import com.mario.security.config.Ini;
import com.mario.security.mgt.DefaultFilterChainManager;
import com.mario.security.mgt.FilterChainManager;
import com.mario.security.mgt.PathMatchingFilterChainResolver;
import com.mario.security.servlet.AbstractSecurityFilter;
import com.mario.security.util.Nameable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class SecurityFilterFactoryBean implements FactoryBean, BeanPostProcessor {

  private static transient final Logger log = LoggerFactory
      .getLogger(SecurityFilterFactoryBean.class);

  private Map<String, Filter> filters;

  private Map<String, String> filterChainDefinitionMap; //urlPathExpression_to_comma-delimited-filter-chain-definition

  private AbstractSecurityFilter instance;

  /**
   * AbstractSecurityFilter
   */
  private Class securityFilterClass;

  /**
   * 不拦截Url列表
   */
  private Set<String> excludesPattern;

  public static final String URLS = "urls";

  public SecurityFilterFactoryBean() {
    this.filters = new LinkedHashMap<String, Filter>();
    this.filterChainDefinitionMap = new LinkedHashMap<String, String>(); //order matters!
  }

  public SecurityFilterFactoryBean(AbstractSecurityFilter instance) {
    this.filters = new LinkedHashMap<String, Filter>();
    this.filterChainDefinitionMap = new LinkedHashMap<String, String>(); //order matters!
  }

  public Map<String, Filter> getFilters() {
    return filters;
  }

  public void setFilters(Map<String, Filter> filters) {
    this.filters = filters;
  }

  public Map<String, String> getFilterChainDefinitionMap() {
    return filterChainDefinitionMap;
  }

  public void setFilterChainDefinitionMap(Map<String, String> filterChainDefinitionMap) {
    this.filterChainDefinitionMap = filterChainDefinitionMap;
  }

  public void setFilterChainDefinitions(String definitions) {
    Ini ini = new Ini();
    ini.load(definitions);
    //did they explicitly state a 'urls' section?  Not necessary, but just in case:
    Ini.Section section = ini.getSection(URLS);
    if (CollectionUtil.isEmpty(section)) {
      //no urls section.  Since this _is_ a urls chain definition property, just assume the
      //default section contains only the definitions:
      section = ini.getSection(Ini.DEFAULT_SECTION_NAME);
    }
    setFilterChainDefinitionMap(section);
  }

  @Override
  public Object getObject() throws Exception {
    if (instance == null) {
      instance = createInstance();
    }
    return instance;
  }

  @Override
  public Class getObjectType() {
    return SpringSecurityFilter.class;
  }

  @Override
  public boolean isSingleton() {
    return true;
  }

  protected FilterChainManager createFilterChainManager() {

    DefaultFilterChainManager manager = new DefaultFilterChainManager();
    Map<String, Filter> defaultFilters = manager.getFilters();
    //apply global settings if necessary:
    for (Filter filter : defaultFilters.values()) {
      applyGlobalPropertiesIfNecessary(filter);
    }

    //Apply the acquired and/or configured filters:
    Map<String, Filter> filters = getFilters();
    if (!CollectionUtil.isEmpty(filters)) {
      for (Map.Entry<String, Filter> entry : filters.entrySet()) {
        String name = entry.getKey();
        Filter filter = entry.getValue();
        applyGlobalPropertiesIfNecessary(filter);
        if (filter instanceof Nameable) {
          ((Nameable) filter).setName(name);
        }
        //'init' argument is false, since Spring-configured filters should be initialized
        //in Spring (i.e. 'init-method=blah') or implement InitializingBean:
        manager.addFilter(name, filter, false);
      }
    }

    //build up the chains:
    Map<String, String> chains = getFilterChainDefinitionMap();
    if (!CollectionUtil.isEmpty(chains)) {
      for (Map.Entry<String, String> entry : chains.entrySet()) {
        String url = entry.getKey();
        String chainDefinition = entry.getValue();
        manager.createChain(url, chainDefinition);
      }
    }

    return manager;
  }

  protected void applyGlobalPropertiesIfNecessary(Filter filter) {

  }

  protected AbstractSecurityFilter createInstance() throws Exception {

    log.debug("Creating Security Filter instance.");

    FilterChainManager manager = createFilterChainManager();

    PathMatchingFilterChainResolver chainResolver = new PathMatchingFilterChainResolver();
    chainResolver.setFilterChainManager(manager);

    AbstractSecurityFilter securityFilter;
    if (securityFilterClass != null) {
      if (!AbstractSecurityFilter.class.isAssignableFrom(securityFilterClass)) {
        throw new ClassCastException("securityFilterClass[" + securityFilterClass.getName()
            + "] must extends AbstractSecurityFilter Class");
      }
      Object o = securityFilterClass.newInstance();
      securityFilter = AbstractSecurityFilter.class.cast(o);
      securityFilter.setFilterChainResolver(chainResolver);
    } else {
      securityFilter = new SpringSecurityFilter(chainResolver);
    }

    securityFilter.setExcludesPattern(excludesPattern);
    return securityFilter;
  }

  /**
   * Inspects a bean, and if it implements the {@link Filter} interface, automatically adds that
   * filter instance to the internal {@link #setFilters(Map) filters map} that will be referenced
   * later during filter chain construction.
   */
  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {
    if (bean instanceof Filter) {
      log.debug("Found filter chain candidate filter '{}'", beanName);
      Filter filter = (Filter) bean;
      getFilters().put(beanName, filter);
    } else {
      log.trace("Ignoring non-Filter bean '{}'", beanName);
    }
    return bean;
  }

  /**
   * Does nothing - only exists to satisfy the BeanPostProcessor interface and immediately returns
   * the {@code bean} argument.
   */
  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    return bean;
  }

  public void setSecurityFilterClass(Class securityFilterClass) {
    this.securityFilterClass = securityFilterClass;
  }

  public void setExcludesPattern(Set<String> excludesPattern) {
    this.excludesPattern = excludesPattern;
  }
}
