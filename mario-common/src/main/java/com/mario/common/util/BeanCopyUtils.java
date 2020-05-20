package com.mario.common.util;

import com.mario.common.exception.ServiceException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.util.CollectionUtils;

/**
 * Description:bean 拷贝 Author: wei Date：2017/12/13
 */
public class BeanCopyUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(BeanCopyUtils.class);

  /**
   * 拷贝list
   *
   * @param dest
   * @param orig
   * @param <T>
   * @return
   */
  public static <T> List<T> copyList(Class<T> dest, List orig) {
    if (CollectionUtils.isEmpty(orig)) {
      return new ArrayList<>();
    }
    BeanCopier beanCopier = BeanCopier.create(orig.get(0).getClass(), dest, false);
    List<T> resultList = new ArrayList<>(orig.size());
    try {
      for (Object o : orig) {
        if (o == null) {
          continue;
        }

        T destObject = dest.newInstance();

        beanCopier.copy(o, destObject, null);
        resultList.add(destObject);
      }
      return resultList;
    } catch (Exception e) {
      LOGGER.error("copyList error", e);
    }
    return resultList;
  }

  /**
   * 拷贝list
   *
   * @param dest
   * @param orig
   * @return
   */
  public static <D, O> List<D> copyList(Class<D> dest, List<O> orig, Consumer<D> d) {
    if (CollectionUtils.isEmpty(orig)) {
      return new ArrayList<>();
    }
    BeanCopier beanCopier = BeanCopier.create(orig.get(0).getClass(), dest, false);
    List<D> resultList = new ArrayList<>(orig.size());
    try {
      for (Object o : orig) {
        if (o == null) {
          continue;
        }

        D destObject = dest.newInstance();

        beanCopier.copy(o, destObject, null);
        if (d != null) {
          d.accept(destObject);
        }
        resultList.add(destObject);
      }
    } catch (Exception e) {
      LOGGER.error("copyList error", e);
    }
    return resultList;
  }

  /**
   * 拷贝List并且 明细也能拷贝(常用与 1里面包含明细数据 进行拷贝)
   *
   * @param dest         目标
   * @param orig         源
   * @param transformKey 转换
   * @param <T>
   * @return
   */
  public static <T> List<T> copyListAndDetail(Class<T> dest,
      List orig,
      Map<String, String> transformKey) {
    return copyListAndDetail(dest, orig, transformKey, null);
  }

  /**
   * 拷贝List并且 明细也能拷贝(常用与 1里面包含明细数据 进行拷贝)
   *
   * @param dest
   * @param orig
   * @param transformKey
   * @param consumer
   * @return
   */
  public static <T, C> List<T> copyListAndDetail(Class<T> dest,
      List orig,
      Map<String, String> transformKey,
      BiConsumer<T, C> consumer) {
    try {
      List<T> resultList = new ArrayList<>();
      if (orig == null || orig.size() <= 0) {
        return resultList;
      }
      Field[] originFields = null;
      Field[] destFields = null;
      int i = 0;
      if (!CollectionUtils.isEmpty(transformKey)) {
        originFields = new Field[transformKey.size()];
        destFields = new Field[transformKey.size()];
        Class origClass = orig.get(0).getClass();
        Set<Map.Entry<String, String>> entries = transformKey.entrySet();
        for (Iterator<Map.Entry<String, String>> iterator = entries.iterator(); iterator.hasNext();
            i++) {
          Map.Entry<String, String> entry = iterator.next();
          String key = entry.getKey();//源
          String value = entry.getValue();//目标
          originFields[i] = origClass.getDeclaredField(key);
          destFields[i] = dest.getDeclaredField(value);
        }
        //私有属性 允许访问
        AccessibleObject.setAccessible(originFields, true);
        AccessibleObject.setAccessible(destFields, true);
      }

      for (Object o : orig) {
        T destObject = dest.newInstance();
        if (o == null) {
          continue;
        }
        copyProperties(destObject, o);

        for (int a = 0; a < i; a++) {
          //源字段
          Field originField = originFields[a];
          //源里面的值
          List o1 = (List) originField.get(o);
          //目标字段
          Field destField = destFields[a];
          Class childClass = (Class) ((ParameterizedType) destField.getGenericType())
              .getActualTypeArguments()[0];

          List destList = copyList(childClass, o1, destObject, consumer);

          destField.set(destObject, destList);
        }
        resultList.add(destObject);
      }
      if (originFields != null && destFields != null) {
        //私有属性 不允许访问
        AccessibleObject.setAccessible(originFields, false);
        AccessibleObject.setAccessible(destFields, false);
      }
      return resultList;
    } catch (Exception e) {
      LOGGER.error("copyListAndDetail error", e);
      return null;
    }
  }

  /**
   * 拷贝list
   *
   * @param dest
   * @param orig
   * @param out      (相当于 一对多环境中的 一)
   * @param consumer
   * @param <T>
   * @return
   */
  private static <T, O> List<T> copyList(Class<T> dest, List orig, O out,
      BiConsumer<O, T> consumer) {
    if (CollectionUtils.isEmpty(orig)) {
      return new ArrayList<>();
    }
    BeanCopier beanCopier = BeanCopier.create(orig.get(0).getClass(), dest, false);
    List<T> resultList = new ArrayList<>(orig.size());
    try {
      for (Object o : orig) {
        if (o == null) {
          continue;
        }

        T destObject = dest.newInstance();
        beanCopier.copy(o, destObject, null);
        resultList.add(destObject);
        if (out != null && consumer != null) {
          consumer.accept(out, destObject);
        }
      }
      return resultList;
    } catch (Exception e) {
      LOGGER.error("copyList error", e);
    }
    return resultList;
  }


  /**
   * 多个orig对象 属性 浅拷贝到 dest
   *
   * @param dest
   * @param orig
   * @param <T>
   * @return
   */
  public static <T> T copyProperties(Class<T> dest, Object... orig) {
    if (orig == null) {
      return null;
    }
    try {
      T destObject = dest.newInstance();
      for (Object o : orig) {
        copyProperties(destObject, o);
      }
      return destObject;
    } catch (Exception e) {
      LOGGER.error("copyProperties error", e);
      return null;
    }
  }

  /**
   * 拷贝对象
   *
   * @param dest
   * @param orig
   * @param <T>
   * @return
   */
  public static <T> T copyProperties(Class<T> dest, Object orig) {
    if (orig == null) {
      return null;
    }
    try {
      T destObject = dest.newInstance();
      copyProperties(destObject, orig);
      return destObject;
    } catch (Exception e) {
      LOGGER.error("copyProperties error", e);
      return null;
    }
  }

  /**
   * 使用 org.springframework.beans.BeanUtils 拷贝对象
   *
   * @param dest
   * @param orig
   */
  public static void copyProperties(Object dest, Object orig) {
    try {
      BeanCopier copier = BeanCopier.create(orig.getClass(), dest.getClass(), false);
      copier.copy(orig, dest, null);
    } catch (Exception e) {
      LOGGER.error("copyProperties error", e);
    }
  }


  /**
   * 根据指定名称键值对,填充javaBean对象。
   *
   * @param bean
   * @param properties
   */
  public static void populate(Object bean, Map properties) {
    try {
      BeanMap beanMap = BeanMap.create(bean);
      beanMap.putAll(properties);
    } catch (Exception e) {
      LOGGER.error("populate bean property error", e);
    }
  }

  /**
   * 根据指定名称键值对,填充javaBean对象。
   *
   * @param c
   * @param properties
   */
  public static <T> T populate(Class<T> c, Map properties) {
    try {
      T o = c.newInstance();
      BeanMap beanMap = BeanMap.create(o);
      beanMap.putAll(properties);
      return (T) beanMap.getBean();
    } catch (Exception e) {
      LOGGER.error("populate bean property error", e);
    }
    return null;
  }

  /**
   * 根据指定名称键值对,填充javaBean对象。
   *
   * @param dest
   * @param orig
   */
  public static <T> List<T> populateList(Class<T> dest, List<Map<String, Object>> orig) {
    if (CollectionUtils.isEmpty(orig)) {
      return new ArrayList<>();
    }

    List<T> resultList = new ArrayList<>(orig.size());
    try {
      BeanMap beanMap = BeanMap.create(dest.newInstance());
      for (Map o : orig) {
        if (o == null) {
          continue;
        }

        T destObject = dest.newInstance();
        beanMap.setBean(destObject);
        beanMap.putAll(o);
        resultList.add(destObject);
      }
      return resultList;
    } catch (Exception e) {
      LOGGER.error("copyList error", e);
    }
    return resultList;

  }

  /**
   * 检查并返回集合中的一个对象 集合不能多于一个对象 集合为空时返回null
   *
   * @param list
   * @param <T>
   * @return
   */
  public static <T> T getSingleResult(List<T> list) {
    if (CollectionUtils.isEmpty(list)) {
      return null;
    } else if (list.size() == 1) {
      return list.get(0);
    } else {
      //加载到多余数据
      throw new ServiceException("加载到多余条数");
    }
  }

  /**
   * 检查并返回集合中的第一对象 集合为空时返回null
   *
   * @param list
   * @param <T>
   * @return
   */
  public static <T> T getFirstResult(List<T> list) {
    if (CollectionUtils.isEmpty(list)) {
      return null;
    } else {
      return list.get(0);
    }
  }

  /**
   * 公共转换 从List转成Map
   *
   * @param stat
   * @return
   */
  public static <K, V, T> Map<K, V> transformListToMap(List<T> stat,
      Collector<T, Map, Map> collector) {
    Map<K, V> hashMap = null;
    if (!CollectionUtils.isEmpty(stat)) {
      Stream<T> stream = null;
      if (stat.size() > 1000) {//并行去收集
        stream = stat.parallelStream();
      } else {
        //否则串行去收集
        stream = stat.stream();
      }
      hashMap = stream.collect(collector);
    }
    return hashMap;
  }

  /**
   * 填充数据
   *
   * @param finalResult
   * @param intoResult
   * @param params
   * @return
   * @throws InvocationTargetException
   * @throws IllegalAccessException
   */
  public static <T> Map<String, T> getAssemblyResult(Map<String, T> finalResult,
      Map<String, T> intoResult,
      Class<T> c,
      String[] params) throws InvocationTargetException, IllegalAccessException {
    if (!CollectionUtils.isEmpty(intoResult)) {
      for (Map.Entry<String, T> entry : intoResult.entrySet()) {
        //key
        String key = entry.getKey();
        //value
        T value = entry.getValue();

        T statPerDayOrder = finalResult.get(key);
        if (statPerDayOrder == null) {
          finalResult.put(key, value);
        } else {
          for (String param : params) {
            PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(c, param);
            Method readMethod = propertyDescriptor.getReadMethod();
            Method writeMethod = propertyDescriptor.getWriteMethod();
            //从finalResult里面获取
            Object oldValue = readMethod.invoke(statPerDayOrder);
            //从新加入对象读
            Object paramValue = readMethod.invoke(value);
            if (oldValue != null && paramValue != null) {
              if (BigDecimal.class.isInstance(paramValue)) {//BigDecimal
                BigDecimal old = (BigDecimal) oldValue;
                paramValue = old.add((BigDecimal) paramValue);
              } else if (Integer.class.isInstance(paramValue)) {
                Integer old = (Integer) oldValue;
                paramValue = old + (Integer) paramValue;
              } else if (Long.class.isInstance(paramValue)) {
                Long old = (Long) oldValue;
                paramValue = old + (Long) paramValue;
              }
            }
            //往已有对象里面写
            writeMethod.invoke(statPerDayOrder, paramValue);
          }
        }
      }
    }
    return finalResult;
  }

}
