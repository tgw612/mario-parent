package com.mario.ali.mq.consumer;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.batch.BatchConsumer;
import com.aliyun.openservices.ons.api.batch.BatchMessageListener;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.mario.ali.mq.consumer.api.RocketMqBatchMessageListener;
import com.mario.ali.mq.consumer.api.batch.RocketMqBatchConsumer;
import com.mario.ali.mq.model.MessageContext;
import com.mario.ali.mq.model.MqBaseMessageBody;
import com.mario.ali.mq.topic.MqTopic;
import com.mario.ali.mq.topic.SubscibeTopic;
import com.mario.ali.mq.util.MqContextUtil;
import com.mario.ali.mq.util.MqUtil;
import com.mario.common.enums.AppName;
import com.mario.common.threadlocal.SerialNo;
import com.mario.common.util.ExceptionUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class RocketMqBatchConsumerBean implements RocketMqBatchConsumer, ConsumerCheck {

  private static final Logger log = LoggerFactory.getLogger(RocketMqBatchConsumerBean.class);
  private Properties properties;
  private Map<String, Map<String, RocketMqBatchMessageListener>> topicTagListenerMap;
  private BatchConsumer batchConsumer;
  private String consumerId;

  public RocketMqBatchConsumerBean() {
  }

  @Override
  public void start() {
    if (null == this.properties) {
      throw new ONSClientException("properties field not set");
    } else if (null == this.topicTagListenerMap) {
      throw new ONSClientException("topicTagListenerMap field not set");
    } else {
      this.consumerId = MqUtil.getConsumerId(this.properties);
      log.info("########## Start to create Batch Consumer[consumerId={}]", this.consumerId);
      this.check(this.consumerId);
      this.batchConsumer = ONSFactory.createBatchConsumer(this.properties);
      this.topicTagListenerMap.forEach((key, value) -> {
        if (value.size() <= 0) {
          throw new ONSClientException("subscriptionTable[topic:" + key + "] listener not set");
        } else {
          if (value.size() == 1) {
            value.forEach((tagKey, rocketMqBatchMessageListener) -> {
              if (rocketMqBatchMessageListener == null) {
                throw new ONSClientException(
                    "subscriptionTable[topic:" + key + ", tag:" + tagKey + "] listener not set");
              } else {
                this.subscribe(key, tagKey, rocketMqBatchMessageListener, (Map) null);
              }
            });
          } else {
            String moreTagStrs = (String) value.entrySet().stream().map((entry) -> {
              return (String) entry.getKey();
            }).collect(Collectors.joining("||", "", ""));
            this.subscribe(key, moreTagStrs, (RocketMqBatchMessageListener) null, value);
          }

        }
      });
      this.batchConsumer.start();
    }
  }

  @Override
  public void updateCredential(Properties credentialProperties) {
    if (this.batchConsumer != null) {
      this.batchConsumer.updateCredential(credentialProperties);
    }

  }

  @Override
  public void shutdown() {
    if (this.batchConsumer != null) {
      this.batchConsumer.shutdown();
    }

  }

  private void subscribe(final String topic, final String subExpression,
      final RocketMqBatchMessageListener messageListener,
      final Map<String, RocketMqBatchMessageListener> tagListenerMap) {
    if (null == this.batchConsumer) {
      throw new ONSClientException("Subscribe must be called after batchConsumerBean started");
    } else {
      log.info("########## consumerId[{}] Start to substribe topic[{}] tags[{}]",
          new Object[]{this.consumerId, topic, subExpression});
      this.batchConsumer.subscribe(topic, subExpression, new BatchMessageListener() {
        @Override
        public Action consume(List<Message> messages, ConsumeContext context) {
          Action var10;
          try {
            String initiationID = SerialNo.init(AppName.DOUBO_MQ);
            MDC.put("initiationID", initiationID);
            if (messages == null || messages.isEmpty()) {
              RocketMqBatchConsumerBean.log
                  .warn("########## consumerId[{}] Start to substribe topic[{}] tags[{}] is empty",
                      new Object[]{RocketMqBatchConsumerBean.this.consumerId, topic,
                          subExpression});
              Action var14 = Action.CommitMessage;
              return var14;
            }

            Map<RocketMqBatchMessageListener, List<Message>> groupConsumeListenerMap = RocketMqBatchConsumerBean.this
                .groupConsumeListenerByMessages(messages, tagListenerMap, messageListener);
            Object[] params;
            Action var16;
            if (groupConsumeListenerMap.isEmpty()) {
              params = new Object[]{initiationID, topic, subExpression,
                  RocketMqBatchConsumerBean.this.consumerId};
              RocketMqBatchConsumerBean.log.error(
                  "[{}]Mq batchConsumer messageListener has not found, topic[{}], tag[{}], consumerId:[{}]",
                  params);
              var16 = Action.ReconsumeLater;
              return var16;
            }

            Iterator var5 = groupConsumeListenerMap.keySet().iterator();

            List messageList;
            Action action;
            do {
              if (!var5.hasNext()) {
                params = new Object[]{initiationID, messages.size(), topic, subExpression,
                    RocketMqBatchConsumerBean.this.consumerId};
                RocketMqBatchConsumerBean.log.error(
                    "[{}]Mq batchConsumer messages success, message size:[{}], topic[{}], tag[{}], consumerId:[{}]",
                    params);
                var16 = Action.CommitMessage;
                return var16;
              }

              RocketMqBatchMessageListener rocketMqBatchMessageListener = (RocketMqBatchMessageListener) var5
                  .next();
              messageList = (List) groupConsumeListenerMap.get(rocketMqBatchMessageListener);
              if (messageList == null || messageList.isEmpty()) {
                Object[] paramsx = new Object[]{initiationID, topic, subExpression,
                    RocketMqBatchConsumerBean.this.consumerId};
                RocketMqBatchConsumerBean.log.error(
                    "[{}]Mq batchConsumer messages is empty, topic[{}], tag[{}], consumerId:[{}]",
                    paramsx);
                Action var18 = Action.ReconsumeLater;
                return var18;
              }

              action = RocketMqBatchConsumerBean.this
                  .consumeBatchMessage(messageList, rocketMqBatchMessageListener);
            } while (action != null && action == Action.CommitMessage);

            Object[] paramsxx = new Object[]{initiationID, messages.size(), messageList.size(),
                topic, subExpression, RocketMqBatchConsumerBean.this.consumerId};
            RocketMqBatchConsumerBean.log.error(
                "[{}]Mq batchConsumer messages failure(retry), all message size:[{}], current message size:[{}], topic[{}], tag[{}], consumerId:[{}]",
                paramsxx);
            var10 = action;
          } finally {
            SerialNo.clear();
            MDC.remove("initiationID");
          }

          return var10;
        }
      });
    }
  }

  private Map<RocketMqBatchMessageListener, List<Message>> groupConsumeListenerByMessages(
      List<Message> messages, Map<String, RocketMqBatchMessageListener> tagListenerMap,
      RocketMqBatchMessageListener messageListener) {
    Map<RocketMqBatchMessageListener, List<Message>> listenerConsumers = new HashMap(3);

    for (int i = 0; i < messages.size(); ++i) {
      Message message = (Message) messages.get(i);
      RocketMqBatchMessageListener listener = messageListener;
      if (tagListenerMap != null) {
        listener = (RocketMqBatchMessageListener) tagListenerMap.get(message.getTag());
      }

      if (listener == null) {
        Object[] params = new Object[]{message.getMsgID(), message.getTopic(), message.getTag(),
            message.getKey(), this.consumerId};
        log.error(
            "Mq batchConsumer messageListener has not found, msgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}]",
            params);
        return Collections.emptyMap();
      }

      List<Message> messageList = (List) listenerConsumers.get(listener);
      if (messageList == null) {
        messageList = new ArrayList();
        listenerConsumers.put(listener, messageList);
      }

      ((List) messageList).add(message);
    }

    return listenerConsumers;
  }

  private Action consumeBatchMessage(List<Message> messages,
      RocketMqBatchMessageListener listener) {
    MqBaseMessageBody record = null;
    Message message = null;
    MessageContext messageContext = null;
    List<MqBaseMessageBody> records = new ArrayList(messages.size());
    ArrayList mqMessageContexts = new ArrayList(messages.size());

    Object[] params;
    try {
      for (int i = 0; i < messages.size(); ++i) {
        message = (Message) messages.get(i);
        messageContext = MqContextUtil.getMessageContext(message);
        record = listener.deserialize(message.getBody(), messageContext);
        if (record == null) {
          params = new Object[]{SerialNo.getSerialNo(), messages.size(), message.getMsgID(),
              messageContext.getUniqMsgId(), message.getTopic(), message.getTag(), message.getKey(),
              this.consumerId, message.getReconsumeTimes()};
          log.error(
              "[{}]Mq batchConsumer message Exception: message size:[{}], record is null. msgId:[{}], uniqMsgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}], retryTimes:[{}]",
              params);
          return Action.ReconsumeLater;
        }

        if (log.isDebugEnabled()) {
          params = new Object[]{SerialNo.getSerialNo(), messages.size(), message.getMsgID(),
              messageContext.getUniqMsgId(), message.getTopic(), message.getTag(), message.getKey(),
              this.consumerId, message.getReconsumeTimes(), record};
          log.debug(
              "[{}] Mq Received message: message size:[{}], msgId:[{}], uniqMsgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}], retryTimes:[{}], record:[{}]",
              params);
        } else {
          params = new Object[]{SerialNo.getSerialNo(), messages.size(), message.getMsgID(),
              messageContext.getUniqMsgId(), message.getTopic(), message.getTag(), message.getKey(),
              message.getReconsumeTimes()};
          log.info(
              "[{}] Mq Received message: message size:[{}], msgId:[{}], uniqMsgId:[{}], topic[{}], tag[{}], key[{}], retryTimes:[{}]",
              params);
        }

        records.add(record);
        mqMessageContexts.add(messageContext);
      }

      return listener.call(records, mqMessageContexts);
    } catch (Throwable var10) {
      if (record != null) {
        params = new Object[]{SerialNo.getSerialNo(), messages.size(), message.getMsgID(),
            messageContext.getUniqMsgId(), message.getTopic(), message.getTag(), message.getKey(),
            this.consumerId, message.getReconsumeTimes(), record, ExceptionUtil.getAsString(var10)};
        log.error(
            "[{}]Mq batchConsumer message Exception: message size:[{}], msgId:[{}], uniqMsgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}], retryTimes:[{}], record:[{}], Exception:{}",
            params);
      } else {
        params = new Object[]{SerialNo.getSerialNo(), messages.size(), message.getMsgID(),
            messageContext.getUniqMsgId(), message.getTopic(), message.getTag(), message.getKey(),
            this.consumerId, message.getReconsumeTimes(), record, ExceptionUtil.getAsString(var10)};
        log.error(
            "[{}]Mq batchConsumer message Exception: message size:[{}], msgId:[{}], uniqMsgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}], retryTimes:[{}], record is null, message:[{}], Exception:{}",
            params);
      }

      return Action.ReconsumeLater;
    }
  }

  @Override
  public <T> void subscribe(String topic, String subExpression,
      RocketMqBatchMessageListener<T> listener) {
    if (listener == null) {
      throw new ONSClientException("subscriptionTable[topic:" + topic + ", tag:" + subExpression
          + "] listener must not null");
    } else {
      this.subscribe(topic, subExpression, listener, (Map) null);
    }
  }

  @Override
  public void unsubscribe(String topic) {
    if (null == this.batchConsumer) {
      throw new ONSClientException("unsubscribe must be called after consumerBean started");
    } else {
      this.batchConsumer.unsubscribe(topic);
    }
  }

  public Properties getProperties() {
    return this.properties;
  }

  public void setProperties(Properties properties) {
    this.properties = properties;
  }

  public void setSubscriptionTable(
      Map<SubscibeTopic, RocketMqBatchMessageListener> subscriptionTable) {
    if (subscriptionTable != null) {
      this.topicTagListenerMap = initTopicTagListenerMap(subscriptionTable);
    }

  }

  public void setSubscriptionList(List<RocketMqBatchMessageListener> subscriptionList) {
    if (subscriptionList != null) {
      this.topicTagListenerMap = this.initTopicTagListenerList(subscriptionList);
    }

  }

  @Override
  public boolean isStarted() {
    return this.batchConsumer.isStarted();
  }

  @Override
  public boolean isClosed() {
    return this.batchConsumer.isClosed();
  }

  private static Map<String, Map<String, RocketMqBatchMessageListener>> initTopicTagListenerMap(
      Map<SubscibeTopic, RocketMqBatchMessageListener> subscriptionTable) {
    Map<String, Map<String, RocketMqBatchMessageListener>> topicTagListenerMap = new HashMap(
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
            (Map<String, RocketMqBatchMessageListener>) tagListenerMap);
      }
    }

    return topicTagListenerMap;
  }

  private Map<String, Map<String, RocketMqBatchMessageListener>> initTopicTagListenerList(
      List<RocketMqBatchMessageListener> subscriptionList) {
    Map<SubscibeTopic, RocketMqBatchMessageListener> subscriptionTable = new HashMap();
    Iterator var3 = subscriptionList.iterator();

    RocketMqBatchMessageListener listener;
    MqTopic rockMqTopic;
    RocketMqBatchMessageListener ifAbsent;
    do {
      if (!var3.hasNext()) {
        return initTopicTagListenerMap(subscriptionTable);
      }

      listener = (RocketMqBatchMessageListener) var3.next();
      rockMqTopic = listener.subscriTopic();
      if (rockMqTopic == null) {
        throw new IllegalArgumentException("MqBatchConsumer[" + listener.getClass()
            + "] subscribe topic must not null, must implement method[subscriTopic]");
      }

      ifAbsent = (RocketMqBatchMessageListener) subscriptionTable
          .putIfAbsent(new SubscibeTopic(rockMqTopic), listener);
    } while (ifAbsent == null);

    throw new IllegalArgumentException(
        "MqBatchConsumer[" + listener.getClass() + "] subscribe topic[" + (String) rockMqTopic
            .getCode() + "] tag[" + rockMqTopic.getTag() + "] is exist!");
  }
}
