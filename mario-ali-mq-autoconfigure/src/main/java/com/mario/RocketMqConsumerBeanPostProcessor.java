package com.mario;

import static org.springframework.context.annotation.AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR;

import com.mario.bean.RocketMqBatchConsumerPostBean;
import com.mario.bean.RocketMqConsumerPostBean;
import com.mario.bean.RocketMqOrderConsumerPostBean;
import com.mario.common.util.CollectionUtil;
import com.mario.common.util.StringUtil;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Slf4j
public class RocketMqConsumerBeanPostProcessor implements BeanDefinitionRegistryPostProcessor,
    EnvironmentAware {

  private final Set<String> packagesToScan;

  private Environment environment;

  /**
   * @see MqOrderConsumer
   * @see MqConsumer
   * @see MqBatchConsumer
   */
  private Class<? extends Annotation> annotationClass;

  public RocketMqConsumerBeanPostProcessor(Class<? extends Annotation> annotationClass,
      String... packagesToScan) {
    this(annotationClass, Arrays.asList(packagesToScan));

  }

  public RocketMqConsumerBeanPostProcessor(Class<? extends Annotation> annotationClass,
      Collection<String> packagesToScan) {
    this(annotationClass, new LinkedHashSet<String>(packagesToScan));
  }

  public RocketMqConsumerBeanPostProcessor(Class<? extends Annotation> annotationClass,
      Set<String> packagesToScan) {
    this.packagesToScan = packagesToScan;
    this.annotationClass = annotationClass;
  }

  @Override
  public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
      throws BeansException {

    Set<String> resolvedPackagesToScan = resolvePackagesToScan(packagesToScan);

    if (!CollectionUtils.isEmpty(resolvedPackagesToScan)) {
      registerServiceBeans(resolvedPackagesToScan, registry);
    } else {
      if (log.isWarnEnabled()) {
        log.warn("packagesToScan is empty , MqConsumer Bean registry will be ignored!");
      }
    }

  }

  /**
   * @param packagesToScan The base packages to scan
   * @param registry       {@link BeanDefinitionRegistry}
   */
  private void registerServiceBeans(Set<String> packagesToScan, BeanDefinitionRegistry registry) {
    if (annotationClass == null) {
      throw new IllegalArgumentException("annotationClass must not be null");
    }

    Class<?> consumerBeanClazz = null;
    if (annotationClass.isAssignableFrom(MqConsumer.class)) {
      consumerBeanClazz = RocketMqConsumerPostBean.class;
    } else if (annotationClass.isAssignableFrom(MqOrderConsumer.class)) {
      consumerBeanClazz = RocketMqOrderConsumerPostBean.class;
    } else if (annotationClass.isAssignableFrom(MqBatchConsumer.class)) {
      consumerBeanClazz = RocketMqBatchConsumerPostBean.class;
    } else {
      throw new IllegalArgumentException(
          "annotationClass must be isAssignableFrom MqConsumer.class or MqOrderConsumer.class or MqBatchConsumer.class");
    }

    if (log.isDebugEnabled()) {
      log.debug("Searching for Mq order Consumer with @" + StringUtil
          .capitalize(annotationClass.getSimpleName()));
    }

    ClassPathConsumerScanner scanner =
        new ClassPathConsumerScanner(registry);

    BeanNameGenerator beanNameGenerator = resolveBeanNameGenerator(registry);

    scanner.setBeanNameGenerator(beanNameGenerator);

    /**
     * @see MqOrderConsumer
     * @see MqConsumer
     * @see MqBatchConsumer
     */
    scanner.setAnnotationClass(annotationClass);
    scanner.registerFilters();
    Map<String, List<RuntimeBeanReference>> pathBeanMap = scanner
        .doScanAndGet(StringUtils.toStringArray(packagesToScan));
    if (CollectionUtil.isNotEmpty(pathBeanMap)) {
      registerRocketMqConsumerBeans(pathBeanMap, registry, consumerBeanClazz);
    }
  }

  private void registerRocketMqConsumerBeans(Map<String, List<RuntimeBeanReference>> groupBeanMap,
      BeanDefinitionRegistry registry, Class<?> consumerBeanClazz) {
    for (String beanName : groupBeanMap.keySet()) {
      List<RuntimeBeanReference> subscriptionList = groupBeanMap.get(beanName);
      RootBeanDefinition beanDefinition = new RootBeanDefinition();
      beanDefinition.setBeanClass(consumerBeanClazz);
//                beanDefinition.setLazyInit(true);
      beanDefinition.getPropertyValues().addPropertyValue(new PropertyValue("beanName", beanName));
      beanDefinition.getPropertyValues().addPropertyValue("subscriptionList", subscriptionList);
//                beanDefinition.getPropertyValues().addPropertyValue("properties", createMqConsumerProDefinition(StringUtil.propertyNameToAttributeName(beanName) + ".properties"));
      beanDefinition.setInitMethodName("start");
      beanDefinition.setDestroyMethodName("shutdown");

      registry.registerBeanDefinition(beanName, beanDefinition);
    }
  }

  private RootBeanDefinition createMqConsumerProDefinition(String consumerProperties) {
    RootBeanDefinition beanDefinition = new RootBeanDefinition();
    beanDefinition.setBeanClass(PropertiesFactoryBean.class);
    beanDefinition.getPropertyValues().addPropertyValue("locations", consumerProperties);
//            PropertiesFactoryBean beanReferenceMap = null;
//            Properties properties = PropertiesLoaderUtils.loadProperties(new EncodedResource(new ClassPathResource(consumerProperties)));
    return beanDefinition;

  }

  private BeanNameGenerator resolveBeanNameGenerator(BeanDefinitionRegistry registry) {

    BeanNameGenerator beanNameGenerator = null;

    if (registry instanceof SingletonBeanRegistry) {
      SingletonBeanRegistry singletonBeanRegistry = SingletonBeanRegistry.class.cast(registry);
      beanNameGenerator = (BeanNameGenerator) singletonBeanRegistry
          .getSingleton(CONFIGURATION_BEAN_NAME_GENERATOR);
    }

    if (beanNameGenerator == null) {

      if (log.isInfoEnabled()) {

        log.info("BeanNameGenerator bean can't be found in BeanFactory with name ["
            + CONFIGURATION_BEAN_NAME_GENERATOR + "]");
        log.info("BeanNameGenerator will be a instance of " +
            AnnotationBeanNameGenerator.class.getName() +
            " , it maybe a potential problem on bean name generation.");
      }

      beanNameGenerator = new AnnotationBeanNameGenerator();

    }

    return beanNameGenerator;

  }

  private Set<String> resolvePackagesToScan(Set<String> packagesToScan) {
    Set<String> resolvedPackagesToScan = new LinkedHashSet<String>(packagesToScan.size());
    for (String packageToScan : packagesToScan) {
      if (StringUtils.hasText(packageToScan)) {
        String resolvedPackageToScan = environment.resolvePlaceholders(packageToScan.trim());
        resolvedPackagesToScan.add(resolvedPackageToScan);
      }
    }
    return resolvedPackagesToScan;
  }

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
      throws BeansException {
  }

  @Override
  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }

  public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
    this.annotationClass = annotationClass;
  }
}
