package com.mario.ali.mq.producer.api;

import com.aliyun.openservices.ons.api.transaction.TransactionStatus;
import com.mario.ali.mq.model.MessageContext;
import com.mario.ali.mq.model.MqBaseMessageBody;
import com.mario.ali.mq.serializer.MqDeserializer;

public interface RocketMqTransactionMessageChecker<T> extends MqDeserializer<T> {

  TransactionStatus check(MqBaseMessageBody<T> var1, MessageContext var2) throws Exception;
}
