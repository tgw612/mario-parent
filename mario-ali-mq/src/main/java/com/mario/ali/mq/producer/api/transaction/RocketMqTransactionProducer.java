package com.mario.ali.mq.producer.api.transaction;

import com.aliyun.openservices.ons.api.Admin;
import com.mario.ali.mq.model.RockMqSendResult;
import com.mario.ali.mq.model.transaction.RockMqTransactionMessage;
import com.mario.ali.mq.producer.api.RocketMqLocalTransactionExecuter;
import com.mario.ali.mq.serializer.MqSerializer;

public interface RocketMqTransactionProducer extends Admin {

  <T> boolean send(RockMqTransactionMessage<T> var1, RocketMqLocalTransactionExecuter var2,
      Object var3, MqSerializer<T> var4);

  <T> RockMqSendResult sendBackResult(RockMqTransactionMessage<T> var1,
      RocketMqLocalTransactionExecuter var2, Object var3, MqSerializer<T> var4);

  <T> boolean send(RockMqTransactionMessage<T> var1, RocketMqLocalTransactionExecuter var2,
      MqSerializer<T> var3);

  <T> RockMqSendResult sendBackResult(RockMqTransactionMessage<T> var1,
      RocketMqLocalTransactionExecuter var2, MqSerializer<T> var3);
}
