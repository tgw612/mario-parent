package com.mario.model;

import java.util.Properties;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MessageContext {

  /**
   * 消息主题
   */
  private String topic;
  /**
   * 用户属性
   */
  private Properties userProperties;

  /**
   * 二级主题分类
   */
  private String tag;

  /**
   * 消息ID
   */
  private String msgID;

  private String key;

  /**
   * 重试次数
   */
  private int retryTimes;

  public MessageContext() {
  }

  public MessageContext(String topic, Properties userProperties, String tag, String msgID,
      String key, int retryTimes) {
    this.topic = topic;
    this.userProperties = userProperties;
    this.tag = tag;
    this.msgID = msgID;
    this.key = key;
    this.retryTimes = retryTimes;
  }
}
