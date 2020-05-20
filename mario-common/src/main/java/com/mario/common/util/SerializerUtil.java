package com.mario.common.util;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.mario.common.enums.CommonErrCodeEnum;
import com.mario.common.exception.ServiceException;
import com.mario.common.threadlocal.SerialNo;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerializerUtil {

  private static final Logger log = LoggerFactory.getLogger(SerializerUtil.class);
  private static ConcurrentHashMap<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap();

  public SerializerUtil() {
  }

  public static <T> byte[] serialize(T source) {
    SerializerUtil.VO<T> vo = new SerializerUtil.VO(source);
    LinkedBuffer buffer = LinkedBuffer.allocate(512);

    byte[] var4;
    try {
      Schema<SerializerUtil.VO> schema = getSchema(SerializerUtil.VO.class);
      var4 = serializeInternal(vo, schema, buffer);
    } catch (Throwable var8) {
      log.error("[{}] [{}] Finish handling .\nSome Exception Occur:[{}]",
          new Object[]{SerialNo.getSerialNo(), SerializerUtil.class.getName(),
              ExceptionUtil.getAsString(var8)});
      throw new ServiceException(CommonErrCodeEnum.BEAN_CONVERT_ERROR);
    } finally {
      buffer.clear();
    }

    return var4;
  }

  public static <T> T unserialize(byte[] bytes) {
    try {
      Schema<SerializerUtil.VO> schema = getSchema(SerializerUtil.VO.class);
      SerializerUtil.VO vo = (SerializerUtil.VO) deserializeInternal(bytes, schema.newMessage(),
          schema);
      return vo != null && vo.getValue() != null ? (T) vo.getValue() : null;
    } catch (Throwable var3) {
      log.error("[{}] [{}] Finish handling .\nSome Exception Occur:[{}]",
          new Object[]{SerialNo.getSerialNo(), SerializerUtil.class.getName(),
              ExceptionUtil.getAsString(var3)});
      throw new ServiceException(CommonErrCodeEnum.BEAN_CONVERT_ERROR);
    }
  }

  private static <T> byte[] serializeInternal(T source, Schema<T> schema, LinkedBuffer buffer) {
    return ProtostuffIOUtil.toByteArray(source, schema, buffer);
  }

  private static <T> T deserializeInternal(byte[] bytes, T result, Schema<T> schema) {
    ProtostuffIOUtil.mergeFrom(bytes, result, schema);
    return result;
  }

  private static <T> Schema<T> getSchema(Class<T> clazz) {
    Schema<T> schema = (Schema) cachedSchema.get(clazz);
    if (schema == null) {
      schema = RuntimeSchema.createFrom(clazz);
      cachedSchema.put(clazz, schema);
    }

    return (Schema) schema;
  }

  private static class VO<T> implements Serializable {

    private T value;

    public VO(T value) {
      this.value = value;
    }

    public VO() {
    }

    public T getValue() {
      return this.value;
    }

    public String toString() {
      return "VO{value=" + this.value + '}';
    }
  }
}