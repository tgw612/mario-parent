package com.mario.ali.mq.consumer.api;

import com.aliyun.openservices.ons.api.Admin;

public interface RocketMqConsumer extends Admin {

  <T> void subscribe(String var1, String var2, RocketMqMessageListener<T> var3);

  void unsubscribe(String var1);
}