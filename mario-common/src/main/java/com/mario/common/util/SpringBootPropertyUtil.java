package com.mario.common.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertyResolver;

public final class SpringBootPropertyUtil {

  private static int springBootVersion = 1;

  public static <T> T handle(Environment environment, String prefix, Class<T> targetClass)
      throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
    switch (springBootVersion) {
      case 1:
        return (T) v1(environment, prefix);
      default:
        return (T) v2(environment, prefix, targetClass);
    }
  }

  private static Object v1(Environment environment, String prefix)
      throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
    try {
      Class<?> resolverClass = Class
          .forName("org.springframework.boot.bind.RelaxedPropertyResolver");
      Constructor<?> resolverConstructor = resolverClass
          .getDeclaredConstructor(PropertyResolver.class);
      Method getSubPropertiesMethod = resolverClass
          .getDeclaredMethod("getSubProperties", String.class);
      Object resolverObject = resolverConstructor.newInstance(environment);
      String prefixParam = prefix.endsWith(".") ? prefix : prefix + ".";
      Method getPropertyMethod = resolverClass.getDeclaredMethod("getProperty", String.class);
      Map<String, Object> dataSourceProps = (Map) getSubPropertiesMethod
          .invoke(resolverObject, prefixParam);
      Map<String, Object> propertiesWithPlaceholderResolved = new HashMap();
      Iterator var10 = dataSourceProps.entrySet().iterator();

      while (true) {
        while (var10.hasNext()) {
          Map.Entry<String, Object> entry = (Map.Entry) var10.next();
          String key = (String) entry.getKey();
          Object value = entry.getValue();
          if (value instanceof String && ((String) value).contains("${")) {
            String resolvedValue = (String) getPropertyMethod
                .invoke(resolverObject, prefixParam + key);
            propertiesWithPlaceholderResolved.put(key, resolvedValue);
          } else {
            propertiesWithPlaceholderResolved.put(key, value);
          }
        }

        return Collections.unmodifiableMap(propertiesWithPlaceholderResolved);
      }
    } catch (Throwable var15) {
      throw var15;
    }
  }

  private static Object v2(Environment environment, String prefix, Class<?> targetClass)
      throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    try {
      Class<?> binderClass = Class
          .forName("org.springframework.boot.context.properties.bind.Binder");
      Method getMethod = binderClass.getDeclaredMethod("get", Environment.class);
      Method bindMethod = binderClass.getDeclaredMethod("bind", String.class, Class.class);
      Object binderObject = getMethod.invoke((Object) null, environment);
      String prefixParam = prefix.endsWith(".") ? prefix.substring(0, prefix.length() - 1) : prefix;
      Object bindResultObject = bindMethod.invoke(binderObject, prefixParam, targetClass);
      Method resultGetMethod = bindResultObject.getClass().getDeclaredMethod("get");
      return resultGetMethod.invoke(bindResultObject);
    } catch (Throwable var10) {
      throw var10;
    }
  }

  private SpringBootPropertyUtil() {
  }

  static {
    try {
      Class.forName("org.springframework.boot.bind.RelaxedPropertyResolver");
    } catch (ClassNotFoundException var1) {
      springBootVersion = 2;
    }

  }
}