package com.mario.ali.mq.model.transaction;

import com.mario.ali.mq.model.MqBaseMessageBody;
import com.mario.ali.mq.model.RockMqMessage;
import com.mario.ali.mq.topic.MqTopic;

public class RockMqTransactionMessage<T> extends RockMqMessage<T> {

  public static int DEFAULT_CHECK_TIMEINSECONDS = 120;
  private int checkImmunityTimeInSeconds;

  public RockMqTransactionMessage() {
  }

  public RockMqTransactionMessage(MqTopic topic, MqBaseMessageBody<T> body,
      int checkImmunityTimeInSeconds) {
    super(topic, body);
    this.checkImmunityTimeInSeconds = checkImmunityTimeInSeconds;
  }

  public RockMqTransactionMessage(MqTopic topic, MqBaseMessageBody<T> body) {
    this(topic, body, DEFAULT_CHECK_TIMEINSECONDS);
  }

  public int getCheckImmunityTimeInSeconds() {
    return this.checkImmunityTimeInSeconds;
  }

  public void setCheckImmunityTimeInSeconds(int checkImmunityTimeInSeconds) {
    this.checkImmunityTimeInSeconds = checkImmunityTimeInSeconds;
  }

  @Override
  public String toString() {
    return "RockMqTransactionMessage(super=" + super.toString() + ", checkImmunityTimeInSeconds="
        + this.getCheckImmunityTimeInSeconds() + ")";
  }
}
