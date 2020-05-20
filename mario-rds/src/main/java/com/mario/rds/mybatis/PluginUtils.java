package com.mario.rds.mybatis;

import java.lang.reflect.Proxy;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

public class PluginUtils {

  public PluginUtils() {
  }

  public static Object realTarget(Object target) {
    if (Proxy.isProxyClass(target.getClass())) {
      MetaObject metaObject = SystemMetaObject.forObject(target);
      return realTarget(metaObject.getValue("h.target"));
    } else {
      return target;
    }
  }
}

