package com.mario.redis.template;

import com.mario.redis.util.RedisTemplateUtil;
import java.util.Map;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;

public class RedisTemplate<K, V> extends
    org.springframework.data.redis.core.RedisTemplate implements InitializingBean {

  public RedisTemplate() {
    RedisSerializer<String> stringSerializer = new StringRedisSerializer();
    this.setKeySerializer(stringSerializer);
    this.setHashKeySerializer(stringSerializer);
  }

  @Override
  public void afterPropertiesSet() {
    Assert.notNull(this.getHashKeySerializer(), "hashKeySerializer must not null");
    Assert.notNull(this.getValueSerializer(), "valueSerializer must not null");
    super.afterPropertiesSet();
  }

  public void setEnableRedisTemplateUtil(boolean enable) {
    if (enable) {
      RedisTemplateUtil.redisTemplate(this);
    }

  }

  public Map<byte[], byte[]> entries(String mapKey) {
    final byte[] rawKey = this.rawKey(mapKey);
    return (Map) this.execute(new RedisCallback<Map<byte[], byte[]>>() {
      @Override
      public Map<byte[], byte[]> doInRedis(RedisConnection connection) throws DataAccessException {
        return connection.hGetAll(rawKey);
      }
    });
  }

  private byte[] rawKey(Object key) {
    Assert.notNull(key, "non null key required");
    return this.getKeySerializer() == null && key instanceof byte[] ? (byte[]) ((byte[]) key)
        : this.getKeySerializer().serialize(key);
  }
}
