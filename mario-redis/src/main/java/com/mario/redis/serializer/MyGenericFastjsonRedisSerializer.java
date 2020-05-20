package com.mario.redis.serializer;

import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import com.alibaba.fastjson.util.IOUtils;
import com.mario.common.util.JsonUtil;
import com.sun.xml.internal.ws.encoding.soap.SerializationException;

public class MyGenericFastjsonRedisSerializer extends GenericFastJsonRedisSerializer {

  private ParserConfig parserConfig;
  private SerializeConfig serializeConfig;
  private SerializerFeature[] serializerFeatures;
  private Feature[] features;

  public MyGenericFastjsonRedisSerializer(SerializerFeature... serializerFeatures) {
    this((SerializeConfig) null, (ParserConfig) null, serializerFeatures);
  }

  public MyGenericFastjsonRedisSerializer(SerializeConfig serializeConfig,
      SerializerFeature... serializerFeatures) {
    this(serializeConfig, (ParserConfig) null, serializerFeatures);
  }

  public MyGenericFastjsonRedisSerializer(ParserConfig parserConfig,
      SerializerFeature... serializerFeatures) {
    this((SerializeConfig) null, parserConfig, serializerFeatures);
  }

  public MyGenericFastjsonRedisSerializer(SerializeConfig serializeConfig,
      ParserConfig parserConfig, SerializerFeature... serializerFeatures) {
    this(serializeConfig, parserConfig, (Feature[]) null, serializerFeatures);
  }

  public MyGenericFastjsonRedisSerializer(SerializeConfig serializeConfig,
      ParserConfig parserConfig, Feature[] features, SerializerFeature... serializerFeatures) {
    this.features = new Feature[0];
    this.initSerializerFeatures(serializerFeatures);
    this.initSerializeConfig(serializeConfig);
    this.initParserConfig(parserConfig);
    this.features = features;
  }

  private void initSerializeConfig(SerializeConfig serializeConfig) {
    if (serializeConfig == null) {
      serializeConfig = new SerializeConfig();
    }

    this.serializeConfig = serializeConfig;
  }

  private void initParserConfig(ParserConfig parserConfig) {
    if (parserConfig == null) {
      parserConfig = new ParserConfig();
    }

    this.parserConfig = parserConfig;
    this.parserConfig.setAutoTypeSupport(true);
  }

  private void initSerializerFeatures(SerializerFeature[] features) {
    if (features != null && features.length > 0) {
      this.serializerFeatures = new SerializerFeature[features.length + 1];
      System.arraycopy(features, 0, this.serializerFeatures, 0, features.length);
      this.serializerFeatures[features.length] = SerializerFeature.WriteClassName;
    } else {
      this.serializerFeatures = new SerializerFeature[1];
      this.serializerFeatures[0] = SerializerFeature.WriteClassName;
    }

  }

  @Override
  public byte[] serialize(Object object) throws SerializationException {
    return object == null ? new byte[0]
        : JsonUtil.toJsonBytes(object, this.serializeConfig, this.serializerFeatures);
  }

  @Override
  public Object deserialize(byte[] bytes) throws SerializationException {
    return bytes != null && bytes.length != 0 ? JsonUtil
        .parseObject(new String(bytes, IOUtils.UTF8), Object.class, this.parserConfig,
            this.features) : null;
  }

  public ParserConfig getParserConfig() {
    return this.parserConfig;
  }

  public SerializeConfig getSerializeConfig() {
    return this.serializeConfig;
  }

  public SerializerFeature[] getSerializerFeatures() {
    return this.serializerFeatures;
  }

  public void setFeatures(Feature[] features) {
    this.features = features;
  }

  public Feature[] getFeatures() {
    return this.features;
  }
}
