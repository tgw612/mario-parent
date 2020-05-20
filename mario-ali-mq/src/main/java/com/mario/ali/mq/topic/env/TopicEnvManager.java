package com.mario.ali.mq.topic.env;

import com.mario.common.util.StaticFieldUtil;

public class TopicEnvManager {

  private static ITopicEnv topicEnv;
  private static TopicEnvManager instance;

  public TopicEnvManager() {
  }

  public static TopicEnvManager getInstance() {
    if (instance == null) {
      Class var0 = TopicEnvManager.class;
      synchronized (TopicEnvManager.class) {
        if (instance == null) {
          instance = new TopicEnvManager();
        }
      }
    }

    return instance;
  }

  public static ITopicEnv topicEnv() {
    return topicEnv;
  }

  public ITopicEnv getTopicEnv() {
    return topicEnv();
  }

  public void setTopicEnv(ITopicEnv topicEnv) {
    topicEnv(topicEnv);
  }

  public static void topicEnv(ITopicEnv topicEnv) {
    TopicEnvManager.topicEnv = topicEnv;
  }

  public void setTopicEnvByFullClassName(String topicFullClassFieldName) {
    topicEnvByFullClassName(topicFullClassFieldName);
  }

  public static void topicEnvByFullClassName(String topicFullClassFieldName) {
    ITopicEnv topicEnv = (ITopicEnv) StaticFieldUtil.getInstance(topicFullClassFieldName);
    topicEnv(topicEnv);
  }

  public static void main(String[] args) {
    TopicEnv dev1 = TopicEnv.resolveByName("DEV");
    TopicEnv dev2 = (TopicEnv) StaticFieldUtil
        .getInstance("com.doubo.common.topic.env.TopicEnv.DEV");
    System.out.println(dev1.getCode(""));
    System.out.println(dev2.getCode(""));
  }

  static {
    topicEnv = TopicEnv.EMPTY;
  }
}
