package com.mario.ali.mq.util;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageAccessor;
import com.mario.ali.mq.model.MessageContext;
import com.mario.ali.mq.model.order.OrderMessageContext;
import com.mario.ali.mq.model.transaction.TransactionMessageContext;

public class MqContextUtil {

  public MqContextUtil() {
  }

  public static MessageContext getMessageContext(Message message) {
    MessageContext messageContext = new MessageContext(message.getTopic(),
        message.getUserProperties(), MessageAccessor.getSystemProperties(message));
    return messageContext;
  }

  public static OrderMessageContext getOrderMessageContext(Message message) {
    OrderMessageContext messageContext = new OrderMessageContext(message.getTopic(),
        message.getUserProperties(), MessageAccessor.getSystemProperties(message));
    return messageContext;
  }

  public static TransactionMessageContext getTransactionMessageContext(Message message) {
    TransactionMessageContext transactionMessageContext = new TransactionMessageContext(
        message.getTopic(), message.getUserProperties(),
        MessageAccessor.getSystemProperties(message));
    return transactionMessageContext;
  }
}