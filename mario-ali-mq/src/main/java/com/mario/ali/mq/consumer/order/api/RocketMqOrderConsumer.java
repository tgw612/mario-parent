package com.mario.ali.mq.consumer.order.api;

import com.aliyun.openservices.ons.api.Admin;

public interface RocketMqOrderConsumer extends Admin {

  <T> void subscribe(String var1, String var2, RocketMqOrderMessageListener<T> var3);
}

