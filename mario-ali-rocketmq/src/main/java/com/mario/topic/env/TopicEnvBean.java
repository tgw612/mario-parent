package com.mario.topic.env;

import org.springframework.beans.factory.InitializingBean;

/**
 * Created with IntelliJ IDEA. User: qiujingwang Date: 2016/9/14 Description:MQ主题环镜
 * 启动的时候必须要先初始化env属性，才能使用，否则报错空指针（不设置默认值，防止忘了修改而导致环镜错误）
 */
public class TopicEnvBean implements InitializingBean {

  ITopicEnv topicEnv;

  public void setTopicEnv(ITopicEnv topicEnv) {
    this.topicEnv = topicEnv;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (this.topicEnv == null) {
      throw new IllegalArgumentException("Mq topic environment must not null");
    }

    //设置MQ 主题环镜
    //TopicEnum.env = topicEnv;
  }

  public ITopicEnv getTopicEnv() {
    return topicEnv;
  }
}
