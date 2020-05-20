package com.mario.ali.mq.topic.env;

import org.springframework.beans.factory.InitializingBean;

public class TopicEnvBean implements InitializingBean {

  ITopicEnv topicEnv;

  public TopicEnvBean() {
  }

  public void setTopicEnv(ITopicEnv topicEnv) {
    this.topicEnv = topicEnv;
  }

  public void afterPropertiesSet() throws Exception {
    if (this.topicEnv == null) {
      throw new IllegalArgumentException("Mq topic environment must not null");
    }
  }

  public ITopicEnv getTopicEnv() {
    return this.topicEnv;
  }
}
