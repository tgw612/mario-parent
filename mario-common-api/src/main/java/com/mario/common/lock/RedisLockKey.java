package com.mario.common.lock;

import com.mario.common.enums.BaseEnum;

public interface RedisLockKey extends BaseEnum<String> {

  int DEFAULT_EXPIRE = 50;

  String get(Object var1);

  default int getExpireTimeSecond() {
    return 50;
  }
}
