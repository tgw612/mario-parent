package com.mario.common.util;

import java.util.Arrays;
import java.util.List;

/**
 * 枚举工具
 *
 * @author huminghe
 * @create 2017/4/5
 */
public abstract class EnumUtil {

  public EnumUtil() {
  }

  public static <T extends Enum<T>> T fromEnumValue(Class<T> enumClass, String property,
      Object propValue) {
    T[] enumConstants = enumClass.getEnumConstants();
    String methodName = "get" + StringUtil.capitalize(property);
    Enum[] var5 = enumConstants;
    int var6 = enumConstants.length;

    for (int var7 = 0; var7 < var6; ++var7) {
      T t = (T) var5[var7];

      try {
        Object constantPropValue = t.getDeclaringClass().getDeclaredMethod(methodName, new Class[0])
            .invoke(t, new Object[0]);
        if (constantPropValue.equals(propValue)) {
          return t;
        }
      } catch (Exception var11) {
        throw new RuntimeException(var11);
      }
    }

    throw new EnumConstantNotPresentException(enumClass, String.valueOf(propValue));
  }

  public static String getEnumProperty(Enum enumObject, String property) {
    String methodName = "get" + StringUtil.capitalize(property);

    Object constantPropValue;
    try {
      constantPropValue = enumObject.getDeclaringClass().getDeclaredMethod(methodName, new Class[0])
          .invoke(enumObject, new Object[0]);
    } catch (Exception var5) {
      throw new RuntimeException(String.format("could not found settable filed [%s] in [%s]",
          new Object[]{property, enumObject.getDeclaringClass()}), var5);
    }

    return constantPropValue.toString();
  }

  public static String getEnumDesc(Enum enumObject) {
    return getEnumProperty(enumObject, "desc");
  }

  public static <T extends Enum<T>> String getEnumDescByCode(String code, Class<T> clazz) {
    Enum enumObject = null;

    try {
      enumObject = fromEnumValue(clazz, "code", code.trim());
    } catch (EnumConstantNotPresentException var4) {
      return code;
    }

    return getEnumDesc(enumObject);
  }

  public static <T extends Enum<T>> T resolveFromCode(String code, Class<T> clazz) {
    return fromEnumValue(clazz, "code", code.trim());
  }

  public static <T extends Enum> List<T> toList(Class<T> clazz) {
    return Arrays.asList(clazz.getEnumConstants());
  }
}
