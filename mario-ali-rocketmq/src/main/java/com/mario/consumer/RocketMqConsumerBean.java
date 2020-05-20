package com.mario.consumer;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.mario.common.enums.AppName;
import com.mario.common.threadlocal.SerialNo;
import com.mario.common.util.ExceptionUtil;
import com.mario.common.util.SerializerUtil;
import com.mario.consumer.api.RocketMqConsumer;
import com.mario.consumer.api.RocketMqMessageListener;
import com.mario.model.MessageContext;
import com.mario.model.MqBaseMessageBody;
import com.mario.topic.SubscibeTopic;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;

/**
 * Description: 无序消息消费者，用来订阅消息
 *
 * @see com.aliyun.openservices.ons.api.bean.ConsumerBean
 */
@Slf4j
public class RocketMqConsumerBean implements RocketMqConsumer {

  private Properties properties;
//    private Map<SubscibeTopic, RocketMqMessageListener> subscriptionTable;

  private Map<String, Map<String, RocketMqMessageListener>> topicTagListenerMap;//key: topic value: tag > listener
  private Consumer consumer;

  @Override
  public void start() {
    if (null == this.properties) {
      throw new ONSClientException("properties not set");
    }

    if (null == this.topicTagListenerMap) {
      throw new ONSClientException("subscriptionTable not set");
    }

    this.consumer = ONSFactory.createConsumer(this.properties);

    Iterator<Map.Entry<String, Map<String, RocketMqMessageListener>>> it = this.topicTagListenerMap
        .entrySet().iterator();

    while (it.hasNext()) {
      Map.Entry<String, Map<String, RocketMqMessageListener>> next = it.next();
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

      Map<String, RocketMqMessageListener> tagListenerMap = next.getValue();
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
        Iterator<Map.Entry<String, RocketMqMessageListener>> tagListenerIterator = tagListenerMap
            .entrySet().iterator();
        StringBuilder tagBuilder = new StringBuilder();
        while (tagListenerIterator.hasNext()) {
          if (tagBuilder.length() > 0) {
            tagBuilder.append("||");
          }
          tagBuilder.append(tagListenerIterator.next().getKey());
        }
        this.subscribe(next.getKey(), tagBuilder.toString(), (record, messageContext) -> {
          RocketMqMessageListener mqMessageListener = tagListenerMap.get(messageContext.getTag());
          if (mqMessageListener == null) {
            Object[] params = new Object[]{SerialNo.getSerialNo(), messageContext.getMsgID(),
                messageContext.getTopic(), messageContext.getTag(), messageContext.getKey(),
                properties.get(PropertyKeyConst.ConsumerId), record};
            log.error(
                "[{}]Mq consume messageListener has not found, msgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}], record is null, message:[{}], Exception:{}",
                params);
            return Action.ReconsumeLater;
          }
          return mqMessageListener.call(record, messageContext);
        });
      }
    }

    this.consumer.start();
  }

  @Override
  public void updateCredential(Properties credentialProperties) {
    if (this.consumer != null) {
      this.consumer.updateCredential(credentialProperties);
    }
  }

  @Override
  public void shutdown() {
    if (this.consumer != null) {
      this.consumer.shutdown();
    }
  }

  @Override
  public void subscribe(String topic, String subExpression, RocketMqMessageListener listener) {
    if (null == this.consumer) {
      throw new ONSClientException("Subscribe must be called after consumerBean started");
    }
    this.consumer.subscribe(topic, subExpression, new MessageListener() {
      @Override
      public Action consume(Message message, ConsumeContext context) {
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
            return Action.CommitMessage;
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
              new MessageContext(message.getTopic(), message.getUserProperties(), message.getTag(),
                  message.getMsgID(), message.getKey(), message.getReconsumeTimes()));
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
                    只要抛出异常就会进入重试
                    重试时间与次数：https://help.aliyun.com/knowledge_detail/39127.html
                    消费业务逻辑代码如果返回Action.ReconsumerLater，或者NULL，或者抛出异常，消息都会走重试流程，至多重试16次，如果重试16次后，仍然失败，则消息丢弃。
                    */
          return Action.ReconsumeLater;
        } finally {
          //清空线程上下文日志ID
          SerialNo.clear();
        }
                /*//不管有没有异常都返回消费成功
                return Action.CommitMessage;*/
      }
    });
  }

  @Override
  public void unsubscribe(String topic) {
    if (null == this.consumer) {
      throw new ONSClientException("unsubscribe must be called after consumerBean started");
    }
    this.consumer.unsubscribe(topic);
  }

  public Properties getProperties() {
    return properties;
  }

  public void setProperties(Properties properties) {
    this.properties = properties;
  }

  public void setSubscriptionTable(Map<SubscibeTopic, RocketMqMessageListener> subscriptionTable) {
    if (subscriptionTable != null) {
      topicTagListenerMap = initTopicTagListenerMap(subscriptionTable);
    }
  }

  @Override
  public boolean isStarted() {
    return this.consumer.isStarted();
  }

  @Override
  public boolean isClosed() {
    return this.consumer.isClosed();
  }

  private Map<String, Map<String, RocketMqMessageListener>> initTopicTagListenerMap(
      Map<SubscibeTopic, RocketMqMessageListener> subscriptionTable) {
    Map<String, Map<String, RocketMqMessageListener>> topicTagListenerMap = new HashMap<>(
        subscriptionTable.size());
    Iterator<Map.Entry<SubscibeTopic, RocketMqMessageListener>> it = subscriptionTable.entrySet()
        .iterator();
    while (it.hasNext()) {
      Map.Entry<SubscibeTopic, RocketMqMessageListener> next = it.next();
      Map<String, RocketMqMessageListener> tagListenerMap = topicTagListenerMap
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
