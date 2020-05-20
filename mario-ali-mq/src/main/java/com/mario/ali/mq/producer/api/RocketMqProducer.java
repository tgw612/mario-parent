package com.mario.ali.mq.producer.api;

import com.aliyun.openservices.ons.api.Admin;
import com.aliyun.openservices.ons.api.SendCallback;
import com.mario.ali.mq.model.RockMqMessage;
import com.mario.ali.mq.model.RockMqSendResult;
import com.mario.ali.mq.serializer.MqSerializer;

public interface RocketMqProducer extends Admin {

  <T> boolean send(RockMqMessage<T> var1, MqSerializer<T> var2);

  <T> RockMqSendResult sendBackResult(RockMqMessage<T> var1, MqSerializer<T> var2);

  <T> boolean sendOneway(RockMqMessage<T> var1, MqSerializer<T> var2);

  <T> boolean sendAsync(RockMqMessage<T> var1, MqSerializer<T> var2, SendCallback var3);
}