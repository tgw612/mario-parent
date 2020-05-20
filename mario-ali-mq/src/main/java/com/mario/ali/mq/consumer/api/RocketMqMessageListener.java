package com.mario.ali.mq.consumer.api;

import com.aliyun.openservices.ons.api.Action;
import com.mario.ali.mq.model.MessageContext;
import com.mario.ali.mq.model.MqBaseMessageBody;
import com.mario.ali.mq.serializer.MqDeserializer;
import com.mario.ali.mq.topic.MqTopic;

public interface RocketMqMessageListener<T> extends MqDeserializer<T> {

  Action call(MqBaseMessageBody<T> var1, MessageContext var2) throws Exception;

  default MqTopic subscriTopic() {
    return null;
  }
}

