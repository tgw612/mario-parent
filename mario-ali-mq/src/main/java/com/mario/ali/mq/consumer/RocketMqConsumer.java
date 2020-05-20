package com.mario.ali.mq.consumer;

import com.aliyun.openservices.ons.api.Admin;
import com.mario.ali.mq.consumer.api.RocketMqMessageListener;

public interface RocketMqConsumer extends Admin {

  <T> void subscribe(String var1, String var2, RocketMqMessageListener<T> var3);

  void unsubscribe(String var1);
}
