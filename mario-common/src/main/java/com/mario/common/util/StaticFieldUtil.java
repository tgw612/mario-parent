package com.mario.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class StaticFieldUtil {

  public StaticFieldUtil() {
  }

  public static Object getInstance(String staticField) {
    if (staticField == null) {
      return null;
    } else {
      int lastDotIndex = staticField.lastIndexOf(46);
      if (lastDotIndex != -1 && lastDotIndex != staticField.length()) {
        String className = staticField.substring(0, lastDotIndex);
        String fieldName = staticField.substring(lastDotIndex + 1);
        Class targetClass = null;

        try {
          targetClass = Class.forName(className);
          Field fieldObject = targetClass.getField(fieldName);
          if (fieldObject == null) {
            throw new IllegalArgumentException(
                "staticField must be a fully qualified class plus static field name: e.g. 'example.MyExampleClass.MY_EXAMPLE_FIELD'");
          } else {
            makeAccessible(fieldObject);
            return fieldObject.get((Object) null);
          }
        } catch (Exception var6) {
          throw new RuntimeException(var6.getMessage(), var6);
        }
      } else {
        throw new IllegalArgumentException(
            "staticField must be a fully qualified class plus static field name: e.g. 'example.MyExampleClass.MY_EXAMPLE_FIELD'");
      }
    }
  }

  public static void makeAccessible(Field field) {
    if ((!Modifier.isPublic(field.getModifiers()) || !Modifier
        .isPublic(field.getDeclaringClass().getModifiers()) || Modifier
        .isFinal(field.getModifiers())) && !field.isAccessible()) {
      field.setAccessible(true);
    }

  }
}
