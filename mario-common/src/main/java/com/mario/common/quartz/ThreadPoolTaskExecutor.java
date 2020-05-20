package com.mario.common.quartz;

import lombok.extern.slf4j.Slf4j;

/**
 * Created with IntelliJ IDEA. User: qiujingwang Date: 2016/9/18 Description:线程池(默认启用
 * ThreadPoolTaskUtil 工具类)
 */
@Slf4j
public class ThreadPoolTaskExecutor extends
    org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor {

  public void setEnableThreadPoolTaskUtil(boolean enableThreadPoolTaskUtil) {
    if (enableThreadPoolTaskUtil) {
      ThreadPoolTaskUtil.pool(this);
    } else {
      ThreadPoolTaskUtil.pool(null);
    }

  }

  @Override
  public void afterPropertiesSet() {

    //ThreadPoolExecutor 统一调整下
    setCorePoolSize(1);
    setMaxPoolSize(Runtime.getRuntime().availableProcessors());
    //队列大小 只放300000
    setQueueCapacity(300000);
    //使用守护线程
    setDaemon(true);

    super.afterPropertiesSet();

    /**
     * 默认为true
     */
    setEnableThreadPoolTaskUtil(true);
  }
}
