package com.mario.ali.mq.consumer;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.mario.ali.mq.consumer.api.RocketMqMessageListener;
import com.mario.ali.mq.model.MessageContext;
import com.mario.ali.mq.model.MqBaseMessageBody;
import com.mario.ali.mq.topic.MqTopic;
import com.mario.ali.mq.topic.SubscibeTopic;
import com.mario.ali.mq.util.MqContextUtil;
import com.mario.ali.mq.util.MqUtil;
import com.mario.common.enums.AppName;
import com.mario.common.threadlocal.SerialNo;
import com.mario.common.util.ExceptionUtil;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class RocketMqConsumerBean implements RocketMqConsumer, ConsumerCheck {

  private static final Logger log = LoggerFactory.getLogger(RocketMqConsumerBean.class);
  private Properties properties;
  private Map<String, Map<String, RocketMqMessageListener>> topicTagListenerMap;
  private Consumer consumer;
  private String consumerId;

  public RocketMqConsumerBean() {
  }

  @Override
  public void start() {
    if (null == this.properties) {
      throw new ONSClientException("properties field not set");
    } else if (null == this.topicTagListenerMap) {
      throw new ONSClientException("topicTagListenerMap field not set");
    } else {
      this.consumerId = MqUtil.getConsumerId(this.properties);
      log.info("########## Start to create Consumer[consumerId={}]", this.consumerId);
      this.check(this.consumerId);
      this.consumer = ONSFactory.createConsumer(this.properties);
      this.topicTagListenerMap.forEach((key, value) -> {
        if (value.size() <= 0) {
          throw new ONSClientException("subscriptionTable[topic:" + key + "] listener not set");
        } else {
          if (value.size() == 1) {
            value.forEach((tagKey, rocketMqMessageListener) -> {
              if (rocketMqMessageListener == null) {
                throw new ONSClientException(
                    "subscriptionTable[topic:" + key + ", tag:" + tagKey + "] listener not set");
              } else {
                this.subscribe(key, tagKey, rocketMqMessageListener, (Map) null);
              }
            });
          } else {
            String moreTagStrs = (String) value.entrySet().stream().map((entry) -> {
              return (String) entry.getKey();
            }).collect(Collectors.joining("||", "", ""));
            this.subscribe(key, moreTagStrs, (RocketMqMessageListener) null, value);
          }

        }
      });
      this.consumer.start();
    }
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

  private void subscribe(String topic, String subExpression,
      final RocketMqMessageListener messageListener,
      final Map<String, RocketMqMessageListener> tagListenerMap) {
    if (null == this.consumer) {
      throw new ONSClientException("Subscribe must be called after consumerBean started");
    } else {
      log.info("########## consumerId[{}] Start to substribe topic[{}] tags[{}]",
          new Object[]{this.consumerId, topic, subExpression});
      this.consumer.subscribe(topic, subExpression, new MessageListener() {
        @Override
        public Action consume(Message message, ConsumeContext context) {
          RocketMqMessageListener listener = messageListener;
          if (tagListenerMap != null) {
            listener = (RocketMqMessageListener) tagListenerMap.get(message.getTag());
          }

          if (listener == null) {
            Object[] params = new Object[]{message.getMsgID(), message.getTopic(), message.getTag(),
                message.getKey(), RocketMqConsumerBean.this.consumerId};
            RocketMqConsumerBean.log.error(
                "Mq consume messageListener has not found, msgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}]",
                params);
            return Action.ReconsumeLater;
          } else {
            MqBaseMessageBody record = null;
            MessageContext messageContext = MqContextUtil.getMessageContext(message);

            Action var14;
            try {
              Object[] paramsx;
              try {
                String initiationID = SerialNo.init(AppName.DOUBO_MQ);
                MDC.put("initiationID", initiationID);
                record = listener.deserialize(message.getBody(), messageContext);
                if (record == null) {
                  paramsx = new Object[]{SerialNo.getSerialNo(), message.getMsgID(),
                      messageContext.getUniqMsgId(), message.getTopic(), message.getTag(),
                      message.getKey(), RocketMqConsumerBean.this.consumerId,
                      message.getReconsumeTimes()};
                  RocketMqConsumerBean.log.error(
                      "[{}]Mq consume message Exception: record is null. msgId:[{}], uniqMsgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}], retryTimes:[{}]",
                      paramsx);
                } else if (RocketMqConsumerBean.log.isDebugEnabled()) {
                  paramsx = new Object[]{SerialNo.getSerialNo(), message.getMsgID(),
                      messageContext.getUniqMsgId(), message.getTopic(), message.getTag(),
                      message.getKey(), RocketMqConsumerBean.this.consumerId,
                      message.getReconsumeTimes(), record};
                  RocketMqConsumerBean.log.debug(
                      "[{}] Mq Received message: msgId:[{}], uniqMsgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}], retryTimes:[{}], record:[{}]",
                      paramsx);
                } else {
                  paramsx = new Object[]{SerialNo.getSerialNo(), message.getMsgID(),
                      messageContext.getUniqMsgId(), message.getTopic(), message.getTag(),
                      message.getKey(), message.getReconsumeTimes()};
                  RocketMqConsumerBean.log.info(
                      "[{}] Mq Received message: msgId:[{}], uniqMsgId:[{}], topic[{}], tag[{}], key[{}], retryTimes:[{}]",
                      paramsx);
                }

                var14 = listener.call(record, messageContext);
                return var14;
              } catch (Throwable var11) {
                if (record != null) {
                  paramsx = new Object[]{SerialNo.getSerialNo(), message.getMsgID(),
                      messageContext.getUniqMsgId(), message.getTopic(), message.getTag(),
                      message.getKey(), RocketMqConsumerBean.this.consumerId,
                      message.getReconsumeTimes(), record, ExceptionUtil.getAsString(var11)};
                  RocketMqConsumerBean.log.error(
                      "[{}]Mq consume message Exception: msgId:[{}], uniqMsgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}], retryTimes:[{}], record:[{}], Exception:{}",
                      paramsx);
                } else {
                  paramsx = new Object[]{SerialNo.getSerialNo(), message.getMsgID(),
                      messageContext.getUniqMsgId(), message.getTopic(), message.getTag(),
                      message.getKey(), RocketMqConsumerBean.this.consumerId,
                      message.getReconsumeTimes(), record, ExceptionUtil.getAsString(var11)};
                  RocketMqConsumerBean.log.error(
                      "[{}]Mq consume message Exception: msgId:[{}], uniqMsgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}], retryTimes:[{}], record is null, message:[{}], Exception:{}",
                      paramsx);
                }
              }

              var14 = Action.ReconsumeLater;
            } finally {
              SerialNo.clear();
              MDC.remove("initiationID");
            }

            return var14;
          }
        }
      });
    }
  }

  @Override
  public <T> void subscribe(String topic, String subExpression,
      RocketMqMessageListener<T> listener) {
    if (listener == null) {
      throw new ONSClientException("subscriptionTable[topic:" + topic + ", tag:" + subExpression
          + "] listener must not null");
    } else {
      this.subscribe(topic, subExpression, listener, (Map) null);
    }
  }

  @Override
  public void unsubscribe(String topic) {
    if (null == this.consumer) {
      throw new ONSClientException("unsubscribe must be called after consumerBean started");
    } else {
      this.consumer.unsubscribe(topic);
    }
  }

  public Properties getProperties() {
    return this.properties;
  }

  public void setProperties(Properties properties) {
    this.properties = properties;
  }

  public void setSubscriptionTable(Map<SubscibeTopic, RocketMqMessageListener> subscriptionTable) {
    if (subscriptionTable != null) {
      this.topicTagListenerMap = initTopicTagListenerMap(subscriptionTable);
    }

  }

  public void setSubscriptionList(List<RocketMqMessageListener> subscriptionList) {
    if (subscriptionList != null) {
      this.topicTagListenerMap = this.initTopicTagListenerList(subscriptionList);
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

  private static Map<String, Map<String, RocketMqMessageListener>> initTopicTagListenerMap(
      Map<SubscibeTopic, RocketMqMessageListener> subscriptionTable) {
    Map<String, Map<String, RocketMqMessageListener>> topicTagListenerMap = new HashMap(
        subscriptionTable.size());

    Map.Entry next;
    Object tagListenerMap;
    for (Iterator it = subscriptionTable.entrySet().iterator(); it.hasNext(); ((Map) tagListenerMap)
        .put(((SubscibeTopic) next.getKey()).getExpression(), next.getValue())) {
      next = (Map.Entry) it.next();
      tagListenerMap = (Map) topicTagListenerMap.get(((SubscibeTopic) next.getKey()).getTopic());
      if (tagListenerMap == null) {
        tagListenerMap = new HashMap(5);
        topicTagListenerMap.put(((SubscibeTopic) next.getKey()).getTopic(),
            (Map<String, RocketMqMessageListener>) tagListenerMap);
      }
    }

    return topicTagListenerMap;
  }

  private Map<String, Map<String, RocketMqMessageListener>> initTopicTagListenerList(
      List<RocketMqMessageListener> subscriptionList) {
    Map<SubscibeTopic, RocketMqMessageListener> subscriptionTable = new HashMap();
    Iterator var3 = subscriptionList.iterator();

    RocketMqMessageListener listener;
    MqTopic rockMqTopic;
    RocketMqMessageListener ifAbsent;
    do {
      if (!var3.hasNext()) {
        return initTopicTagListenerMap(subscriptionTable);
      }

      listener = (RocketMqMessageListener) var3.next();
      rockMqTopic = listener.subscriTopic();
      if (rockMqTopic == null) {
        throw new IllegalArgumentException("MqConsumer[" + listener.getClass()
            + "] subscribe topic must not null, must implement method[subscriTopic]");
      }

      ifAbsent = (RocketMqMessageListener) subscriptionTable
          .putIfAbsent(new SubscibeTopic(rockMqTopic), listener);
    } while (ifAbsent == null);

    throw new IllegalArgumentException(
        "MqConsumer[" + listener.getClass() + "] subscribe topic[" + (String) rockMqTopic.getCode()
            + "] tag[" + rockMqTopic.getTag() + "] is exist!");
  }
}