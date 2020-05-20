package com.mario;

import com.mario.ali.mq.producer.RocketMqTransactionProducerBean;
import com.mario.ali.mq.producer.api.RocketMqTransactionMessageChecker;
import com.mario.common.util.SpringBootPropertyUtil;
import com.mario.common.util.StringUtil;
import java.util.Map;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.ManagedProperties;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.env.Environment;

@Slf4j
public class RegUtil {

  public static void regRocketMqProducerBeans(BeanDefinitionRegistry registry,
      String producerPrefix, Class<?> mqProducerBanClass, Environment environment) {
    Map<String, Map<String, String>> producers = null;
    try {
      producers = SpringBootPropertyUtil.handle(environment, producerPrefix, Map.class);
    } catch (Exception e) {
    }

    if (producers == null || producers.isEmpty()) {
      //TODO LOG
      log.debug("No {} found.", mqProducerBanClass.getName());
      return;
    }
    for (Map.Entry<String, Map<String, String>> entry : producers.entrySet()) {
      String producerBeanNames = entry.getKey();
      Map<String, String> map = entry.getValue();
      if (map == null || map.isEmpty()) {
        throw new IllegalArgumentException(
            "MqProducer[" + producerPrefix + producerBeanNames + "] properties must not empty!");
      }
      Properties properties = new ManagedProperties();
      String transactionMessageCheckerBeanName = null;
      for (Map.Entry<String, String> prop : map.entrySet()) {
        if (mqProducerBanClass == RocketMqTransactionProducerBean.class) {
          if ("transactionMessageCheckerBeanName".equals(prop.getKey())) {
            transactionMessageCheckerBeanName = prop.getValue();
            continue;
          }
        }

        properties.setProperty(prop.getKey(), prop.getValue());

      }
      if (mqProducerBanClass == RocketMqTransactionProducerBean.class && StringUtil
          .isBlank(transactionMessageCheckerBeanName)) {
        throw new IllegalArgumentException("MqProducer[" + producerPrefix + producerBeanNames
            + ".transactionMessageCheckerBeanName] properties must not empty! @see "
            + RocketMqTransactionMessageChecker.class);
      }
      registerRocketMqProducerBeans(mqProducerBanClass, producerBeanNames, registry, properties,
          transactionMessageCheckerBeanName);
    }
  }

  private static void registerRocketMqProducerBeans(Class<?> mqProducerBanClass, String beanName,
      BeanDefinitionRegistry registry, Properties properties,
      String transactionMessageCheckerBeanName) {
    RootBeanDefinition beanDefinition = new RootBeanDefinition();
    beanDefinition.setBeanClass(mqProducerBanClass);
//                beanDefinition.setLazyInit(true);
    beanDefinition.getPropertyValues().addPropertyValue("properties", properties);
    if (StringUtil.isNotBlank(transactionMessageCheckerBeanName)) {
      beanDefinition.getPropertyValues().addPropertyValue("rocketMqTransactionMessageChecker",
          new RuntimeBeanReference(transactionMessageCheckerBeanName));
    }
//                beanDefinition.getPropertyValues().addPropertyValue("properties", createMqConsumerProDefinition(StringUtil.propertyNameToAttributeName(beanName) + ".properties"));
    beanDefinition.setInitMethodName("start");
    beanDefinition.setDestroyMethodName("shutdown");

    registry.registerBeanDefinition(beanName, beanDefinition);
  }


}
