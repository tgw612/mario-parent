package com.mario.common.model.response;

import java.io.Serializable;

public class Message<T> implements BaseMessage<T>, Serializable {

  private static final long serialVersionUID = -6581653428040313373L;
  private T content;

  private Message() {
  }

  private Message(T content) {
    this.content = content;
  }

  public static <T> Message<T> getInstance(T content) {
    notNull(content, "Mq Message content must not null!!!");
    return new Message(content);
  }

  @Override
  public T getContent() {
    return this.content;
  }

  private static void notNull(Object object, String message) {
    if (object == null) {
      throw new IllegalArgumentException(message);
    }
  }

  @Override
  public String toString() {
    return "Message(content=" + this.getContent() + ")";
  }
}