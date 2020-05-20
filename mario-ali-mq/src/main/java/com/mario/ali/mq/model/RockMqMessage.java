package com.mario.ali.mq.model;

import com.aliyun.openservices.ons.api.Message;
import com.mario.ali.mq.topic.MqTopic;
import java.io.Serializable;

public class RockMqMessage<T> extends Message implements Serializable {

  private MqBaseMessageBody<T> content;

  public RockMqMessage() {
  }

  public RockMqMessage(MqTopic topic, MqBaseMessageBody<T> content) {
    super((String) topic.getCode(), topic.getTag(), content.getBusinessId(), (byte[]) null);
    this.content = content;
  }

  @Override
  public String toString() {
    return "RockMqMessage(super=" + super.toString() + ", content=" + this.getContent() + ")";
  }

  public MqBaseMessageBody<T> getContent() {
    return this.content;
  }

  public void setContent(MqBaseMessageBody<T> content) {
    this.content = content;
  }
}
