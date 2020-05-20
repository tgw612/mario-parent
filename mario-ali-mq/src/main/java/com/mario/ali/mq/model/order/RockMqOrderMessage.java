package com.mario.ali.mq.model.order;

import com.mario.ali.mq.model.MqBaseMessageBody;
import com.mario.ali.mq.model.RockMqMessage;
import com.mario.ali.mq.topic.MqTopic;
import com.mario.common.util.StringUtil;
import javax.validation.constraints.NotNull;

public class RockMqOrderMessage<T> extends RockMqMessage<T> {

  @NotNull
  private String shardingKey;

  public RockMqOrderMessage() {
  }

  public RockMqOrderMessage(MqTopic topic, MqBaseMessageBody<T> body, String shardingKey) {
    super(topic, body);
    this.shardingKey = shardingKey;
  }

  public String getShardingKey() {
    return StringUtil.defaultString(this.shardingKey, this.getKey());
  }

  public void setShardingKey(String shardingKey) {
    this.shardingKey = shardingKey;
  }

  @Override
  public String toString() {
    return "RockMqOrderMessage(super=" + super.toString() + ", shardingKey=" + this.getShardingKey()
        + ")";
  }
}
