package com.mario.producer.order;

import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.aliyun.openservices.ons.api.order.OrderProducer;
import com.mario.common.threadlocal.SerialNo;
import com.mario.common.util.ExceptionUtil;
import com.mario.common.util.SerializerUtil;
import com.mario.model.order.RockMqOrderMessage;
import com.mario.producer.order.api.RocketMqOrderProducer;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;

/**
 * Description: 有序消息生产者接口
 *
 * @seeurl https://help.aliyun.com/document_detail/49319.html?spm=5176.doc43349.6.561.zH2zYW
 * @see OrderProducer 全局顺序：对于指定的一个 Topic，所有消息按照严格的先入先出（FIFO）的顺序进行发布和消费。 MQ 全局顺序消息适用于以下场景：
 * 性能要求不高，所有的消息严格按照 FIFO 原则进行消息发布和消费的场景。 MQ 分区顺序消息适用于如下场景： 性能要求高，以 sharding key 作为分区字段，在同一个区块中严格的按照
 * FIFO 原则进行消息发布和消费的场景。 举例说明： 【例一】 用户注册需要发送发验证码，以用户 ID 作为 sharding key， 那么同一个用户发送的消息都会按照先后顺序来发布和订阅。
 * 【例二】 电商的订单创建，以订单 ID 作为 sharding key，那么同一个订单相关的创建订单消息、订单支付消息、订单退款消息、订单物流消息都会按照先后顺序来发布和订阅。
 * 阿里巴巴集团内部电商系统均使用此种分区顺序消息，既保证业务的顺序，同时又能保证业务的高性能。
 * <p>
 * <p>
 * 消息类型对比
 * <p>
 * Topic类型	支持事务消息	支持定时消息	性能 无序消息	       是	       是	   最高 分区顺序	       否	       否	   高 全局顺序	       否
 * 否	   一般
 * <p>
 * 发送方式对比
 * <p>
 * 消息类型	支持可靠同步发送	支持可靠异步发送	支持 oneway 发送 无序消息	是	           是	            是 分区顺序	是	           否
 * 否 全局顺序	是	           否	            否
 */
@Slf4j
public class RocketMqOrderProducerBean implements RocketMqOrderProducer {

  private Properties properties;

  private OrderProducer orderProducer;

  @Override
  public void start() {
    if (null == this.properties) {
      throw new ONSClientException("properties not set");
    }

    this.orderProducer = ONSFactory.createOrderProducer(this.properties);
    this.orderProducer.start();
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

  /**
   * 同步发送消息，只要不抛异常就表示成功
   *
   * @param message
   * @return 发送结果，true 表示发送成功，否则发送失败
   */
  @Override
  public boolean send(final RockMqOrderMessage message) {
    boolean sendSuccess = true;
    long start = System.currentTimeMillis();
    try {
      orderProducer.send(message, message.getShardingKey());
    } catch (Throwable e) {
      sendSuccess = false;
      String objectStr = null;
      try {
        Object object = SerializerUtil.unserialize(message.getBody());
//                objectStr = JacksonUtil.toJson(object);
        objectStr = object != null ? object.toString() : "null";
      } catch (Exception ex) {
        objectStr = message.toString();
      }
      Object[] params = {SerialNo.getSerialNo(), message.getTopic(), message.getTag(),
          message.getKey(), objectStr, message.getShardingKey(),
          (System.currentTimeMillis() - start), ExceptionUtil.getAsString(e)};
      log.error(
          "[{}] Mq send order message Exception, topic:[{}], tag:[{}], key:[{}], message:[{}], shardingKey[{}], costTime:{}ms, Some Exception Occur:[{}]",
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
    return this.orderProducer.isStarted();
  }

  @Override
  public boolean isClosed() {
    return this.orderProducer.isClosed();
  }
}
