package com.mario.ali.mq.consumer;

import com.mario.ali.mq.util.MqUtil;
import com.mario.common.constants.CommonConstants;
import java.util.concurrent.ConcurrentHashMap;

public interface ConsumerCheck {

  ConcurrentHashMap<String, Object> consumerIds = new ConcurrentHashMap();

  default void check(String consumerId) throws IllegalArgumentException {
    if (consumerIds.putIfAbsent(consumerId, CommonConstants.EMPTY_OBJECT) != null) {
      throw new IllegalArgumentException("GroupId(consumerId):" + consumerId
          + " 订阅关系不一致(只能检查当前GroupId(consumerId)是否有多个实例，不能检查是否订阅不同topic，只能通过日志ons.log或者控制台查询)");
    }
  }

  static void main(String[] args) {
    MqUtil.check("1213");
    MqUtil.check("1213");
  }
}
