package com.mario.scanner.producer;

import com.mario.MqEnvProperties;
import com.mario.RegUtil;
import com.mario.ali.mq.producer.order.RocketMqOrderProducerBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 顺序生产者s
 *
 * @author qiujingwang
 * @version 1.0
 * @date 2019/01/02 下午11:13
 * @Description: TODO
 */
@Slf4j
public class MqOrderProducerBeanScannerRegistrar
    implements BeanFactoryAware, ImportBeanDefinitionRegistrar, ResourceLoaderAware,
    EnvironmentAware {

  private BeanFactory beanFactory;

  private ResourceLoader resourceLoader;

  private Environment environment;

  @Override
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
      BeanDefinitionRegistry registry) {

    log.debug("Searching for Mq OrderProducer from Environment");

    String producerPrefix = MqEnvProperties.MQ_ORDER_PRODUCERS_PREFIX;
    Class<?> beanClass = RocketMqOrderProducerBean.class;
    RegUtil.regRocketMqProducerBeans(registry, producerPrefix, beanClass, environment);
  }

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    this.beanFactory = beanFactory;
  }

  @Override
  public void setResourceLoader(ResourceLoader resourceLoader) {
    this.resourceLoader = resourceLoader;
  }

  @Override
  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }
}