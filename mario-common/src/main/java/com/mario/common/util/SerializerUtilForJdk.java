package com.mario.common.util;

import com.mario.common.constants.CommonConstants;
import com.mario.common.enums.CommonErrCodeEnum;
import com.mario.common.exception.ServiceException;
import com.mario.common.threadlocal.SerialNo;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerializerUtilForJdk {

  private static final Logger log = LoggerFactory.getLogger(SerializerUtilForJdk.class);

  public SerializerUtilForJdk() {
  }

  public static <T> T unserialize(byte[] bytes) {
    if (isEmpty(bytes)) {
      return null;
    } else {
      Object result = null;

      try {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);

        try {
          ObjectInputStream objectInputStream = new ObjectInputStream(byteStream);

          try {
            result = objectInputStream.readObject();
            return (T) result;
          } catch (ClassNotFoundException var5) {
            log.error("[{}] Failed to deserialize object type, Some Exception Occur:[{}]",
                SerialNo.getSerialNo(), ExceptionUtil.getAsString(var5));
            throw new ServiceException(CommonErrCodeEnum.BEAN_CONVERT_ERROR);
          }
        } catch (Throwable var6) {
          log.error("[{}] Failed to deserialize, Some Exception Occur:[{}]", SerialNo.getSerialNo(),
              ExceptionUtil.getAsString(var6));
          throw new ServiceException(CommonErrCodeEnum.BEAN_CONVERT_ERROR);
        }
      } catch (Exception var7) {
        log.error("[{}] Failed to deserialize, Some Exception Occur:[{}]", SerialNo.getSerialNo(),
            ExceptionUtil.getAsString(var7));
        throw new ServiceException(CommonErrCodeEnum.BEAN_CONVERT_ERROR);
      }
    }
  }

  private static boolean isEmpty(byte[] data) {
    return data == null || data.length == 0;
  }

  public static byte[] serialize(Object object) {
    if (object == null) {
      return CommonConstants.EMPTY_BYTES;
    } else {
      Object var1 = null;

      try {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream(128);

        try {
          if (!(object instanceof Serializable)) {
            log.error("[{}] {} requires a Serializable payload but received an object of type [{}]",
                new Object[]{SerialNo.getSerialNo(), SerializerUtilForJdk.class.getSimpleName(),
                    object.getClass().getName()});
            throw new ServiceException(CommonErrCodeEnum.BEAN_CONVERT_ERROR);
          } else {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            byte[] result = byteStream.toByteArray();
            return result;
          }
        } catch (Throwable var4) {
          log.error("[{}] Failed to serialize, Some Exception Occur:[{}]", SerialNo.getSerialNo(),
              ExceptionUtil.getAsString(var4));
          throw new ServiceException(CommonErrCodeEnum.BEAN_CONVERT_ERROR);
        }
      } catch (Exception var5) {
        log.error("[{}] Failed to serialize, Some Exception Occur:[{}]", SerialNo.getSerialNo(),
            ExceptionUtil.getAsString(var5));
        throw new ServiceException(CommonErrCodeEnum.BEAN_CONVERT_ERROR);
      }
    }
  }
}