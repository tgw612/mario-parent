package com.mario.common.quartz;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * Created with IntelliJ IDEA. User: qiujingwang Date: 2016/10/19 Description:
 */
public class ThreadPoolTaskUtil {

  //线程池
  private static ThreadPoolTaskExecutor pool;

  /**
   * 执行线程
   */
  public static void execute(Runnable thread) {
    pool.execute(thread);
  }

  /**
   * 执行线程
   */
  public static Future<?> submit(Runnable thread) {
    return pool.submit(thread);
  }

  public static <T> Future<T> submit(Callable<T> task) {
    return pool.submit(task);
  }

  public static ListenableFuture<?> submitListenable(Runnable task) {
    return pool.submitListenable(task);
  }

  public static <T> ListenableFuture<T> submitListenable(Callable<T> task) {
    return pool.submitListenable(task);
  }

  public void setPool(ThreadPoolTaskExecutor pool) {
    ThreadPoolTaskUtil.pool = pool;
  }

  public static void pool(ThreadPoolTaskExecutor pool) {
    ThreadPoolTaskUtil.pool = pool;
  }
}
