package com.mario.ali.mq.topic;

import com.aliyun.openservices.ons.api.bean.Subscription;

public class SubscibeTopic extends Subscription {

  public SubscibeTopic() {
  }

  public SubscibeTopic(MqTopic topic) {
    this.setTopic((String) topic.getCode());
    this.setExpression(topic.getTag());
  }

  public void setRockMqTopic(MqTopic topic) {
    this.setTopic((String) topic.getCode());
    this.setExpression(topic.getTag());
  }

  public int hashCode() {
    int result = 1;
    result = 31 * result + (this.getTopic() == null ? 0 : this.getTopic().hashCode()) + (
        this.getExpression() == null ? 0 : this.getExpression().hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    } else if (obj == null) {
      return false;
    } else if (this.getClass() != obj.getClass()) {
      return false;
    } else {
      Subscription other = (Subscription) obj;
      if (this.getTopic() == null) {
        if (other.getTopic() != null) {
          return false;
        }
      } else if (!this.getTopic().equals(other.getTopic())) {
        return false;
      }

      if (this.getExpression() == null) {
        if (other.getExpression() != null) {
          return false;
        }
      } else if (!this.getExpression().equals(other.getExpression())) {
        return false;
      }

      return true;
    }
  }

  @Override
  public String toString() {
    return "SubscibeTopic [topic=" + this.getTopic() + ", expression=" + this.getExpression() + "]";
  }
}
