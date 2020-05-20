package com.mario.consumer.order;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.aliyun.openservices.ons.api.order.ConsumeOrderContext;
import com.aliyun.openservices.ons.api.order.MessageOrderListener;
import com.aliyun.openservices.ons.api.order.OrderAction;
import com.aliyun.openservices.ons.api.order.OrderConsumer;
import com.mario.common.enums.AppName;
import com.mario.common.threadlocal.SerialNo;
import com.mario.common.util.ExceptionUtil;
import com.mario.common.util.SerializerUtil;
import com.mario.consumer.order.api.RocketMqOrderConsumer;
import com.mario.consumer.order.api.RocketMqOrderMessageListener;
import com.mario.model.MqBaseMessageBody;
import com.mario.model.order.OrderMessageContext;
import com.mario.topic.SubscibeTopic;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;

/**
 * Description: 有序消息消费者，用来订阅消息(顺序消费)
 *
 * @seeurl https://help.aliyun.com/document_detail/49319.html?spm=5176.doc43349.6.561.zH2zYW
 * @see OrderConsumer
 * <p>
 * <p>
 * 全局顺序：对于指定的一个 Topic，所有消息按照严格的先入先出（FIFO）的顺序进行发布和消费。 MQ 全局顺序消息适用于以下场景： 性能要求不高，所有的消息严格按照 FIFO
 * 原则进行消息发布和消费的场景。 MQ 分区顺序消息适用于如下场景： 性能要求高，以 sharding key 作为分区字段，在同一个区块中严格的按照 FIFO 原则进行消息发布和消费的场景。
 * 举例说明： 【例一】 用户注册需要发送发验证码，以用户 ID 作为 sharding key， 那么同一个用户发送的消息都会按照先后顺序来发布和订阅。 【例二】 电商的订单创建，以订单 ID
 * 作为 sharding key，那么同一个订单相关的创建订单消息、订单支付消息、订单退款消息、订单物流消息都会按照先后顺序来发布和订阅。
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
public class RocketMqOrderConsumerBean implements RocketMqOrderConsumer {

  private Properties properties;
//    private Map<SubscibeTopic, RocketMqOrderMessageListener> subscriptionTable;

  private Map<String, Map<String, RocketMqOrderMessageListener>> topicTagListenerMap;//key: topic value: tag > listener
  private OrderConsumer orderConsumer;

  @Override
  public void start() {
    if (null == this.properties) {
      throw new ONSClientException("properties not set");
    }

    if (null == this.topicTagListenerMap) {
      throw new ONSClientException("subscriptionTable not set");
    }

    this.orderConsumer = ONSFactory.createOrderedConsumer(this.properties);

    Iterator<Map.Entry<String, Map<String, RocketMqOrderMessageListener>>> it = this.topicTagListenerMap
        .entrySet().iterator();

    while (it.hasNext()) {
      Map.Entry<String, Map<String, RocketMqOrderMessageListener>> next = it.next();
            /*if (this.consumer.getClass().getCanonicalName()
                    .equals("com.aliyun.openservices.ons.api.impl.notify.ConsumerImpl")
                    && (next.getKey() instanceof SubscriptionExt)) {
                SubscriptionExt subscription = (SubscriptionExt) next.getKey();
                for (Method method : this.consumer.getClass().getMethods()) {
                    if ("subscribeNotify".equals(method.getName())) {
                        try {
                            method.invoke(consumer, subscription.getTopic(), subscription.getExpression(),
                                    subscription.isPersistence(), next.getValue());
                        } catch (Exception e) {
                            throw new ONSClientException("subscribeNotify invoke exception", e);
                        }
                        break;
                    }
                }

            } else {
                this.subscribe(next.getKey().getTopic(), next.getKey().getExpression(), next.getValue());
            }*/

      Map<String, RocketMqOrderMessageListener> tagListenerMap = next.getValue();
      if (tagListenerMap.size() <= 0) {
        throw new ONSClientException(
            "subscriptionTable[topic:" + next.getKey() + "] listener not set");
      }
      if (tagListenerMap.size() == 1) {
        //该主题只有一个tag，so
        tagListenerMap.keySet().forEach(tag -> {
          this.subscribe(next.getKey(), tag, tagListenerMap.get(tag));
          return;
        });
      } else {
        //多个tag
        Iterator<Map.Entry<String, RocketMqOrderMessageListener>> tagListenerIterator = tagListenerMap
            .entrySet().iterator();
        StringBuilder tagBuilder = new StringBuilder();
        while (tagListenerIterator.hasNext()) {
          if (tagBuilder.length() > 0) {
            tagBuilder.append("||");
          }
          tagBuilder.append(tagListenerIterator.next().getKey());
        }
        this.subscribe(next.getKey(), tagBuilder.toString(), (record, messageContext) -> {
          RocketMqOrderMessageListener mqMessageListener = tagListenerMap
              .get(messageContext.getTag());
          if (mqMessageListener == null) {
            Object[] params = new Object[]{SerialNo.getSerialNo(), messageContext.getMsgID(),
                messageContext.getTopic(), messageContext.getTag(), messageContext.getKey(),
                properties.get(PropertyKeyConst.ConsumerId), record};
            log.error(
                "[{}]Mq consume messageListener has not found, msgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}], record is null, message:[{}], Exception:{}",
                params);
            return OrderAction.Suspend;
          }
          return mqMessageListener.call(record, messageContext);
        });
      }
    }

    this.orderConsumer.start();
  }

  @Override
  public void updateCredential(Properties credentialProperties) {
    if (this.orderConsumer != null) {
      this.orderConsumer.updateCredential(credentialProperties);
    }
  }

  @Override
  public void shutdown() {
    if (this.orderConsumer != null) {
      this.orderConsumer.shutdown();
    }
  }

  @Override
  public void subscribe(String topic, String subExpression, RocketMqOrderMessageListener listener) {
    if (null == this.orderConsumer) {
      throw new ONSClientException("Subscribe must be called after orderConsumerBean started");
    }
    this.orderConsumer.subscribe(topic, subExpression, new MessageOrderListener() {
      @Override
      public OrderAction consume(Message message, ConsumeOrderContext context) {
        MqBaseMessageBody record = null;
        try {
          //初始化线程上下文日志ID
          SerialNo.init(AppName.DOUBO_MQ);

          //反序列化对象
          record = SerializerUtil.unserialize(message.getBody());
          if (record == null) {
            Object[] params = new Object[]{SerialNo.getSerialNo(), message.getMsgID(),
                message.getTopic(), message.getTag(), message.getKey(),
                properties.get(PropertyKeyConst.ConsumerId), message.getReconsumeTimes()};
            log.error(
                "[{}]Mq consume message Exception: record is null. msgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}], retryTimes:[{}]",
                params);
            return OrderAction.Success;
          } else {
            if (log.isDebugEnabled()) {
              Object[] params = new Object[]{SerialNo.getSerialNo(), message.getMsgID(),
                  message.getTopic(), message.getTag(), message.getKey(),
                  properties.get(PropertyKeyConst.ConsumerId), message.getReconsumeTimes(), record};
              log.debug(
                  "[{}] Mq Received message: msgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}], retryTimes:[{}], record:[{}]",
                  params);
            } else {
              Object[] params = new Object[]{SerialNo.getSerialNo(), message.getMsgID(),
                  message.getTopic(), message.getTag(), message.getKey(),
                  message.getReconsumeTimes()};
              log.info(
                  "[{}] Mq Received message: msgId:[{}], topic[{}], tag[{}], key[{}], retryTimes:[{}]",
                  params);
            }
          }

          //回调业务处理接口（默认返回消费成功）
          return listener.call(record,
              new OrderMessageContext(message.getTopic(), message.getUserProperties(),
                  message.getTag(), message.getMsgID(), message.getKey(),
                  message.getReconsumeTimes()));
        } catch (Throwable e) {
          //记录日志
          if (record != null) {
            Object[] params = new Object[]{SerialNo.getSerialNo(), message.getMsgID(),
                message.getTopic(), message.getTag(), message.getKey(),
                properties.get(PropertyKeyConst.ConsumerId), message.getReconsumeTimes(), record,
                ExceptionUtil.getAsString(e)};
            log.error(
                "[{}]Mq consume message Exception: msgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}], retryTimes:[{}], record:[{}], Exception:{}",
                params);
          } else {
            Object[] params = new Object[]{SerialNo.getSerialNo(), message.getMsgID(),
                message.getTopic(), message.getTag(), message.getKey(),
                properties.get(PropertyKeyConst.ConsumerId), message.getReconsumeTimes(), record,
                ExceptionUtil.getAsString(e)};
            log.error(
                "[{}]Mq consume message Exception: msgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}], retryTimes:[{}], record is null, message:[{}], Exception:{}",
                params);
          }
                    /*
                    消费失败，挂起当前队列
                    */
          return OrderAction.Suspend;
        } finally {
          //清空线程上下文日志ID
          SerialNo.clear();
        }
      }
    });
  }

  public Properties getProperties() {
    return properties;
  }

  public void setProperties(Properties properties) {
    this.properties = properties;
  }

  public void setSubscriptionTable(
      Map<SubscibeTopic, RocketMqOrderMessageListener> subscriptionTable) {
    if (subscriptionTable != null) {
      topicTagListenerMap = initTopicTagListenerMap(subscriptionTable);
    }
  }

  @Override
  public boolean isStarted() {
    return this.orderConsumer.isStarted();
  }

  @Override
  public boolean isClosed() {
    return this.orderConsumer.isClosed();
  }

  private Map<String, Map<String, RocketMqOrderMessageListener>> initTopicTagListenerMap(
      Map<SubscibeTopic, RocketMqOrderMessageListener> subscriptionTable) {
    Map<String, Map<String, RocketMqOrderMessageListener>> topicTagListenerMap = new HashMap<>(
        subscriptionTable.size());
    Iterator<Map.Entry<SubscibeTopic, RocketMqOrderMessageListener>> it = subscriptionTable
        .entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<SubscibeTopic, RocketMqOrderMessageListener> next = it.next();
      Map<String, RocketMqOrderMessageListener> tagListenerMap = topicTagListenerMap
          .get(next.getKey().getTopic());
      if (tagListenerMap == null) {
        tagListenerMap = new HashMap<>(5);
        topicTagListenerMap.put(next.getKey().getTopic(), tagListenerMap);
      }
      tagListenerMap.put(next.getKey().getExpression(), next.getValue());
    }
    return topicTagListenerMap;
  }
}
