package com.mario.ali.mq.producer;

import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.SendCallback;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.mario.ali.mq.model.RockMqMessage;
import com.mario.ali.mq.model.RockMqSendResult;
import com.mario.ali.mq.producer.api.RocketMqProducer;
import com.mario.ali.mq.serializer.MqSerializer;
import com.mario.common.threadlocal.SerialNo;
import com.mario.common.util.ExceptionUtil;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RocketMqProducerBean extends RocketMqProducerAbstract implements RocketMqProducer {

  private static final Logger log = LoggerFactory.getLogger(RocketMqProducerBean.class);
  private Properties properties;
  private Producer producer;

  public RocketMqProducerBean() {
  }

  public void start() {
    if (null == this.properties) {
      throw new ONSClientException("properties not set");
    } else {
      super.init(this.properties);
      this.producer = ONSFactory.createProducer(this.properties);
      this.producer.start();
    }
  }

  public void updateCredential(Properties credentialProperties) {
    if (this.producer != null) {
      this.producer.updateCredential(credentialProperties);
    }

  }

  public void shutdown() {
    if (this.producer != null) {
      this.producer.shutdown();
    }

  }

  public <T> boolean send(RockMqMessage<T> message, MqSerializer<T> serializer) {
    return this.sendBackResult(message, serializer).isSuccess();
  }

  public <T> RockMqSendResult sendBackResult(RockMqMessage<T> message, MqSerializer<T> serializer) {
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

        sendResult = this.producer.send(message);
      } catch (Throwable var8) {
        Object[] params = new Object[]{SerialNo.getSerialNo(), message.getTopic(), message.getTag(),
            message.getKey(), message.getContent(), System.currentTimeMillis() - start,
            ExceptionUtil.getAsString(var8)};
        log.error(
            "[{}] Mq send Exception, topic:[{}], tag:[{}], key:[{}], message:[{}], costTime:{}ms, Some Exception Occur:[{}]",
            params);
      }

      return RockMqSendResult.successIfNotNull(sendResult);
    }
  }

  public <T> boolean sendOneway(RockMqMessage<T> message, MqSerializer<T> serializer) {
    if (message.getContent() == null) {
      Object[] params = new Object[]{SerialNo.getSerialNo(), message.getTopic(), message.getTag(),
          message.getKey()};
      log.error(
          "[{}] Mq sendOneway Failure, Because Message content is null , topic:[{}], tag:[{}], key:[{}]",
          params);
      return false;
    } else {
      boolean sendSuccess = true;
      long start = System.currentTimeMillis();

      try {
        byte[] bytes = serializer.serialize(message.getContent());
        message.setBody(bytes);
        if (!this.checkBeforeSendMsg(message)) {
          return false;
        }

        this.producer.sendOneway(message);
      } catch (Throwable var8) {
        sendSuccess = false;
        Object[] params = new Object[]{SerialNo.getSerialNo(), message.getTopic(), message.getTag(),
            message.getKey(), message.getContent(), System.currentTimeMillis() - start,
            ExceptionUtil.getAsString(var8)};
        log.error(
            "[{}] Mq send Exception(Oneway), topic:[{}], tag:[{}], key:[{}], message:[{}], costTime:{}ms, Some Exception Occur:[{}]",
            params);
      }

      return sendSuccess;
    }
  }

  public <T> boolean sendAsync(RockMqMessage<T> message, MqSerializer<T> serializer,
      SendCallback sendCallback) {
    if (message.getContent() == null) {
      Object[] params = new Object[]{SerialNo.getSerialNo(), message.getTopic(), message.getTag(),
          message.getKey()};
      log.error(
          "[{}] Mq sendAsync Failure, Because Message content is null , topic:[{}], tag:[{}], key:[{}]",
          params);
      return false;
    } else {
      boolean sendSuccess = true;
      long start = System.currentTimeMillis();

      try {
        byte[] bytes = serializer.serialize(message.getContent());
        message.setBody(bytes);
        if (!this.checkBeforeSendMsg(message)) {
          return false;
        }

        this.producer.sendAsync(message, sendCallback);
      } catch (Throwable var9) {
        sendSuccess = false;
        Object[] params = new Object[]{SerialNo.getSerialNo(), message.getTopic(), message.getTag(),
            message.getKey(), message.getContent(), System.currentTimeMillis() - start,
            ExceptionUtil.getAsString(var9)};
        log.error(
            "[{}] Mq send Exception(Async), topic:[{}], tag:[{}], key:[{}], message:[{}], costTime:{}ms, Some Exception Occur:[{}]",
            params);
      }

      return sendSuccess;
    }
  }

  public Properties getProperties() {
    return this.properties;
  }

  public void setProperties(Properties properties) {
    this.properties = properties;
  }

  public boolean isStarted() {
    return this.producer.isStarted();
  }

  public boolean isClosed() {
    return this.producer.isClosed();
  }
}
