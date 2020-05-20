package com.mario.common.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.mario.common.util.JsonUtil;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.InitializingBean;

public class FastJsonConfigBean extends FastJsonConfig implements InitializingBean {

  private Boolean enableJsonUtil;
  private Boolean enableDefault = true;
  private Set<SerializerFeature> serializerFeatureSet;

  public FastJsonConfigBean() {
    this.serializerFeatureSet = Collections.EMPTY_SET;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    Set<SerializerFeature> newSerializerFeatureSet = new HashSet();
    newSerializerFeatureSet.addAll(this.serializerFeatureSet);
    if (this.enableDefault) {
      newSerializerFeatureSet.addAll(Arrays.asList(JsonUtil.getDefaultFeatures()));
    }

    this.setSerializerFeatures((SerializerFeature[]) newSerializerFeatureSet
        .toArray(new SerializerFeature[newSerializerFeatureSet.size()]));
    if (this.enableJsonUtil == null || this.enableJsonUtil) {
      JsonUtil.fastJsonConfig(this);
    }

  }

  public void addSerializerFeature(SerializerFeature... serializerFeatures) {
    if (this.serializerFeatureSet == Collections.EMPTY_SET) {
      this.serializerFeatureSet = new HashSet();
    }

    SerializerFeature[] var2 = serializerFeatures;
    int var3 = serializerFeatures.length;

    for (int var4 = 0; var4 < var3; ++var4) {
      SerializerFeature serializerFeature = var2[var4];
      this.serializerFeatureSet.add(serializerFeature);
    }

  }

  public void setEnableJsonUtil(Boolean enableJsonUtil) {
    this.enableJsonUtil = enableJsonUtil;
  }

  public void setEnableDefault(Boolean enableDefault) {
    this.enableDefault = enableDefault;
  }

  public Set<SerializerFeature> getSerializerFeatureSet() {
    return this.serializerFeatureSet;
  }
}

