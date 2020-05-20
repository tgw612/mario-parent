package com.mario.redis.lock;

import com.mario.common.lock.RedisLockKey;
import com.mario.common.threadlocal.SerialNo;
import com.mario.common.util.ExceptionUtil;
import com.mario.common.util.RandomUtil;
import com.mario.redis.util.RedisTemplateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisLock {

  private static final Logger log = LoggerFactory.getLogger(RedisLock.class);
  public static final String LOCKED = "L";
  public static final long ONE_MILLI_NANOS = 1000000L;
  public static final long DEFAULT_TIME_OUT = 5000L;
  private int expireTimeSecond;
  private String key;
  private volatile boolean locked = false;

  public RedisLock(RedisLockKey redisLockKey, String val) {
    this.key = redisLockKey.get(val);
    this.expireTimeSecond = redisLockKey.getExpireTimeSecond();
    if (this.expireTimeSecond <= 0) {
      this.expireTimeSecond = 50;
    }

  }

  public boolean lock(long timeout) {
    if (!this.locked) {
      long nano = System.nanoTime();
      timeout *= 1000000L;

      try {
        for (; System.nanoTime() - nano < timeout; Thread.sleep(5L, RandomUtil.nextInt(500))) {
          synchronized (this.key) {
            if (this.locked) {
              return false;
            }

            if (RedisTemplateUtil.setIfAbsent(this.key, "L", (long) this.expireTimeSecond)) {
              this.locked = true;
              return this.locked;
            }
          }
        }

        log.warn("[{}] Redis lock key[{}] timeout", SerialNo.getSerialNo(), this.key);
      } catch (Exception var8) {
        log.error("[{}] Redis lock key[{}] Exception:{}",
            new Object[]{SerialNo.getSerialNo(), this.key, ExceptionUtil.getAsString(var8)});
      }
    }

    return false;
  }

  public boolean lockAtOnce() {
    if (!this.locked) {
      try {
        synchronized (this.key) {
          if (this.locked) {
            return false;
          }

          if (RedisTemplateUtil.setIfAbsent(this.key, "L", (long) this.expireTimeSecond)) {
            this.locked = true;
            return this.locked;
          }
        }

        log.warn("[{}] Redis lock key[{}] once failure", SerialNo.getSerialNo(), this.key);
      } catch (Exception var4) {
        log.error("[{}] Redis lock key[{}] Exception:{}",
            new Object[]{SerialNo.getSerialNo(), this.key, ExceptionUtil.getAsString(var4)});
      }
    }

    return false;
  }

  public boolean lock() {
    return this.lock(5000L);
  }

  public boolean isLocked() {
    return this.locked;
  }

  public boolean unlock() {
    if (this.locked) {
      try {
        RedisTemplateUtil.del(new String[]{this.key});
      } catch (Throwable var2) {
        log.error("[{}] Redis Del lock key[{}] Exception:{}",
            new Object[]{SerialNo.getSerialNo(), this.key, ExceptionUtil.getAsString(var2)});
        return false;
      }
    }

    return true;
  }
}

