package com.mario.ali.mq.producer.order.api;


import com.aliyun.openservices.ons.api.Admin;
import com.mario.ali.mq.model.RockMqSendResult;
import com.mario.ali.mq.model.order.RockMqOrderMessage;
import com.mario.ali.mq.serializer.MqSerializer;

public interface RocketMqOrderProducer extends Admin {

  <T> boolean send(RockMqOrderMessage<T> var1, MqSerializer<T> var2);

  <T> RockMqSendResult sendBackResult(RockMqOrderMessage<T> var1, MqSerializer<T> var2);
}
