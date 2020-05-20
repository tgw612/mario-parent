package com.mario.topic;

public interface RockMqTopic extends Topic {

  /**
   * 二级主题 // Message Tag 可理解为Gmail中的标签，对消息进行再归类，方便Consumer指定过滤条件在MQ服务器过滤
   *
   * @return
   */
  default String getTag() {
    return "*";
  }
}
