package com.mario.ali.mq.consumer.api.batch;

import com.aliyun.openservices.ons.api.Admin;
import com.mario.ali.mq.consumer.api.RocketMqBatchMessageListener;

public interface RocketMqBatchConsumer extends Admin {

  <T> void subscribe(String var1, String var2, RocketMqBatchMessageListener<T> var3);

  void unsubscribe(String var1);
}
