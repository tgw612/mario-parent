package com.mario.topic;

import com.aliyun.openservices.ons.api.bean.Subscription;

public class SubscibeTopic extends Subscription {

  public SubscibeTopic() {
  }

  public SubscibeTopic(RockMqTopic topic) {
    setTopic(topic.getCode());
    setExpression(topic.getTag());
  }

  public void setRockMqTopic(RockMqTopic topic) {
    setTopic(topic.getCode());
    setExpression(topic.getTag());
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((getTopic() == null) ? 0 : getTopic().hashCode()) + (
        (getExpression() == null) ? 0 : getExpression().hashCode());
    return result;
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Subscription other = (Subscription) obj;
    if (getTopic() == null) {
      if (other.getTopic() != null) {
        return false;
      }
    } else if (!getTopic().equals(other.getTopic())) {
      return false;
    }

    if (getExpression() == null) {
      if (other.getExpression() != null) {
        return false;
      }
    } else if (!getExpression().equals(other.getExpression())) {
      return false;
    }
    return true;
  }


  @Override
  public String toString() {
    return "SubscibeTopic [topic=" + getTopic() + ", expression=" + getExpression() + "]";
  }
}
