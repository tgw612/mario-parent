package com.mario.common.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public final class DouboThreadFactory implements ThreadFactory {

  private static final AtomicLong THREAD_NUMBER = new AtomicLong(1L);
  private static final ThreadGroup THREAD_GROUP = new ThreadGroup("Doubo");
  private final boolean daemon;
  private final String namePrefix;

  private DouboThreadFactory(String namePrefix, boolean daemon) {
    this.namePrefix = namePrefix;
    this.daemon = daemon;
  }

  public static ThreadFactory create(String namePrefix, boolean daemon) {
    return new DouboThreadFactory(namePrefix, daemon);
  }

  @Override
  public Thread newThread(Runnable runnable) {
    Thread thread = new Thread(THREAD_GROUP, runnable,
        THREAD_GROUP.getName() + "-" + this.namePrefix + "-" + THREAD_NUMBER.getAndIncrement());
    thread.setDaemon(this.daemon);
    if (thread.getPriority() != 5) {
      thread.setPriority(5);
    }

    return thread;
  }
}
