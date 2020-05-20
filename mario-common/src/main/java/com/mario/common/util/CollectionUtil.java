package com.mario.common.util;

import com.mario.common.constants.CommonConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import org.apache.commons.beanutils.PropertyUtils;

public abstract class CollectionUtil {

  public CollectionUtil() {
  }

  public static boolean isEmpty(Object object) {
    return object == null;
  }

  public static boolean isEmpty(Boolean b) {
    return !b;
  }

  public static boolean isEmpty(String string) {
    return string == null || string.equals("");
  }

  public static boolean isEmpty(Long l) {
    return l == null || l < 1L;
  }

  public static boolean isEmpty(Integer i) {
    return i == null || i < 1;
  }

  public static boolean isEmpty(Collection collection) {
    return collection == null || collection.isEmpty();
  }

  public static boolean isEmpty(Map map) {
    return map == null || map.isEmpty();
  }

  public static boolean isNotEmpty(Collection<?> collection) {
    return !isEmpty(collection);
  }

  public static boolean isNotEmpty(Map<?, ?> map) {
    return !isEmpty(map);
  }

  public static List arrayToList(Object source) {
    return Arrays.asList(ObjectUtil.toObjectArray(source));
  }

  public static <E> void mergeArrayIntoCollection(Object array, Collection<E> collection) {
    if (array == null) {
      throw new IllegalArgumentException("array must not be null");
    } else {
      Object[] objs = ObjectUtil.toObjectArray(array);
      Object[] var3 = objs;
      int var4 = objs.length;

      for (int var5 = 0; var5 < var4; ++var5) {
        Object obj = var3[var5];
        collection.add((E) obj);
      }

    }
  }

  public static <K, V> void mergePropertiesIntoMap(Properties props, Map<String, Object> map) {
    if (map == null) {
      throw new IllegalArgumentException("Map must not be null");
    } else {
      String key;
      Object value;
      if (props != null) {
        for (Enumeration en = props.propertyNames(); en.hasMoreElements(); map.put(key, value)) {
          key = (String) en.nextElement();
          value = props.getProperty(key);
          if (value == null) {
            value = props.get(key);
          }
        }
      }

    }
  }

  public static boolean contains(Iterator<?> iterator, Object element) {
    if (iterator != null) {
      while (iterator.hasNext()) {
        Object candidate = iterator.next();
        if (ObjectUtil.nullSafeEquals(candidate, element)) {
          return true;
        }
      }
    }

    return false;
  }

  public static boolean contains(Enumeration<?> enumeration, Object element) {
    if (enumeration != null) {
      while (enumeration.hasMoreElements()) {
        Object candidate = enumeration.nextElement();
        if (ObjectUtil.nullSafeEquals(candidate, element)) {
          return true;
        }
      }
    }

    return false;
  }

  public static boolean containsInstance(Collection<?> collection, Object element) {
    if (collection != null) {
      Iterator var2 = collection.iterator();

      while (var2.hasNext()) {
        Object candidate = var2.next();
        if (candidate == element) {
          return true;
        }
      }
    }

    return false;
  }

  public static boolean containsAny(Collection<?> source, Collection<?> candidates) {
    if (!isEmpty(source) && !isEmpty(candidates)) {
      Iterator var2 = candidates.iterator();

      Object candidate;
      do {
        if (!var2.hasNext()) {
          return false;
        }

        candidate = var2.next();
      } while (!source.contains(candidate));

      return true;
    } else {
      return false;
    }
  }

  public static <T> String toString(T[] beans) {
    if (beans != null && beans.length != 0) {
      StringBuilder stringBuilder = new StringBuilder(100);
      stringBuilder.append("[");
      Object[] var2 = beans;
      int var3 = beans.length;

      for (int var4 = 0; var4 < var3; ++var4) {
        T bean = (T) var2[var4];
        stringBuilder.append(bean.toString());
        stringBuilder.append(",");
      }

      return stringBuilder.substring(0, stringBuilder.length() - 1) + "]";
    } else {
      return "[]";
    }
  }

  public static <T> String toString(Collection<T> beans) {
    if (beans != null && beans.size() != 0) {
      StringBuilder stringBuilder = new StringBuilder(100);
      stringBuilder.append("[");
      Iterator var2 = beans.iterator();

      while (var2.hasNext()) {
        T bean = (T) var2.next();
        stringBuilder.append(bean.toString());
        stringBuilder.append(",");
      }

      return stringBuilder.substring(0, stringBuilder.length() - 1) + "]";
    } else {
      return "[]";
    }
  }

  public static <T> String toString(Map<String, Object> map) {
    if (isEmpty(map)) {
      return "{}";
    } else {
      StringBuilder stringBuilder = new StringBuilder(100);
      stringBuilder.append("{");
      Iterator var2 = map.entrySet().iterator();

      while (var2.hasNext()) {
        Entry<String, Object> entry = (Entry) var2.next();
        stringBuilder.append((String) entry.getKey());
        stringBuilder.append("=");
        stringBuilder.append(entry.getValue().toString());
        stringBuilder.append(",");
      }

      return stringBuilder.substring(0, stringBuilder.length() - 1) + "}";
    }
  }

