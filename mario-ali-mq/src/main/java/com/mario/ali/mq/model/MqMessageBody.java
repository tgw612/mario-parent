package com.mario.ali.mq.model;

import java.io.Serializable;

public class MqMessageBody<T> implements MqBaseMessageBody<T>, Serializable {

  private static final long serialVersionUID = -6581653428040313373L;
  private String businessId;
  private T content;

  public MqMessageBody() {
  }

  public MqMessageBody(T content) {
    this.content = content;
  }

  public MqMessageBody(T content, String businessId) {
    this.content = content;
    this.businessId = businessId;
  }

  public static <T> MqMessageBody<T> getInstance(T content) {
    return new MqMessageBody(content);
  }

  public static <T> MqMessageBody<T> getInstance(T content, String businessId) {
    return new MqMessageBody(content, businessId);
  }

  @Override
  public String toString() {
    return "MqMessageBody(super=" + super.toString() + ", businessId=" + this.getBusinessId()
        + ", content=" + this.getContent() + ")";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (!(o instanceof MqMessageBody)) {
      return false;
    } else {
      MqMessageBody<?> other = (MqMessageBody) o;
      if (!other.canEqual(this)) {
        return false;
      } else {
        Object this$businessId = this.getBusinessId();
        Object other$businessId = other.getBusinessId();
        if (this$businessId == null) {
          if (other$businessId != null) {
            return false;
          }
        } else if (!this$businessId.equals(other$businessId)) {
          return false;
        }

        Object this$content = this.getContent();
        Object other$content = other.getContent();
        if (this$content == null) {
          if (other$content != null) {
            return false;
          }
        } else if (!this$content.equals(other$content)) {
          return false;
        }

        return true;
      }
    }
  }

  protected boolean canEqual(Object other) {
    return other instanceof MqMessageBody;
  }

  @Override
  public int hashCode() {
    int result = 1;
    Object $businessId = this.getBusinessId();
    result = result * 59 + ($businessId == null ? 43 : $businessId.hashCode());
    Object $content = this.getContent();
    result = result * 59 + ($content == null ? 43 : $content.hashCode());
    return result;
  }

  @Override
  public String getBusinessId() {
    return this.businessId;
  }

  @Override
  public T getContent() {
    return this.content;
  }

  public void setBusinessId(String businessId) {
    this.businessId = businessId;
  }

  public void setContent(T content) {
    this.content = content;
  }
}

