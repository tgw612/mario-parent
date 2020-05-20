package com.mario.redis.serializer;

import com.mario.common.util.SerializerUtil;
import com.sun.xml.internal.ws.encoding.soap.SerializationException;
import org.springframework.data.redis.serializer.RedisSerializer;

public class ProtostuffRedisSerializer implements RedisSerializer {

  public ProtostuffRedisSerializer() {
  }

  @Override
  public byte[] serialize(Object o) throws SerializationException {
    if (o != null) {
      return !(o instanceof byte[]) ? SerializerUtil.serialize(o) : (byte[]) ((byte[]) o);
    } else {
      return null;
    }
  }

  @Override
  public Object deserialize(byte[] bytes) throws SerializationException {
    return bytes != null ? SerializerUtil.unserialize(bytes) : null;
  }
}