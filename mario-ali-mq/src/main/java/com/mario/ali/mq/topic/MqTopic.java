package com.mario.ali.mq.topic;

public interface MqTopic extends Topic {

  default String getTag() {
    return "*";
  }
}