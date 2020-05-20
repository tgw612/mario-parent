package com.mario.ali.mq.producer.api;

import com.aliyun.openservices.ons.api.transaction.TransactionStatus;
import com.mario.ali.mq.model.transaction.RockMqTransactionMessage;
import com.mario.ali.mq.model.transaction.TransactionMessageContext;

@FunctionalInterface
public interface RocketMqLocalTransactionExecuter<T> {

  TransactionStatus execute(RockMqTransactionMessage<T> var1, Object var2,
      TransactionMessageContext var3);
}