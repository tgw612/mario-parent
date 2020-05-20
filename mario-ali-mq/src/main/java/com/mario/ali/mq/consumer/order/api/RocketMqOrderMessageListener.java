package com.mario.ali.mq.consumer.order.api;

import com.aliyun.openservices.ons.api.order.OrderAction;
import com.mario.ali.mq.model.MessageContext;
import com.mario.ali.mq.model.MqBaseMessageBody;
import com.mario.ali.mq.serializer.MqDeserializer;
import com.mario.ali.mq.topic.MqTopic;

public interface RocketMqOrderMessageListener<T> extends MqDeserializer<T> {

  OrderAction call(MqBaseMessageBody<T> var1, MessageContext var2) throws Exception;

  default MqTopic subscriTopic() {
    return null;
  }
}

