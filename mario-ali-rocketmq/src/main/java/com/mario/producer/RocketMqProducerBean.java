package com.mario.producer;

import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.SendCallback;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.mario.common.threadlocal.SerialNo;
import com.mario.common.util.ExceptionUtil;
import com.mario.common.util.SerializerUtil;
import com.mario.model.RockMqMessage;
import com.mario.producer.api.RocketMqProducer;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;

/**
 * Description: 无序消息生产者接口
 *
 * @see Producer
 */
@Slf4j
public class RocketMqProducerBean implements RocketMqProducer {

  private Properties properties;

  private Producer producer;

  @Override
  public void start() {
    if (null == this.properties) {
      throw new ONSClientException("properties not set");
    }

    this.producer = ONSFactory.createProducer(this.properties);
    this.producer.start();
  }

  @Override
  public void updateCredential(Properties credentialProperties) {
    if (this.producer != null) {
      this.producer.updateCredential(credentialProperties);
    }
  }

  @Override
  public void shutdown() {
    if (this.producer != null) {
      this.producer.shutdown();
    }
  }

  /**
   * 同步发送消息，只要不抛异常就表示成功
   *
   * @param message
   * @return 发送结果，true 表示发送成功，否则发送失败
   * @see Message
   */
  @Override
  public boolean send(final RockMqMessage message) {
    boolean sendSuccess = true;
    long start = System.currentTimeMillis();
    try {
      producer.send(message);
    } catch (Throwable e) {
      sendSuccess = false;
      String objectStr = null;
      try {
        Object object = SerializerUtil.unserialize(message.getBody());
        //objectStr = JacksonUtil.toJson(object);
        objectStr = object != null ? object.toString() : "null";
      } catch (Exception ex) {
        objectStr = message.toString();
      }
      Object[] params = {SerialNo.getSerialNo(), message.getTopic(), message.getTag(),
          message.getKey(), objectStr, (System.currentTimeMillis() - start),
          ExceptionUtil.getAsString(e)};
      log.error(
          "[{}] Mq send Exception, topic:[{}], tag:[{}], key:[{}], message:[{}], costTime:{}ms, Some Exception Occur:[{}]",
          params);
    }
    return sendSuccess;
  }

  /**
   * 发送消息，Oneway形式，服务器不应答，无法保证消息是否成功到达服务器
   *
   * @param message
   * @return 发送结果，true 表示发送成功，否则发送失败
   * @see Message
   */
  @Override
  public boolean sendOneway(final RockMqMessage message) {
    boolean sendSuccess = true;
    long start = System.currentTimeMillis();
    try {
      producer.sendOneway(message);
    } catch (Throwable e) {
      sendSuccess = false;
      String objectStr = null;
      try {
        Object object = SerializerUtil.unserialize(message.getBody());
        //objectStr = JacksonUtil.toJson(object);
        objectStr = object != null ? object.toString() : "null";
      } catch (Exception ex) {
        objectStr = message.toString();
      }
      Object[] params = {SerialNo.getSerialNo(), message.getTopic(), message.getTag(),
          message.getKey(), objectStr, (System.currentTimeMillis() - start),
          ExceptionUtil.getAsString(e)};
      log.error(
          "[{}] Mq send Exception(Oneway), topic:[{}], tag:[{}], key:[{}], message:[{}], costTime:{}ms, Some Exception Occur:[{}]",
          params);
    }
    return sendSuccess;
  }

  /**
   * 发送消息，异步Callback形式
   *
   * @param message
   * @return 发送结果，true 表示发送成功，否则发送失败
   * @see Message
   */
  @Override
  public boolean sendAsync(final RockMqMessage message, final SendCallback sendCallback) {
    boolean sendSuccess = true;
    long start = System.currentTimeMillis();
    try {
      producer.sendAsync(message, sendCallback);
    } catch (Throwable e) {
      sendSuccess = false;
      String objectStr = null;
      try {
        Object object = SerializerUtil.unserialize(message.getBody());
        //objectStr = JacksonUtil.toJson(object);
        objectStr = object != null ? object.toString() : "null";
      } catch (Exception ex) {
        objectStr = message.toString();
      }
      Object[] params = {SerialNo.getSerialNo(), message.getTopic(), message.getTag(),
          message.getKey(), objectStr, (System.currentTimeMillis() - start),
          ExceptionUtil.getAsString(e)};
      log.error(
          "[{}] Mq send Exception(Async), topic:[{}], tag:[{}], key:[{}], message:[{}], costTime:{}ms, Some Exception Occur:[{}]",
          params);
    }
    return sendSuccess;
  }

  public Properties getProperties() {
    return properties;
  }


  public void setProperties(Properties properties) {
    this.properties = properties;
  }

  @Override
  public boolean isStarted() {
    return this.producer.isStarted();
  }

  @Override
  public boolean isClosed() {
    return this.producer.isClosed();
  }
}
