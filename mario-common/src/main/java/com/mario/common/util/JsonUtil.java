package com.mario.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.ParseProcess;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.util.IOUtils;
import com.mario.common.config.FastJsonConfigBean;
import com.mario.common.enums.CommonErrCodeEnum;
import com.mario.common.exception.SystemException;
import com.mario.common.threadlocal.SerialNo;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUtil {

  private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);
  private static final SerializerFeature[] defaultFeatures;
  private static FastJsonConfig fastJsonConfig;

  public JsonUtil() {
  }

  public static String toJson(Object obj) {
    return toJson(obj, getFastJsonConfig().getSerializeConfig(),
        getFastJsonConfig().getSerializeFilters(), getFastJsonConfig().getDateFormat(),
        JSON.DEFAULT_GENERATE_FEATURE, getFastJsonConfig().getSerializerFeatures());
  }

  public static Object toJSON(Object javaObject) {
    return toJSON(javaObject, getFastJsonConfig().getSerializeConfig());
  }

  public static Object toJSON(Object javaObject, SerializeConfig config) {
    try {
      return JSON.toJSON(javaObject, config);
    } catch (Throwable var3) {
      log.error("[{}] Object convert to JSON Some Exception Occur:[{}]", SerialNo.getSerialNo(),
          ExceptionUtil.getAsString(var3));
      throw new SystemException(CommonErrCodeEnum.OBJECT_TO_JSON_ERROR);
    }
  }

  public static String toJson(Object object, boolean prettyFormat) {
    return !prettyFormat ? toJson(object) : toJson(object, SerializerFeature.PrettyFormat);
  }

  public static String toJson(Object object, SerializerFeature... features) {
    return toJson(object, JSON.DEFAULT_GENERATE_FEATURE, features);
  }

  public static String toJsonWithDateFormat(Object object, String dateFormat,
      SerializerFeature... features) {
    return toJson(object, getFastJsonConfig().getSerializeConfig(),
        getFastJsonConfig().getSerializeFilters(), dateFormat, JSON.DEFAULT_GENERATE_FEATURE,
        features);
  }

  public static String toJson(Object object, int defaultFeatures, SerializerFeature... features) {
    try {
      return JSON.toJSONString(object, defaultFeatures, features);
    } catch (Throwable var4) {
      log.error("[{}] Object convert to JSON Some Exception Occur:[{}]", SerialNo.getSerialNo(),
          ExceptionUtil.getAsString(var4));
      throw new SystemException(CommonErrCodeEnum.OBJECT_TO_JSON_ERROR);
    }
  }

  public static String toJson(Object object, SerializeFilter[] filters,
      SerializerFeature... features) {
    return toJson(object, getFastJsonConfig().getSerializeConfig(), filters,
        getFastJsonConfig().getDateFormat(), JSON.DEFAULT_GENERATE_FEATURE, features);
  }

  public static String toJsonWithDateFormat(Object object, String dateFormat,
      SerializeFilter[] filters, SerializerFeature... features) {
    return toJson(object, getFastJsonConfig().getSerializeConfig(), filters, dateFormat,
        JSON.DEFAULT_GENERATE_FEATURE, features);
  }

  public static String toJson(Object object, SerializeConfig config,
      SerializerFeature... features) {
    return toJson(object, config, getFastJsonConfig().getSerializeFilters(), features);
  }

  public static String toJsonWithDateFormat(Object object, String dateFormat,
      SerializeConfig config, SerializerFeature... features) {
    return toJsonWithDateFormat(object, dateFormat, config,
        getFastJsonConfig().getSerializeFilters(), features);
  }

  public static String toJson(Object object, SerializeConfig config, SerializeFilter filter,
      SerializerFeature... features) {
    return toJson(object, config, new SerializeFilter[]{filter},
        getFastJsonConfig().getDateFormat(), JSON.DEFAULT_GENERATE_FEATURE, features);
  }

  public static String toJson(Object object, SerializeConfig config, SerializeFilter[] filters,
      SerializerFeature... features) {
    return toJson(object, config, filters, getFastJsonConfig().getDateFormat(),
        JSON.DEFAULT_GENERATE_FEATURE, features);
  }

  public static String toJsonWithDateFormat(Object object, String dateFormat,
      SerializeConfig config, SerializeFilter[] filters, SerializerFeature... features) {
    return toJson(object, config, filters, dateFormat, JSON.DEFAULT_GENERATE_FEATURE, features);
  }

  public static String toJson(Object object, SerializeConfig config, SerializeFilter[] filters,
      String dateFormat, int defaultFeatures, SerializerFeature... features) {
    try {
      return JSON.toJSONString(object, config, filters, dateFormat, defaultFeatures, features);
    } catch (Throwable var7) {
      log.error("[{}] Object convert to JSON Some Exception Occur:[{}]", SerialNo.getSerialNo(),
          ExceptionUtil.getAsString(var7));
      throw new SystemException(CommonErrCodeEnum.OBJECT_TO_JSON_ERROR);
    }
  }

  public static byte[] toJsonBytes(Object object, SerializerFeature... features) {
    return toJsonBytes(object, JSON.DEFAULT_GENERATE_FEATURE, features);
  }

  public static byte[] toJsonBytes(Object object, int defaultFeatures,
      SerializerFeature... features) {
    return toJsonBytes(object, getFastJsonConfig().getSerializeConfig(), defaultFeatures, features);
  }

  public static byte[] toJsonBytes(Object object, SerializeConfig config,
      SerializerFeature... features) {
    return toJsonBytes(object, config, JSON.DEFAULT_GENERATE_FEATURE, features);
  }

  public static byte[] toJsonBytes(Object object, SerializeConfig config, int defaultFeatures,
      SerializerFeature... features) {
    try {
      return JSON.toJSONBytes(object, config, defaultFeatures, features);
    } catch (Throwable var5) {
      log.error("[{}] Object convert to JSON Exception Occur:[{}]", SerialNo.getSerialNo(),
          ExceptionUtil.getAsString(var5));
      throw new SystemException(CommonErrCodeEnum.OBJECT_TO_JSON_ERROR);
    }
  }

  public static void writeJson(Writer writer, Object object, SerializerFeature... features) {
    writeJson(writer, object, JSON.DEFAULT_GENERATE_FEATURE, features);
  }

  public static void writeJson(Writer writer, Object object, int defaultFeatures,
      SerializerFeature... features) {
    try {
      JSON.writeJSONString(writer, object, defaultFeatures, features);
    } catch (Throwable var5) {
      log.error("[{}] Object convert to JSON Exception Occur:[{}]", SerialNo.getSerialNo(),
          ExceptionUtil.getAsString(var5));
      throw new SystemException(CommonErrCodeEnum.OBJECT_TO_JSON_ERROR);
    }
  }

  public static final int writeJson(OutputStream os, Object object, SerializerFeature... features) {
    return writeJson(os, object, JSON.DEFAULT_GENERATE_FEATURE, features);
  }

  public static final int writeJson(OutputStream os, Object object, int defaultFeatures,
      SerializerFeature... features) {
    return writeJson(os, IOUtils.UTF8, object, getFastJsonConfig().getSerializeConfig(),
        getFastJsonConfig().getSerializeFilters(), getFastJsonConfig().getDateFormat(),
        defaultFeatures, features);
  }

  public static final int writeJsonWithDateFormat(OutputStream os, Object object, String dataFormat,
      SerializerFeature... features) throws IOException {
    return writeJson(os, IOUtils.UTF8, object, getFastJsonConfig().getSerializeConfig(),
        getFastJsonConfig().getSerializeFilters(), dataFormat, JSON.DEFAULT_GENERATE_FEATURE,
        features);
  }

  public static final int writeJson(OutputStream os, Charset charset, Object object,
      SerializeConfig config, SerializeFilter[] filters, String dateFormat, int defaultFeatures,
      SerializerFeature... features) {
    try {
      return JSON.writeJSONString(os, charset, object, config, filters, dateFormat, defaultFeatures,
          features);
    } catch (Throwable var9) {
      log.error("[{}] Object convert to JSON Exception Occur:[{}]", SerialNo.getSerialNo(),
          ExceptionUtil.getAsString(var9));
      throw new SystemException(CommonErrCodeEnum.OBJECT_TO_JSON_ERROR);
    }
  }

  public static Object parse(String text) {
    return parse(text, JSON.DEFAULT_PARSER_FEATURE);
  }

  public static Object parse(String text, int features) {
    try {
      return JSON.parse(text, features);
    } catch (Throwable var3) {
      log.error("[{}] JSON convert to Object Exception Occur:[{}]", SerialNo.getSerialNo(),
          ExceptionUtil.getAsString(var3));
      throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
    }
  }

  public static Object parse(String text, Feature... features) {
    try {
      return JSON.parse(text, features);
    } catch (Throwable var3) {
      log.error("[{}] JSON convert to Object Exception Occur:[{}]", SerialNo.getSerialNo(),
          ExceptionUtil.getAsString(var3));
      throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
    }
  }

  public static Object parse(byte[] input, Feature... features) {
    try {
      return JSON.parse(input, features);
    } catch (Throwable var3) {
      log.error("[{}] JSON convert to Object Exception Occur:[{}]", SerialNo.getSerialNo(),
          ExceptionUtil.getAsString(var3));
      throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
    }
  }

  public static JSONObject parseObject(String text, Feature... features) {
    return (JSONObject) parse(text, features);
  }

  public static JSONObject parseObject(String text) {
    Object obj = parse(text);
    if (obj instanceof JSONObject) {
      return (JSONObject) obj;
    } else {
      try {
        return (JSONObject) JSON.toJSON(obj);
      } catch (Throwable var3) {
        log.error("[{}] JSON convert to JSONObject Exception Occur:[{}]", SerialNo.getSerialNo(),
            ExceptionUtil.getAsString(var3));
        throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
      }
    }
  }

  public static <T> T parseObject(String text, TypeReference<T> type, Feature... features) {
    try {
      return JSON.parseObject(text, type, features);
    } catch (Throwable var4) {
      log.error("[{}] JSON convert to JSONObject Exception Occur:[{}]", SerialNo.getSerialNo(),
          ExceptionUtil.getAsString(var4));
      throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
    }
  }

  public static <T> T parseObject(String json, Class<T> clazz, Feature... features) {
    try {
      return JSON.parseObject(json, clazz, features);
    } catch (Throwable var4) {
      log.error("[{}] JSON convert to JSONObject Exception Occur:[{}]", SerialNo.getSerialNo(),
          ExceptionUtil.getAsString(var4));
      throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
    }
  }

  public static <T> T parseObject(String json, Type type, Feature... features) {
    try {
      return JSON.parseObject(json, type, features);
    } catch (Throwable var4) {
      log.error("[{}] JSON convert to JSONObject Exception Occur:[{}]", SerialNo.getSerialNo(),
          ExceptionUtil.getAsString(var4));
      throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
    }
  }

  public static <T> T parseObject(String input, Type clazz, int featureValues,
      Feature... features) {
    try {
      return JSON.parseObject(input, clazz, featureValues, features);
    } catch (Throwable var5) {
      log.error("[{}] JSON convert to JSONObject Exception Occur:[{}]", SerialNo.getSerialNo(),
          ExceptionUtil.getAsString(var5));
      throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
    }
  }

  public static <T> T parseObject(String input, Type clazz, ParserConfig config,
      Feature... features) {
    try {
      return JSON.parseObject(input, clazz, config, features);
    } catch (Throwable var5) {
      log.error("[{}] JSON convert to JSONObject Exception Occur:[{}]", SerialNo.getSerialNo(),
          ExceptionUtil.getAsString(var5));
      throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
    }
  }

  public static <T> T parseObject(String input, Type clazz, ParserConfig config) {
    try {
      return JSON.parseObject(input, clazz, config, getFastJsonConfig().getFeatures());
    } catch (Throwable var4) {
      log.error("[{}] JSON convert to JSONObject Exception Occur:[{}]", SerialNo.getSerialNo(),
          ExceptionUtil.getAsString(var4));
      throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
    }
  }

  public static <T> T parseObject(byte[] bytes, Type clazz, Feature... features) {
    try {
      return JSON.parseObject(bytes, clazz, features);
    } catch (Throwable var4) {
      log.error("[{}] JSON convert to JSONObject Exception Occur:[{}]", SerialNo.getSerialNo(),
          ExceptionUtil.getAsString(var4));
      throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
    }
  }

  public static <K, V> Map<K, V> parseToMap(String json, Class<K> keyType, Class<V> valueType) {
    return (Map) parseObject(json, new TypeReference<Map<K, V>>(new Type[]{keyType, valueType}) {
    });
  }

  public static <T> T fromJson(String text, Class<T> clazz) {
    try {
      return JSON.parseObject(text, clazz);
    } catch (Throwable var3) {
      log.error("[{}] JSON convert to Object Exception Occur:[{}]", SerialNo.getSerialNo(),
          ExceptionUtil.getAsString(var3));
      throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
    }
  }

  public static <T> T fromJson(String text, TypeReference<T> type, Feature... features) {
    try {
      return JSON.parseObject(text, type, features);
    } catch (Throwable var4) {
      log.error("[{}] JSON convert to Object Exception Occur:[{}]", SerialNo.getSerialNo(),
          ExceptionUtil.getAsString(var4));
      throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
    }
  }

  public static <T> T fromJson(String json, Class<T> clazz, Feature... features) {
    try {
      return JSON.parseObject(json, clazz, features);
    } catch (Throwable var4) {
      log.error("[{}] JSON convert to Object Exception Occur:[{}]", SerialNo.getSerialNo(),
          ExceptionUtil.getAsString(var4));
      throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
    }
  }

  public static <T> T fromJson(String text, Class<T> clazz, ParseProcess processor,
      Feature... features) {
    try {
      return JSON.parseObject(text, clazz, processor, features);
    } catch (Throwable var5) {
      log.error("[{}] JSON convert to Object Exception Occur:[{}]", SerialNo.getSerialNo(),
          ExceptionUtil.getAsString(var5));
      throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
    }
  }

  public static <T> T fromJson(String json, Type type, Feature... features) {
    try {
      return JSON.parseObject(json, type, features);
    } catch (Throwable var4) {
      log.error("[{}] JSON convert to Object Exception Occur:[{}]", SerialNo.getSerialNo(),
          ExceptionUtil.getAsString(var4));
      throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
    }
  }

  public static <T> T fromJson(String input, Type clazz, ParseProcess processor,
      Feature... features) {
    try {
      return JSON.parseObject(input, clazz, processor, features);
    } catch (Throwable var5) {
      log.error("[{}] JSON convert to Object Exception Occur:[{}]", SerialNo.getSerialNo(),
          ExceptionUtil.getAsString(var5));
      throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
    }
  }

  public static <T> T fromJson(String input, Type clazz, int featureValues, Feature... features) {
    try {
      return JSON.parseObject(input, clazz, featureValues, features);
    } catch (Throwable var5) {
      log.error("[{}] JSON convert to Object Exception Occur:[{}]", SerialNo.getSerialNo(),
          ExceptionUtil.getAsString(var5));
      throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
    }
  }

  public static <T> T fromJson(String input, Type clazz, ParserConfig config, Feature... features) {
    try {
      return JSON.parseObject(input, clazz, config, features);
    } catch (Throwable var5) {
      log.error("[{}] JSON convert to Object Exception Occur:[{}]", SerialNo.getSerialNo(),
          ExceptionUtil.getAsString(var5));
      throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
    }
  }

  public static <T> T fromJson(String input, Type clazz, ParserConfig config, int featureValues,
      Feature... features) {
    try {
      return JSON.parseObject(input, clazz, config, featureValues, features);
    } catch (Throwable var6) {
      log.error("[{}] JSON convert to Object Exception Occur:[{}]", SerialNo.getSerialNo(),
          ExceptionUtil.getAsString(var6));
      throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
    }
  }

  public static <T> T fromJson(byte[] bytes, Type clazz, Feature... features) {
    try {
      return JSON.parseObject(bytes, clazz, features);
    } catch (Throwable var4) {
      log.error("[{}] JSON convert to Object Exception Occur:[{}]", SerialNo.getSerialNo(),
          ExceptionUtil.getAsString(var4));
      throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
    }
  }

  public static <T> T fromJson(InputStream is, Type type, Feature... features) {
    try {
      return JSON.parseObject(is, type, features);
    } catch (Throwable var4) {
      log.error("[{}] JSON convert to Object Exception Occur:[{}]", SerialNo.getSerialNo(),
          ExceptionUtil.getAsString(var4));
      throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
    }
  }

  public static JSONArray parseArray(String text) {
    try {
      return JSON.parseArray(text);
    } catch (Throwable var2) {
      log.error("[{}] JSON convert to Array Exception Occur:[{}]", SerialNo.getSerialNo(),
          ExceptionUtil.getAsString(var2));
      throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
    }
  }

  public static <T> List<T> parseArray(String text, Class<T> clazz) {
    try {
      return JSON.parseArray(text, clazz);
    } catch (Throwable var3) {
      log.error("[{}] JSON convert to Array Exception Occur:[{}]", SerialNo.getSerialNo(),
          ExceptionUtil.getAsString(var3));
      throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
    }
  }

  public static List<Object> parseArray(String text, Type[] types) {
    try {
      return JSON.parseArray(text, types);
    } catch (Throwable var3) {
      log.error("[{}] JSON convert to Array Exception Occur:[{}]", SerialNo.getSerialNo(),
          ExceptionUtil.getAsString(var3));
      throw new SystemException(CommonErrCodeEnum.JSON_TO_OBJECT_ERROR);
    }
  }

  public static FastJsonConfig getFastJsonConfig() {
    if (fastJsonConfig == null) {
      Class var0 = JsonUtil.class;
      synchronized (JsonUtil.class) {
        if (fastJsonConfig == null) {
          FastJsonConfigBean fastJsonConfigBean = new FastJsonConfigBean();
          fastJsonConfigBean.setEnableDefault(true);
          fastJsonConfigBean.setEnableJsonUtil(false);

          try {
            fastJsonConfigBean.afterPropertiesSet();
          } catch (Exception var4) {
            throw new RuntimeException(var4);
          }

          fastJsonConfig = fastJsonConfigBean;
        }
      }
    }

    return fastJsonConfig;
  }

  public void setFastJsonConfig(FastJsonConfig fastJsonConfig) {
    JsonUtil.fastJsonConfig = fastJsonConfig;
  }

  public static void fastJsonConfig(FastJsonConfig fastJsonConfig) {
    JsonUtil.fastJsonConfig = fastJsonConfig;
  }

  public static SerializerFeature[] getDefaultFeatures() {
    return defaultFeatures;
  }

  static {
    defaultFeatures = new SerializerFeature[]{SerializerFeature.DisableCircularReferenceDetect,
        SerializerFeature.QuoteFieldNames, SerializerFeature.WriteMapNullValue,
        SerializerFeature.WriteBigDecimalAsPlain};
  }
}