  public static <T> String toString(Map<String, Object>[] paramMapArr) {
    if (paramMapArr != null && paramMapArr.length != 0) {
      StringBuilder stringBuilder = new StringBuilder(100);
      stringBuilder.append("[");
      Map[] var2 = paramMapArr;
      int var3 = paramMapArr.length;

      for (int var4 = 0; var4 < var3; ++var4) {
        Map<String, Object> map = var2[var4];
        stringBuilder.append(toString(map));
      }

      return stringBuilder.substring(0, stringBuilder.length() - 1) + "]";
    } else {
      return "[]";
    }
  }

  public static Map extractToMap(Collection collection, String keyPropertyName,
      String valuePropertyName) {
    HashMap map = new HashMap(collection.size());

    try {
      Iterator var4 = collection.iterator();

      while (var4.hasNext()) {
        Object obj = var4.next();
        map.put(PropertyUtils.getProperty(obj, keyPropertyName),
            PropertyUtils.getProperty(obj, valuePropertyName));
      }

      return map;
    } catch (Exception var6) {
      throw ReflectionUtil.convertReflectionExceptionToUnchecked(var6);
    }
  }

  public static List extractToList(Collection collection, String propertyName) {
    ArrayList list = new ArrayList(collection.size());

    try {
      Iterator var3 = collection.iterator();

      while (var3.hasNext()) {
        Object obj = var3.next();
        list.add(PropertyUtils.getProperty(obj, propertyName));
      }

      return list;
    } catch (Exception var5) {
      throw ReflectionUtil.convertReflectionExceptionToUnchecked(var5);
    }
  }

  public static String extractToString(Collection collection, String propertyName,
      String separator) {
    List list = extractToList(collection, propertyName);
    return StringUtil.join(list, separator);
  }

  public static String convertToString(Collection collection, String separator) {
    return StringUtil.join(collection, separator);
  }

  public static String convertToString(Collection collection, String prefix, String postfix) {
    StringBuilder builder = new StringBuilder();
    Iterator var4 = collection.iterator();

    while (var4.hasNext()) {
      Object o = var4.next();
      builder.append(prefix).append(o).append(postfix);
    }

    return builder.toString();
  }

  public static String convertToString(Collection collection, String separator, String prefix,
      String postfix) {
    if (isNotEmpty(collection)) {
      StringBuilder builder = new StringBuilder();
      collection.stream().forEach((o) -> {
        builder.append(prefix).append(o).append(postfix).append(separator);
      });
      return builder.toString().substring(0, builder.length() - separator.length());
    } else {
      return "";
    }
  }

  public static <T> T getFirst(Collection<T> collection) {
    return isEmpty(collection) ? null : collection.iterator().next();
  }

  public static <T> T getLast(Collection<T> collection) {
    if (isEmpty(collection)) {
      return null;
    } else if (collection instanceof List) {
      List<T> list = (List) collection;
      return list.get(list.size() - 1);
    } else {
      Iterator iterator = collection.iterator();

      Object current;
      do {
        current = iterator.next();
      } while (iterator.hasNext());

      return (T) current;
    }
  }

  public static <T> List<T> union(Collection<T> a, Collection<T> b) {
    List<T> result = new ArrayList(a);
    result.addAll(b);
    return result;
  }

  public static <T> List<T> subtract(Collection<T> a, Collection<T> b) {
    List<T> list = new ArrayList(a);
    Iterator var3 = b.iterator();

    while (var3.hasNext()) {
      T element = (T) var3.next();
      list.remove(element);
    }

    return list;
  }

  public static <T> List<T> intersection(Collection<T> a, Collection<T> b) {
    List<T> list = new ArrayList();
    Iterator var3 = a.iterator();

    while (var3.hasNext()) {
      T element = (T) var3.next();
      if (b.contains(element)) {
        list.add(element);
      }
    }

    return list;
  }

  public static <T> List<List<T>> split(List<T> resList, int count) {
    if (resList != null && count >= 1) {
      List<List<T>> ret = new ArrayList();
      int size = resList.size();
      if (size <= count) {
        ret.add(resList);
      } else {
        int pre = size / count;
        int last = size % count;

        for (int i = 0; i < pre; ++i) {
          List<T> itemList = new ArrayList();

          for (int j = 0; j < count; ++j) {
            itemList.add(resList.get(i * count + j));
          }

          ret.add(itemList);
        }

        if (last > 0) {
          List<T> itemList = new ArrayList();

          for (int i = 0; i < last; ++i) {
            itemList.add(resList.get(pre * count + i));
          }

          ret.add(itemList);
        }
      }

      return ret;
    } else {
      return CommonConstants.EMPTY_LIST;
    }
  }
}

