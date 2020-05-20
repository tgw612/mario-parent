package com.mario.ali.mq.consumer.order;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.aliyun.openservices.ons.api.order.ConsumeOrderContext;
import com.aliyun.openservices.ons.api.order.MessageOrderListener;
import com.aliyun.openservices.ons.api.order.OrderAction;
import com.aliyun.openservices.ons.api.order.OrderConsumer;
import com.mario.ali.mq.consumer.ConsumerCheck;
import com.mario.ali.mq.consumer.order.api.RocketMqOrderConsumer;
import com.mario.ali.mq.consumer.order.api.RocketMqOrderMessageListener;
import com.mario.ali.mq.model.MqBaseMessageBody;
import com.mario.ali.mq.model.order.OrderMessageContext;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class RocketMqOrderConsumerBean implements RocketMqOrderConsumer, ConsumerCheck {

  private static final Logger log = LoggerFactory.getLogger(RocketMqOrderConsumerBean.class);
  private Properties properties;
  private Map<String, Map<String, RocketMqOrderMessageListener>> topicTagListenerMap;
  private OrderConsumer orderConsumer;
  private String consumerId;

  public RocketMqOrderConsumerBean() {
  }

  @Override
  public void start() {
    if (null == this.properties) {
      throw new ONSClientException("properties field not set");
    } else if (null == this.topicTagListenerMap) {
      throw new ONSClientException("topicTagListenerMap field not set");
    } else {
      this.consumerId = MqUtil.getConsumerId(this.properties);
      log.info("########## Start to create Ordered Consumer[consumerId={}]", this.consumerId);
      this.check(this.consumerId);
      this.orderConsumer = ONSFactory.createOrderedConsumer(this.properties);
      Iterator it = this.topicTagListenerMap.entrySet().iterator();

      while (true) {
        while (it.hasNext()) {
          Map.Entry<String, Map<String, RocketMqOrderMessageListener>> entry = (Map.Entry) it
              .next();
          Map<String, RocketMqOrderMessageListener> tagListenerMap = (Map) entry.getValue();
          if (tagListenerMap.size() <= 0) {
            throw new ONSClientException(
                "subscriptionTable[topic:" + (String) entry.getKey() + "] listener not set");
          }

          if (tagListenerMap.size() == 1) {
            tagListenerMap.keySet().forEach((tag) -> {
              RocketMqOrderMessageListener rocketMqMessageListener = (RocketMqOrderMessageListener) tagListenerMap
                  .get(tag);
              if (rocketMqMessageListener == null) {
                throw new ONSClientException(
                    "subscriptionTable[topic:" + (String) entry.getKey() + ", tag:" + tag
                        + "] listener not set");
              } else {
                this.subscribe((String) entry.getKey(), tag, rocketMqMessageListener, (Map) null);
              }
            });
          } else {
            Iterator<Map.Entry<String, RocketMqOrderMessageListener>> tagListenerIterator = tagListenerMap
                .entrySet().iterator();

            StringBuilder tagBuilder;
            for (tagBuilder = new StringBuilder(); tagListenerIterator.hasNext();
                tagBuilder.append((String) ((Map.Entry) tagListenerIterator.next()).getKey())) {
              if (tagBuilder.length() > 0) {
                tagBuilder.append("||");
              }
            }

            this.subscribe((String) entry.getKey(), tagBuilder.toString(),
                (RocketMqOrderMessageListener) null, tagListenerMap);
          }
        }

        this.orderConsumer.start();
        return;
      }
    }
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

  private void subscribe(String topic, String subExpression,
      final RocketMqOrderMessageListener messageListener,
      final Map<String, RocketMqOrderMessageListener> tagListenerMap) {
    if (null == this.orderConsumer) {
      throw new ONSClientException("Subscribe must be called after orderConsumerBean started");
    } else {
      log.info("########## consumerId[{}] Start to substribe topic[{}] tags[{}]",
          new Object[]{this.consumerId, topic, subExpression});
      this.orderConsumer.subscribe(topic, subExpression, new MessageOrderListener() {
        @Override
        public OrderAction consume(Message message, ConsumeOrderContext context) {
          RocketMqOrderMessageListener listener = messageListener;
          if (tagListenerMap != null) {
            listener = (RocketMqOrderMessageListener) tagListenerMap.get(message.getTag());
          }

          if (listener == null) {
            Object[] params = new Object[]{message.getMsgID(), message.getTopic(), message.getTag(),
                message.getKey(), RocketMqOrderConsumerBean.this.consumerId};
            RocketMqOrderConsumerBean.log.error(
                "Mq order consume[has been suspended] messageListener has not found, msgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}]",
                params);
            return OrderAction.Suspend;
          } else {
            MqBaseMessageBody record = null;
            OrderMessageContext orderMessageContext = MqContextUtil.getOrderMessageContext(message);

            OrderAction var14;
            try {
              Object[] paramsx;
              try {
                String initiationID = SerialNo.init(AppName.DOUBO_MQ);
                MDC.put("initiationID", initiationID);
                record = listener.deserialize(message.getBody(), orderMessageContext);
                if (record == null) {
                  paramsx = new Object[]{SerialNo.getSerialNo(), message.getMsgID(),
                      orderMessageContext.getUniqMsgId(), message.getTopic(), message.getTag(),
                      message.getKey(), RocketMqOrderConsumerBean.this.consumerId,
                      message.getReconsumeTimes()};
                  RocketMqOrderConsumerBean.log.error(
                      "[{}]Mq order consume message Exception: record is null. msgId:[{}], uniqMsgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}], retryTimes:[{}]",
                      paramsx);
                } else if (RocketMqOrderConsumerBean.log.isDebugEnabled()) {
                  paramsx = new Object[]{SerialNo.getSerialNo(), message.getMsgID(),
                      orderMessageContext.getUniqMsgId(), message.getTopic(), message.getTag(),
                      message.getKey(), RocketMqOrderConsumerBean.this.consumerId,
                      message.getReconsumeTimes(), record};
                  RocketMqOrderConsumerBean.log.debug(
                      "[{}]Mq Received message: msgId:[{}], uniqMsgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}], retryTimes:[{}], record:[{}]",
                      paramsx);
                } else {
                  paramsx = new Object[]{SerialNo.getSerialNo(), message.getMsgID(),
                      orderMessageContext.getUniqMsgId(), message.getTopic(), message.getTag(),
                      message.getKey(), message.getReconsumeTimes()};
                  RocketMqOrderConsumerBean.log.info(
                      "[{}]Mq Received message: msgId:[{}], uniqMsgId:[{}], topic[{}], tag[{}], key[{}], retryTimes:[{}]",
                      paramsx);
                }

                var14 = listener.call(record, orderMessageContext);
                return var14;
              } catch (Throwable var11) {
                if (record != null) {
                  paramsx = new Object[]{SerialNo.getSerialNo(), message.getMsgID(),
                      orderMessageContext.getUniqMsgId(), message.getTopic(), message.getTag(),
                      message.getKey(), RocketMqOrderConsumerBean.this.consumerId,
                      message.getReconsumeTimes(), record, ExceptionUtil.getAsString(var11)};
                  RocketMqOrderConsumerBean.log.error(
                      "[{}]Mq order consume[has been suspended] message Exception: msgId:[{}], uniqMsgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}], retryTimes:[{}], record:[{}], Exception:{}",
                      paramsx);
                } else {
                  paramsx = new Object[]{SerialNo.getSerialNo(), message.getMsgID(),
                      orderMessageContext.getUniqMsgId(), message.getTopic(), message.getTag(),
                      message.getKey(), RocketMqOrderConsumerBean.this.consumerId,
                      message.getReconsumeTimes(), record, ExceptionUtil.getAsString(var11)};
                  RocketMqOrderConsumerBean.log.error(
                      "[{}]Mq order consume[has been suspended] message Exception: msgId:[{}], uniqMsgId:[{}], topic[{}], tag[{}], key[{}], consumerId:[{}], retryTimes:[{}], record is null, message:[{}], Exception:{}",
                      paramsx);
                }
              }

              var14 = OrderAction.Suspend;
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
  public void subscribe(String topic, String subExpression, RocketMqOrderMessageListener listener) {
    if (listener == null) {
      throw new ONSClientException("subscriptionTable[topic:" + topic + ", tag:" + subExpression
          + "] listener must not null");
    } else {
      this.subscribe(topic, subExpression, listener, (Map) null);
    }
  }

  public Properties getProperties() {
    return this.properties;
  }

  public void setProperties(Properties properties) {
    this.properties = properties;
  }

  public void setSubscriptionTable(
      Map<SubscibeTopic, RocketMqOrderMessageListener> subscriptionTable) {
    if (subscriptionTable != null) {
      this.topicTagListenerMap = this.initTopicTagListenerMap(subscriptionTable);
    }

  }

  public void setSubscriptionList(List<RocketMqOrderMessageListener> subscriptionList) {
    if (subscriptionList != null) {
      this.topicTagListenerMap = this.initTopicTagListenerList(subscriptionList);
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
    Map<String, Map<String, RocketMqOrderMessageListener>> topicTagListenerMap = new HashMap(
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
            (Map<String, RocketMqOrderMessageListener>) tagListenerMap);
      }
    }

    return topicTagListenerMap;
  }

  private Map<String, Map<String, RocketMqOrderMessageListener>> initTopicTagListenerList(
      List<RocketMqOrderMessageListener> subscriptionList) {
    Map<SubscibeTopic, RocketMqOrderMessageListener> subscriptionTable = new HashMap();
    Iterator var3 = subscriptionList.iterator();

    RocketMqOrderMessageListener listener;
    MqTopic rockMqTopic;
    RocketMqOrderMessageListener ifAbsent;
    do {
      if (!var3.hasNext()) {
        return this.initTopicTagListenerMap(subscriptionTable);
      }

      listener = (RocketMqOrderMessageListener) var3.next();
      rockMqTopic = listener.subscriTopic();
      if (rockMqTopic == null) {
        throw new IllegalArgumentException("MqOrderConsumer[" + listener.getClass()
            + "] subscribe topic must not null, must implement method[subscriTopic]");
      }

      ifAbsent = (RocketMqOrderMessageListener) subscriptionTable
          .putIfAbsent(new SubscibeTopic(rockMqTopic), listener);
    } while (ifAbsent == null);

    throw new IllegalArgumentException(
        "MqOrderConsumer[" + listener.getClass() + "] subscribe topic[" + (String) rockMqTopic
            .getCode() + "] tag[" + rockMqTopic.getTag() + "] is exist!");
  }
}
