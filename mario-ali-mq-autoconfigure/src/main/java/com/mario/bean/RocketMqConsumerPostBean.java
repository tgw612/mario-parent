package com.mario.bean;

import com.mario.MqEnvProperties;
import com.mario.ali.mq.consumer.RocketMqConsumerBean;
import java.util.Properties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 注意 （RocketMqMessageListener） bean的名字 不能与 消费组(mq.consumers.fileExportConsumer)配置的名字一样
 */
@Slf4j
@Data
@EnableConfigurationProperties(MqEnvProperties.class)
public class RocketMqConsumerPostBean extends RocketMqConsumerBean implements InitializingBean {

  private String beanName;

  private final MqEnvProperties mqEnvProperties;

  public RocketMqConsumerPostBean(MqEnvProperties mqEnvProperties) {
    this.mqEnvProperties = mqEnvProperties;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (mqEnvProperties != null) {
      Properties properties = mqEnvProperties.getConsumers().get(getBeanName());
      if (properties == null || properties.isEmpty()) {
        throw new IllegalArgumentException("###########  Can not found Consumer[" + beanName
            + "] config from properties. see MqEnvProperties.class");
      } else {
        log.info("###########  found Consumer[" + beanName
            + "] config from properties. start to setProperties");
      }
      this.setProperties(properties);
    }
  }

    /*@Override
    public void destroy() throws Exception {
        shutdown();
    }*/
}
