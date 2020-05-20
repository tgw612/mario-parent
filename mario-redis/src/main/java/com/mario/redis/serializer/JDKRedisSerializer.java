package com.mario.redis.serializer;

import com.mario.common.util.SerializerUtilForJdk;
import com.sun.xml.internal.ws.encoding.soap.SerializationException;
import org.springframework.data.redis.serializer.RedisSerializer;

public class JDKRedisSerializer implements RedisSerializer {

  public JDKRedisSerializer() {
  }

  @Override
  public byte[] serialize(Object o) throws SerializationException {
    if (o != null) {
      return !(o instanceof byte[]) ? SerializerUtilForJdk.serialize(o) : (byte[]) ((byte[]) o);
    } else {
      return null;
    }
  }

  @Override
  public Object deserialize(byte[] bytes) throws SerializationException {
    return bytes != null ? SerializerUtilForJdk.unserialize(bytes) : null;
  }
}