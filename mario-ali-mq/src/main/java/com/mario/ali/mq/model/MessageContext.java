package com.mario.ali.mq.model;

import java.util.Properties;

public class MessageContext {

  private String topic;
  private Properties systemProperties;
  private Properties userProperties;

  public MessageContext() {
  }

  public MessageContext(String topic, Properties userProperties, Properties systemProperties) {
    this.topic = topic;
    this.userProperties = userProperties;
    this.systemProperties = systemProperties;
  }

  public String getTag() {
    return this.getSystemProperties("__TAG");
  }

  public String getSystemProperties(String key) {
    return null != this.systemProperties ? this.systemProperties.getProperty(key) : null;
  }

  public String getUserProperties(String key) {
    return null != this.userProperties ? this.userProperties.getProperty(key) : null;
  }

  public String getKey() {
    return this.getSystemProperties("__KEY");
  }

  public String getMsgID() {
    return this.getSystemProperties("__MSGID");
  }

  public int getRetryTimes() {
    return this.getReconsumeTimes();
  }

  public int getReconsumeTimes() {
    String pro = this.getSystemProperties("__RECONSUMETIMES");
    return pro != null ? Integer.parseInt(pro) : 0;
  }

  public long getBornTimestamp() {
    String pro = this.getSystemProperties("__BORNTIMESTAMP");
    return pro != null ? Long.parseLong(pro) : 0L;
  }

  public String getBornHost() {
    String pro = this.getSystemProperties("__BORNHOST");
    return pro == null ? "" : pro;
  }

  public long getStartDeliverTime() {
    String pro = this.getSystemProperties("__STARTDELIVERTIME");
    return pro != null ? Long.parseLong(pro) : 0L;
  }

  public String getShardingKey() {
    String pro = this.getSystemProperties("__SHARDINGKEY");
    return pro == null ? "" : pro;
  }

  public String getUniqMsgId() {
    String pro = this.getUserProperties("UNIQ_KEY");
    return pro == null ? "" : pro;
  }

  public long consumeStartTime() {
    String pro = this.getSystemProperties("CONSUME_START_TIME");
    return pro != null ? Long.parseLong(pro) : 0L;
  }

  @Override
  public String toString() {
    return "MessageContext(topic=" + this.getTopic() + ", systemProperties=" + this
        .getSystemProperties() + ", userProperties=" + this.getUserProperties() + ")";
  }

  public String getTopic() {
    return this.topic;
  }

  public Properties getSystemProperties() {
    return this.systemProperties;
  }

  public Properties getUserProperties() {
    return this.userProperties;
  }
}
