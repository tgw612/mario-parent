package com.mario.ali.mq.config;

import com.mario.common.util.StringUtil;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class RocketMqLoggerConfigBean implements InitializingBean {

  private static final Logger log = LoggerFactory.getLogger(RocketMqLoggerConfigBean.class);
  private static final String CLIENT_LOG_ROOT = "ons.client.logRoot";
  private static final String CLIENT_LOG_FILEMAXINDEX = "ons.client.logFileMaxIndex";
  private static final String CLIENT_LOG_LEVEL = "ons.client.logLevel";
  private static final String[] levelArray = new String[]{"ERROR", "WARN", "INFO", "DEBUG"};
  private String logRoot;
  private String logLevel;
  private int logMaxIndex;

  public RocketMqLoggerConfigBean() {
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (StringUtil.isNotBlank(this.logRoot)) {
      System.setProperty("ons.client.logRoot", this.logRoot);
    }

    if (StringUtil.isNotBlank(this.logLevel)) {
      if (!Arrays.asList(levelArray).contains(this.logLevel)) {
        throw new IllegalArgumentException("Mq Log level must be [ERROR, WARN, INFO, DEBUG]");
      }

      System.setProperty("ons.client.logLevel", this.logLevel);
    }

    if (this.logMaxIndex > 0 && this.logMaxIndex <= 100) {
      System.setProperty("ons.client.logFileMaxIndex", this.logMaxIndex + "");
    } else {
      throw new IllegalArgumentException("Mq Log max index must be Between 1 and 100");
    }
  }

  public void setLogRoot(String logRoot) {
    this.logRoot = logRoot;
  }

  public void setLogLevel(String logLevel) {
    this.logLevel = logLevel;
  }

  public void setLogMaxIndex(int logMaxIndex) {
    this.logMaxIndex = logMaxIndex;
  }
}
