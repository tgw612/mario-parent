package com.mario.ali.mq.topic.env;

public enum TopicEnv implements ITopicEnv {
  DEV {
    public String getCode(String code) {
      return code + "_dev";
    }
  },
  TEST {
    public String getCode(String code) {
      return code + "_test";
    }
  },
  UAT {
    public String getCode(String code) {
      return code + "_uat";
    }
  },
  PRE {
    public String getCode(String code) {
      return code + "_pre";
    }
  },
  PROD {
    public String getCode(String code) {
      return code + "_prod";
    }
  },
  EMPTY {
    public String getCode(String code) {
      return code;
    }
  };

  private TopicEnv() {
  }

  public static TopicEnv resolveByName(String name) {
    return valueOf(name);
  }
}

