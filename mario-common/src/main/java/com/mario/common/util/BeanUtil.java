package com.mario.common.util;

import com.mario.common.enums.CommonErrCodeEnum;
import com.mario.common.exception.ServiceException;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BeanUtil {

  private static final Logger log = LoggerFactory.getLogger(BeanUtil.class);

  public BeanUtil() {
  }

  public static <M> M createInstance(Class<M> cls) {
    try {
      return cls.newInstance();
    } catch (InstantiationException var2) {
      log.error("[{}] Finish handling .\nSome Exception Occur:[{}]", BeanUtil.class.getName(),
          ExceptionUtil.getAsString(var2));
      throw new ServiceException(CommonErrCodeEnum.CREATOBJ_ERROR);
    } catch (IllegalAccessException var3) {
      log.error("[{}] Finish handling .\nSome Exception Occur:[{}]", BeanUtil.class.getName(),
          ExceptionUtil.getAsString(var3));
      throw new ServiceException(CommonErrCodeEnum.CREATOBJ_ERROR);
    }
  }

  public static List<Type> getActualType(Class cls) {
    List<Type> list = new ArrayList();
    ParameterizedType clsType = (ParameterizedType) cls.getGenericSuperclass();
    Type[] var3 = clsType.getActualTypeArguments();
    int var4 = var3.length;

    for (int var5 = 0; var5 < var4; ++var5) {
      Type t1 = var3[var5];
      list.add(t1);
    }

    return list;
  }

  public static Class getFirstActualType(Class cls) {
    List<Type> list = getActualType(cls);
    return list != null && list.size() != 0 ? (Class) list.get(0) : null;
  }

  public static Object getFieldValue(Object obj, String fieldName) {
    Object value = null;
    Object source = obj;
    if (obj != null && StringUtil.isNotBlank(fieldName)) {
      try {
        String shortField = fieldName;

        while (StringUtil.isNotBlank(shortField)) {
          String firstFiled = null;
          if (shortField.contains(".")) {
            firstFiled = shortField.trim().substring(0, shortField.indexOf(46));
            shortField = shortField.trim().substring(shortField.indexOf(46) + 1);
          } else {
            firstFiled = shortField.trim();
            shortField = "";
          }

          if (!StringUtil.isNotBlank(firstFiled)) {
            break;
          }

          Class cls = source.getClass();

          try {
            Field field = cls.getDeclaredField(firstFiled.trim());
            if (field != null) {
              field.setAccessible(true);
              source = field.get(source);
            }
          } catch (NoSuchFieldException var10) {
            try {
              Method method = cls.getMethod(
                  "get" + String.valueOf(firstFiled.trim().charAt(0)).toUpperCase() + firstFiled
                      .trim().substring(1));
              if (method != null) {
                method.setAccessible(true);
                source = method.invoke(source, (Object[]) null);
              }
            } catch (NoSuchMethodException var9) {
              log.error("[{}] Finish handling .\nSome Exception Occur:[{}]",
                  BeanUtil.class.getName(), ExceptionUtil.getAsString(var10));
              throw new ServiceException(CommonErrCodeEnum.BEAN_VLUE_ERROR);
            }
          }
        }

        value = source;
      } catch (IllegalAccessException var11) {
        log.error("[{}] Finish handling .\nSome Exception Occur:[{}]", BeanUtil.class.getName(),
            ExceptionUtil.getAsString(var11));
        throw new ServiceException(CommonErrCodeEnum.BEAN_VLUE_ERROR);
      } catch (InvocationTargetException var12) {
        log.error("[{}] Finish handling .\nSome Exception Occur:[{}]", BeanUtil.class.getName(),
            ExceptionUtil.getAsString(var12));
      }
    }

    return value;
  }

  public static Object transMap2Bean(Map<String, Object> map, Object obj) {
    try {
      BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
      PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
      PropertyDescriptor[] var4 = propertyDescriptors;
      int var5 = propertyDescriptors.length;

      for (int var6 = 0; var6 < var5; ++var6) {
        PropertyDescriptor property = var4[var6];
        String key = property.getName();
        if (map.containsKey(key)) {
          Object value = map.get(key);
          Method setter = property.getWriteMethod();
          setter.invoke(obj, value);
        }
      }

      return obj;
    } catch (Exception var11) {
      log.error("[{}] Finish handling .\nSome Exception Occur:[{}]", BeanUtil.class.getName(),
          ExceptionUtil.getAsString(var11));
      throw new ServiceException(CommonErrCodeEnum.BEAN_CONVERT_ERROR);
    }
  }

  public static Map<String, Object> transBean2Map(Object obj) {
    if (obj == null) {
      return null;
    } else {
      HashMap map = new HashMap();

      try {
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        PropertyDescriptor[] var4 = propertyDescriptors;
        int var5 = propertyDescriptors.length;

        for (int var6 = 0; var6 < var5; ++var6) {
          PropertyDescriptor property = var4[var6];
          String key = property.getName();
          if (!key.equals("class")) {
            Method getter = property.getReadMethod();
            Object value = getter.invoke(obj);
            map.put(key, value);
          }
        }

        return map;
      } catch (Exception var11) {
        log.error("[{}] Finish handling .\nSome Exception Occur:[{}]", BeanUtil.class.getName(),
            ExceptionUtil.getAsString(var11));
        throw new ServiceException(CommonErrCodeEnum.BEAN_CONVERT_ERROR);
      }
    }
  }
}