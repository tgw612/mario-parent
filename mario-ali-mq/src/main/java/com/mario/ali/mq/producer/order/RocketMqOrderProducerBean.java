package com.mario.ali.mq.producer.order;

import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.aliyun.openservices.ons.api.order.OrderProducer;
import com.mario.ali.mq.model.RockMqSendResult;
import com.mario.ali.mq.model.order.RockMqOrderMessage;
import com.mario.ali.mq.producer.RocketMqProducerAbstract;
import com.mario.ali.mq.producer.order.api.RocketMqOrderProducer;
import com.mario.ali.mq.serializer.MqSerializer;
import com.mario.common.threadlocal.SerialNo;
import com.mario.common.util.ExceptionUtil;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RocketMqOrderProducerBean extends RocketMqProducerAbstract implements
    RocketMqOrderProducer {

  private static final Logger log = LoggerFactory.getLogger(RocketMqOrderProducerBean.class);
  private Properties properties;
  private OrderProducer orderProducer;

  public RocketMqOrderProducerBean() {
  }

  @Override
  public void start() {
    if (null == this.properties) {
      throw new ONSClientException("properties not set");
    } else {
      super.init(this.properties);
      this.orderProducer = ONSFactory.createOrderProducer(this.properties);
      this.orderProducer.start();
    }
  }

  @Override
  public void updateCredential(Properties credentialProperties) {
    if (this.orderProducer != null) {
      this.orderProducer.updateCredential(credentialProperties);
    }

  }

  @Override
  public void shutdown() {
    if (this.orderProducer != null) {
      this.orderProducer.shutdown();
    }

  }

  @Override
  public <T> boolean send(RockMqOrderMessage<T> message, MqSerializer<T> serializer) {
    return this.sendBackResult(message, serializer).isSuccess();
  }

  @Override
  public <T> RockMqSendResult sendBackResult(RockMqOrderMessage<T> message,
      MqSerializer<T> serializer) {
    if (message.getContent() == null) {
      Object[] params = new Object[]{SerialNo.getSerialNo(), message.getTopic(), message.getTag(),
          message.getKey()};
      log.error(
          "[{}] Mq send Failure, Because Message content is null , topic:[{}], tag:[{}], key:[{}]",
          params);
      return RockMqSendResult.fail();
    } else {
      SendResult sendResult = null;
      long start = System.currentTimeMillis();

      try {
        byte[] bytes = serializer.serialize(message.getContent());
        message.setBody(bytes);
        if (!this.checkBeforeSendMsg(message)) {
          return RockMqSendResult.fail();
        }

        sendResult = this.orderProducer.send(message, message.getShardingKey());
      } catch (Throwable var8) {
        Object[] params = new Object[]{SerialNo.getSerialNo(), message.getTopic(), message.getTag(),
            message.getKey(), message.getContent(), message.getShardingKey(),
            System.currentTimeMillis() - start, ExceptionUtil.getAsString(var8)};
        log.error(
            "[{}] Mq send order message Exception, topic:[{}], tag:[{}], key:[{}], message:[{}], shardingKey[{}], costTime:{}ms, Some Exception Occur:[{}]",
            params);
      }

      return RockMqSendResult.successIfNotNull(sendResult);
    }
  }

  public Properties getProperties() {
    return this.properties;
  }

  public void setProperties(Properties properties) {
    this.properties = properties;
  }

  @Override
  public boolean isStarted() {
    return this.orderProducer.isStarted();
  }

  @Override
  public boolean isClosed() {
    return this.orderProducer.isClosed();
  }
}