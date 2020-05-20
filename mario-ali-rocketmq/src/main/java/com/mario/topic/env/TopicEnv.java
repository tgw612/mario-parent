package com.mario.topic.env;

public enum TopicEnv implements ITopicEnv {
  /**
   * 测试环镜
   */
  TEST {
    @Override
    public String getCode(String code) {
      return code + "_test";
    }
  },
  /**
   * uat环镜
   */
  UAT {
    @Override
    public String getCode(String code) {
      return code + "_uat";
    }
  },
  /**
   * PRE环镜
   */
  PRE {
    @Override
    public String getCode(String code) {
      return code + "_pre";
    }
  },
  /**
   * 生产环境
   */
  PRO;

  private TopicEnv() {

  }

  @Override
  public String getCode(String code) {
    return code + "_prod";
  }
}
