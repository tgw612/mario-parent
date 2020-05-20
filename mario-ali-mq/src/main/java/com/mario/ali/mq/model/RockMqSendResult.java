package com.mario.ali.mq.model;

import com.aliyun.openservices.ons.api.SendResult;
import java.beans.ConstructorProperties;
import javax.annotation.Nullable;

public class RockMqSendResult {

  private static final RockMqSendResult FAIL_INSTANCE = new RockMqSendResult(false, (String) null,
      (String) null) {
    @Override
    public void setMessageId(@Nullable String messageId) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setSuccess(boolean success) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setTopic(@Nullable String topic) {
      throw new UnsupportedOperationException();
    }
  };
  private boolean success;
  @Nullable
  private String messageId;
  @Nullable
  private String topic;

  public static RockMqSendResult successIfNotNull(SendResult sendResult) {
    return sendResult != null ? new RockMqSendResult(true, sendResult.getMessageId(),
        sendResult.getTopic()) : fail();
  }

  public static RockMqSendResult fail() {
    return FAIL_INSTANCE;
  }

  @ConstructorProperties({"success", "messageId", "topic"})
  public RockMqSendResult(boolean success, @Nullable String messageId, @Nullable String topic) {
    this.success = success;
    this.messageId = messageId;
    this.topic = topic;
  }

  public RockMqSendResult() {
  }

  public boolean isSuccess() {
    return this.success;
  }

  @Nullable
  public String getMessageId() {
    return this.messageId;
  }

  @Nullable
  public String getTopic() {
    return this.topic;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public void setMessageId(@Nullable String messageId) {
    this.messageId = messageId;
  }

  public void setTopic(@Nullable String topic) {
    this.topic = topic;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (!(o instanceof RockMqSendResult)) {
      return false;
    } else {
      RockMqSendResult other = (RockMqSendResult) o;
      if (!other.canEqual(this)) {
        return false;
      } else if (this.isSuccess() != other.isSuccess()) {
        return false;
      } else {
        Object this$messageId = this.getMessageId();
        Object other$messageId = other.getMessageId();
        if (this$messageId == null) {
          if (other$messageId != null) {
            return false;
          }
        } else if (!this$messageId.equals(other$messageId)) {
          return false;
        }

        Object this$topic = this.getTopic();
        Object other$topic = other.getTopic();
        if (this$topic == null) {
          if (other$topic != null) {
            return false;
          }
        } else if (!this$topic.equals(other$topic)) {
          return false;
        }

        return true;
      }
    }
  }

  protected boolean canEqual(Object other) {
    return other instanceof RockMqSendResult;
  }

  @Override
  public int hashCode() {
    int result = 1;
    result = result * 59 + (this.isSuccess() ? 79 : 97);
    Object $messageId = this.getMessageId();
    result = result * 59 + ($messageId == null ? 43 : $messageId.hashCode());
    Object $topic = this.getTopic();
    result = result * 59 + ($topic == null ? 43 : $topic.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return "RockMqSendResult(success=" + this.isSuccess() + ", messageId=" + this.getMessageId()
        + ", topic=" + this.getTopic() + ")";
  }
}
