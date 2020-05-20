package com.mario.redis.util;

import com.mario.common.constants.CommonConstants;
import com.mario.common.util.CollectionUtil;
import com.mario.common.util.StringUtil;
import com.mario.common.util.UTF8Encoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import org.springframework.dao.DataAccessException;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metric;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoRadiusCommandArgs;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;

public class RedisTemplateUtil {

  public static final long NONE_EXPIRE = 0L;
  private static RedisTemplate<String, Object> redisTemplate;
  private static StringRedisTemplate stringRedisTemplate;

  public RedisTemplateUtil() {
  }

  public static void del(String... key) {
    if (key != null && key.length > 0) {
      if (key.length == 1) {
        redisTemplate.delete(key[0]);
      } else {
        redisTemplate.delete(Arrays.asList(key));
      }
    }

  }

  public static void batchDel(String... pattern) {
    String[] var1 = pattern;
    int var2 = pattern.length;

    for (int var3 = 0; var3 < var2; ++var3) {
      String kp = var1[var3];
      redisTemplate.delete(redisTemplate.keys(kp + "*"));
    }

  }

  public static List multiGetObject(Collection<String> keys) {
    return redisTemplate.opsForValue().multiGet(keys);
  }

  public static List<String> multiGetString(Collection<String> keys) {
    return stringRedisTemplate.opsForValue().multiGet(keys);
  }

  public static String getStr(String key) {
    return (String) stringRedisTemplate.boundValueOps(key).get();
  }

  public static void setStr(String key, String value) {
    stringRedisTemplate.opsForValue().set(key, value);
  }

  public static void setStr(String key, String value, long expireSeconds) {
    if (expireSeconds > 0L) {
      stringRedisTemplate.opsForValue().set(key, value, expireSeconds, TimeUnit.SECONDS);
    } else {
      stringRedisTemplate.opsForValue().set(key, value);
    }

  }

  public static String getAndSetStr(String key, String value) {
    return (String) stringRedisTemplate.opsForValue().getAndSet(key, value);
  }

  public static Integer appendStr(String key, String value) {
    return stringRedisTemplate.opsForValue().append(key, value);
  }

  public static Integer getInt(String key) {
    String value = getStr(key);
    return StringUtil.isNumeric(value) ? Integer.valueOf(value) : null;
  }

  public static void setInt(String key, int value) {
    setStr(key, Integer.toString(value));
  }

  public static void setInt(String key, int value, long expireSeconds) {
    setStr(key, Integer.toString(value), expireSeconds);
  }

  public static Integer getAndSetInt(String key, int value) {
    String intStr = (String) stringRedisTemplate.opsForValue()
        .getAndSet(key, Integer.toString(value));
    return StringUtil.isNumeric(intStr) ? Integer.valueOf(intStr) : null;
  }

  public static Double getDouble(String key) {
    String value = getStr(key);
    return value != null && StringUtil.checkDecimals(value) ? Double.valueOf(value) : null;
  }

  public static void setDouble(String key, double value) {
    setStr(key, Double.toString(value));
  }

  public static void setDouble(String key, double value, long expireSeconds) {
    setStr(key, Double.toString(value), expireSeconds);
  }

  public static Double getAndSetDouble(String key, double value) {
    String doubleStr = (String) stringRedisTemplate.opsForValue()
        .getAndSet(key, Double.toString(value));
    return doubleStr != null && StringUtil.checkDecimals(doubleStr) ? Double.valueOf(doubleStr)
        : null;
  }

  public static Float getFloat(String key) {
    String value = getStr(key);
    return value != null && StringUtil.checkDecimals(value) ? Float.valueOf(value) : null;
  }

  public static void setFloat(String key, float value) {
    setStr(key, Float.toString(value));
  }

  public static void setFloat(String key, float value, long expireSeconds) {
    setStr(key, Float.toString(value), expireSeconds);
  }

  public static Float getAndSetFloat(String key, float value) {
    String floatStr = (String) stringRedisTemplate.opsForValue()
        .getAndSet(key, Float.toString(value));
    return floatStr != null && StringUtil.checkDecimals(floatStr) ? Float.valueOf(floatStr) : null;
  }

  public static Short getShort(String key) {
    String value = getStr(key);
    return StringUtil.isNumeric(value) ? Short.valueOf(value) : null;
  }

  public static void setShort(String key, short value) {
    setStr(key, Short.toString(value));
  }

  public static void setShort(String key, short value, long expireSeconds) {
    setStr(key, Short.toString(value), expireSeconds);
  }

  public static Short getAndSetShort(String key, short value) {
    String floatStr = (String) stringRedisTemplate.opsForValue()
        .getAndSet(key, Short.toString(value));
    return floatStr != null && StringUtil.isNumeric(floatStr) ? Short.valueOf(floatStr) : null;
  }

  public static Long getLong(String key) {
    String value = getStr(key);
    return StringUtil.isNumeric(value) ? Long.valueOf(value) : null;
  }

  public static void setLong(String key, long value) {
    setStr(key, Long.toString(value));
  }

  public static void setLong(String key, long value, long expireSeconds) {
    setStr(key, Long.toString(value), expireSeconds);
  }

  public static Long getAndSetLong(String key, long value) {
    String longStr = (String) stringRedisTemplate.opsForValue()
        .getAndSet(key, Long.toString(value));
    return longStr != null && StringUtil.isNumeric(longStr) ? Long.valueOf(longStr) : null;
  }

  public static Boolean getBoolean(String key) {
    String value = getStr(key);
    return value != null ? Boolean.valueOf(value) : null;
  }

  public static void setBoolean(String key, boolean value) {
    setStr(key, Boolean.toString(value));
  }

  public static void setBoolean(String key, boolean value, long expireSeconds) {
    setStr(key, Boolean.toString(value), expireSeconds);
  }

  public static Boolean getAndSetBoolean(String key, boolean value) {
    String boolStr = (String) stringRedisTemplate.opsForValue()
        .getAndSet(key, Boolean.toString(value));
    return boolStr != null ? Boolean.valueOf(boolStr) : null;
  }

  public static Object getObj(String key) {
    return redisTemplate.boundValueOps(key).get();
  }

  public static void setObj(String key, Object value) {
    redisTemplate.opsForValue().set(key, value);
  }

  public static void setObj(String key, Object value, long expireSeconds) {
    if (expireSeconds > 0L) {
      redisTemplate.opsForValue().set(key, value, expireSeconds, TimeUnit.SECONDS);
    } else {
      redisTemplate.opsForValue().set(key, value);
    }

  }

  public static Object getAndSetObj(String key, Object value) {
    return redisTemplate.opsForValue().getAndSet(key, value);
  }

  public static <T> T get(String key, Class<T> clazz) {
    if (clazz.equals(String.class)) {
      return (T) stringRedisTemplate.boundValueOps(key).get();
    } else {
      String value;
      if (clazz.equals(Integer.class)) {
        value = (String) stringRedisTemplate.boundValueOps(key).get();
        if (StringUtil.isNotBlank(value)) {
          return (T) Integer.valueOf(value);
        }
      } else if (clazz.equals(Long.class)) {
        value = (String) stringRedisTemplate.boundValueOps(key).get();
        if (StringUtil.isNotBlank(value)) {
          return (T) Long.valueOf(value);
        }
      } else if (clazz.equals(Double.class)) {
        value = (String) stringRedisTemplate.boundValueOps(key).get();
        if (StringUtil.isNotBlank(value)) {
          return (T) Double.valueOf(value);
        }
      } else if (clazz.equals(Boolean.class)) {
        value = (String) stringRedisTemplate.boundValueOps(key).get();
        if (StringUtil.isNotBlank(value)) {
          return (T) Boolean.valueOf(value);
        }
      } else if (clazz.equals(Float.class)) {
        value = (String) stringRedisTemplate.boundValueOps(key).get();
        if (StringUtil.isNotBlank(value)) {
          return (T) Float.valueOf(value);
        }
      } else {
        if (!clazz.equals(Short.class)) {
          return (T) redisTemplate.boundValueOps(key).get();
        }

        value = (String) stringRedisTemplate.boundValueOps(key).get();
        if (StringUtil.isNotBlank(value)) {
          return (T) Short.valueOf(value);
        }
      }

      return null;
    }
  }

  public static void set(String key, Object value) {
    set(key, value, 0L);
  }

  public static void set(String key, Object value, long expireSeconds) {
    if (isBaseDataType(value.getClass())) {
      setStr(key, value.toString(), expireSeconds);
    } else {
      setObj(key, value, expireSeconds);
    }

  }

  private static boolean isBaseDataType(Class clazz) {
    return clazz.equals(String.class) || clazz.equals(Integer.class) || clazz.equals(Long.class)
        || clazz.equals(Double.class) || clazz.equals(Float.class) || clazz.equals(Short.class)
        || clazz.equals(Boolean.class);
  }

  public static double decrDouble(String key) {
    return decrDouble(key, 1.0D);
  }

  public static double decrDouble(String key, double by) {
    return redisTemplate.opsForValue().increment(key, -by);
  }

  public static double incrDouble(String key) {
    return incrDouble(key, 1.0D);
  }

  public static double incrDouble(String key, double by) {
    return redisTemplate.opsForValue().increment(key, by);
  }

  public static double getIncrDoubleVal(String key) {
    return incrDouble(key, 0.0D);
  }

  public static List<Double> multiGetIncrDoubleValWithPip(final Collection<String> keys) {
    List result;
    if (CollectionUtil.isNotEmpty(keys)) {
      result = redisTemplate.executePipelined(new SessionCallback() {
        public Object execute(RedisOperations operations) throws DataAccessException {
          keys.forEach((key) -> {
            operations.opsForValue().increment(key, 0.0D);
          });
          return null;
        }
      });
    } else {
      result = CommonConstants.EMPTY_LIST;
    }

    return result;
  }

  public static List<Long> multiGetIncrLongValWithPip(final Collection<String> keys) {
    List result;
    if (CollectionUtil.isNotEmpty(keys)) {
      result = redisTemplate.executePipelined(new SessionCallback() {
        @Override
        public Object execute(RedisOperations operations) throws DataAccessException {
          keys.forEach((key) -> {
            operations.opsForValue().increment(key, 0L);
          });
          return null;
        }
      });
    } else {
      result = CommonConstants.EMPTY_LIST;
    }

    return result;
  }

  public static long incrLong(String key) {
    return incrLong(key, 1L);
  }

  public static long incrLong(String key, long by) {
    return redisTemplate.opsForValue().increment(key, by);
  }

  public static long decrLong(String key) {
    return decrLong(key, 1L);
  }

  public static long decrLong(String key, long by) {
    return redisTemplate.opsForValue().increment(key, -by);
  }

  public static long getIncrLongVal(String key) {
    return incrLong(key, 0L);
  }

  public static Boolean getBit(String key, long offset) {
    return stringRedisTemplate.opsForValue().getBit(key, offset);
  }

  public static Boolean setBit(String key, long offset, boolean value) {
    return stringRedisTemplate.opsForValue().setBit(key, offset, value);
  }

  public static Long bitCount(String key) {
    byte[] rawKey = stringRedisTemplate.getStringSerializer().serialize(key);
    return (Long) stringRedisTemplate.execute((connection) -> {
      return connection.bitCount(rawKey);
    }, true);
  }

  public static Long bitCount(String key, long start, long end) {
    byte[] rawKey = stringRedisTemplate.getStringSerializer().serialize(key);
    return (Long) stringRedisTemplate.execute((connection) -> {
      return connection.bitCount(rawKey, start, end);
    }, true);
  }

  public static Long bitCount(RedisStringCommands.BitOperation op, String storeDestKey,
      String... sourcekeys) {
    byte[][] rawSourcekeys = rawTypes(stringRedisTemplate.getStringSerializer(), sourcekeys);
    byte[] rawDestKey = stringRedisTemplate.getStringSerializer().serialize(storeDestKey);
    return (Long) stringRedisTemplate.execute((connection) -> {
      return connection.bitOp(op, rawDestKey, rawSourcekeys);
    }, true);
  }

  public static Boolean setBitForTrue(String key, long offset) {
    return stringRedisTemplate.opsForValue().setBit(key, offset, true);
  }

  public static Boolean setBitForFalse(String key, long offset) {
    return stringRedisTemplate.opsForValue().setBit(key, offset, false);
  }

  public static Boolean setIfAbsent(String key, String value) {
    return stringRedisTemplate.opsForValue().setIfAbsent(key, value);
  }

  public static Boolean setIfAbsent(String key, String value, long expireSeconds) {
    RedisSerializer<String> serializer = stringRedisTemplate.getStringSerializer();
    byte[] keyBytes = serializer.serialize(key);
    byte[] valueBytes = serializer.serialize(value);
    return (Boolean) stringRedisTemplate.execute((connection) -> {
      return connection.set(keyBytes, valueBytes, Expiration.seconds(expireSeconds),
          RedisStringCommands.SetOption.SET_IF_ABSENT);
    }, true);
  }

  public static Boolean setIfPresent(String key, String value, long expireSeconds) {
    RedisSerializer<String> serializer = stringRedisTemplate.getStringSerializer();
    byte[] keyBytes = serializer.serialize(key);
    byte[] valueBytes = serializer.serialize(value);
    return (Boolean) stringRedisTemplate.execute((connection) -> {
      return connection.set(keyBytes, valueBytes, Expiration.seconds(expireSeconds),
          RedisStringCommands.SetOption.SET_IF_PRESENT);
    }, true);
  }

  public static String getAndSetString(String key, String value) {
    return (String) stringRedisTemplate.opsForValue().getAndSet(key, value);
  }

  public static Object getAndSetObject(String key, Object value) {
    return redisTemplate.opsForValue().getAndSet(key, value);
  }

  public static boolean hasKey(String key) {
    Boolean aBoolean = stringRedisTemplate.hasKey(key);
    return aBoolean != null && aBoolean;
  }

  public static void expire(String key, long expireSeconds) {
    if (expireSeconds > 0L) {
      redisTemplate.expire(key, expireSeconds, TimeUnit.SECONDS);
    }

  }

//    public static void expire(String key, final Date date) {
//        final byte[] rawKey = redisTemplate.getKeySerializer().serialize(key);
//        redisTemplate.execute(new RedisCallback<Boolean>() {
//            @Override
//            public Boolean doInRedis(RedisConnection connection) {
//                return connection.expireAt(rawKey, date.getTime() / 1000L);
//            }
//        }, true);
//    }

  public static Long getExpire(String key) {
    return redisTemplate.boundValueOps(key).getExpire();
  }

  public static Boolean hasSetExpire(String key) {
    return redisTemplate.boundValueOps(key).getExpire() >= 0L;
  }

  public static Boolean persist(String key) {
    return redisTemplate.boundValueOps(key).persist();
  }

  public static Long mDelMapField(String mapKey, Object... fields) {
    return redisTemplate.opsForHash().delete(mapKey, fields);
  }

  public static Long mDelMapField(String mapKey, String... fields) {
    return redisTemplate.boundHashOps(mapKey).delete(fields);
  }

  public static Long delMapField(String mapKey, String... fields) {
    return mDelMapField(mapKey, fields);
  }

  public static boolean mHasKey(String mapKey, String field) {
    return redisTemplate.boundHashOps(mapKey).hasKey(field);
  }

  public static <T> void setMap(String mapKey, Map<String, T> map) {
    setMap(mapKey, map, 0L);
  }

  public static <T> void setMap(String mapKey, Map<String, T> map, long expireSeconds) {
    redisTemplate.opsForHash().putAll(mapKey, map);
    if (expireSeconds > 0L) {
      expire(mapKey, expireSeconds);
    }

  }

  public static void addMap(String mapKey, String field, String value) {
    redisTemplate.opsForHash().put(mapKey, field, value);
  }

  public static Boolean addMapIfAbsent(String mapKey, String field, String value) {
    return redisTemplate.opsForHash().putIfAbsent(mapKey, field, value);
  }

  public static <T> void addMap(String mapKey, String field, T obj) {
    redisTemplate.opsForHash().put(mapKey, field, obj);
  }

  public static <T> Boolean addMapIfAbsent(String mapKey, String field, T obj) {
    return redisTemplate.opsForHash().putIfAbsent(mapKey, field, obj);
  }

  public static <T> Map<String, T> mget(String mapKey, Class<T> clazz) {
    BoundHashOperations<String, String, T> boundHashOperations = redisTemplate.boundHashOps(mapKey);
    return boundHashOperations.entries();
  }

  public static <T> Map<String, ?> mget(String mapKey) {
    BoundHashOperations<String, String, ?> boundHashOperations = redisTemplate.boundHashOps(mapKey);
    return boundHashOperations.entries();
  }

//    public static Map<byte[], byte[]> mgetbytes(String mapKey) {
//        return redisTemplate.entries(mapKey);
//    }

  public static List<Object> mvalues(String mapKey) {
    return redisTemplate.opsForHash().values(mapKey);
  }

  public static Set<Object> mkeys(String mapKey) {
    return redisTemplate.opsForHash().keys(mapKey);
  }

//    public static Map<String, byte[]> mgetstrbytes(String mapKey) {
//        return deserializeHashMap(redisTemplate.entries(mapKey));
//    }

  public static Map<String, byte[]> deserializeHashMap(Map<byte[], byte[]> entries) {
    if (entries == null) {
      return null;
    } else {
      Map<String, byte[]> map = new LinkedHashMap(entries.size());
      Iterator var2 = entries.entrySet().iterator();

      while (var2.hasNext()) {
        Map.Entry<byte[], byte[]> entry = (Map.Entry) var2.next();
        map.put(String
                .valueOf(redisTemplate.getHashKeySerializer().deserialize((byte[]) entry.getKey())),
            entry.getValue());
      }

      return map;
    }
  }

  public static List<Object> multiGet(String mapKey, List<Object> fields) {
    return redisTemplate.opsForHash().multiGet(mapKey, fields);
  }

  public static Long mIncrLong(String mapKey, String field) {
    return mIncrLong(mapKey, field, 1L);
  }

  public static Long mIncrLong(String mapKey, String field, long by) {
    return redisTemplate.opsForHash().increment(mapKey, field, by);
  }

  public static Long mDecrLong(String mapKey, String field) {
    return mDecrLong(mapKey, field, 1L);
  }

  public static Long mDecrLong(String mapKey, String field, long by) {
    return redisTemplate.opsForHash().increment(mapKey, field, -by);
  }

  public static Long mGetIncrLong(String mapKey, String field) {
    return redisTemplate.opsForHash().increment(mapKey, field, 0L);
  }

  public static Double mIncrDouble(String mapKey, String field) {
    return mIncrDouble(mapKey, field, 1.0D);
  }

  public static Double mIncrDouble(String mapKey, String field, double by) {
    return redisTemplate.opsForHash().increment(mapKey, field, by);
  }

  public static Long mDecrDouble(String mapKey, String field) {
    return mDecrDouble(mapKey, field, 1L);
  }

  public static Long mDecrDouble(String mapKey, String field, long by) {
    return redisTemplate.opsForHash().increment(mapKey, field, -by);
  }

  public static Double mGetIncrDouble(String mapKey, String field) {
    return redisTemplate.opsForHash().increment(mapKey, field, 0.0D);
  }

  public static List<Double> multiGetIncrDoubleValWithPip(final List<String> mapKeys,
      final List<String> fields) {
    List result;
    if (CollectionUtil.isNotEmpty(mapKeys) && CollectionUtil.isNotEmpty(fields)
        && mapKeys.size() == fields.size()) {
      result = redisTemplate.executePipelined(new SessionCallback() {
        @Override
        public Object execute(RedisOperations operations) throws DataAccessException {
          IntStream.range(0, mapKeys.size()).forEach((i) -> {
            operations.opsForHash().increment(mapKeys.get(i), fields.get(i), 0.0D);
          });
          return null;
        }
      });
    } else {
      result = CommonConstants.EMPTY_LIST;
    }

    return result;
  }

  public static List<Long> multiGetIncrLongValWithPip(final List<String> mapKeys,
      final List<String> fields) {
    List result;
    if (CollectionUtil.isNotEmpty(mapKeys) && CollectionUtil.isNotEmpty(fields)
        && mapKeys.size() == fields.size()) {
      result = redisTemplate.executePipelined(new SessionCallback() {
        public Object execute(RedisOperations operations) throws DataAccessException {
          IntStream.range(0, mapKeys.size()).forEach((i) -> {
            operations.opsForHash().increment(mapKeys.get(i), fields.get(i), 0L);
          });
          return null;
        }
      });
    } else {
      result = CommonConstants.EMPTY_LIST;
    }

    return result;
  }

  public static Map<String, byte[]> convertToBytesMap(Map<String, Long> longMap) {
    Map<String, byte[]> longBytesMap = new HashMap(longMap.size());
    Iterator var2 = longMap.entrySet().iterator();

    while (var2.hasNext()) {
      Map.Entry<String, Long> entry = (Map.Entry) var2.next();
      longBytesMap.put(entry.getKey(), UTF8Encoder.encode(((Long) entry.getValue()).toString()));
    }

    return longBytesMap;
  }

  public static Map<String, Long> convertToLongMap(Map<String, byte[]> longBytesMap) {
    HashMap longMap = new HashMap(longBytesMap.size());

    try {
      Iterator var2 = longBytesMap.entrySet().iterator();

      while (var2.hasNext()) {
        Map.Entry<String, byte[]> entry = (Map.Entry) var2.next();
        longMap.put(entry.getKey(), Long.parseLong(UTF8Encoder.decode((byte[]) entry.getValue())));
      }
    } catch (NumberFormatException var4) {
      var4.printStackTrace();
    }

    return longMap;
  }

  public static Long mSize(String mapKey) {
    return redisTemplate.opsForHash().size(mapKey);
  }

  public static <T> T getMapField(String mapKey, String field, Class<T> clazz) {
    return (T) redisTemplate.boundHashOps(mapKey).get(field);
  }

  public static Long sadd(String key, Object... values) {
    return redisTemplate.boundSetOps(key).add(values);
  }

  public static Long sdel(String key, Object... values) {
    return redisTemplate.boundSetOps(key).remove(values);
  }

  public static Object spop(String key) {
    return redisTemplate.boundSetOps(key).pop();
  }

  public static List srandomMembers(String key, long count) {
    return redisTemplate.boundSetOps(key).randomMembers(count);
  }

  public static Object srandomMember(String key) {
    return redisTemplate.boundSetOps(key).randomMember();
  }

  public static Set smembers(String key) {
    return redisTemplate.boundSetOps(key).members();
  }

  public static Long sSize(String key) {
    return redisTemplate.boundSetOps(key).size();
  }

  public static Boolean isMember(String key, Object obj) {
    return redisTemplate.boundSetOps(key).isMember(obj);
  }

  public static void srename(String oldkey, String newkey) {
    redisTemplate.boundSetOps(oldkey).rename(newkey);
  }

  public static Boolean smoveMember(String sourcekey, String destKey, Object member) {
    return redisTemplate.boundSetOps(sourcekey).move(destKey, member);
  }

  public static Set sdiff(String sourcekey, String destKey) {
    return redisTemplate.boundSetOps(sourcekey).diff(destKey);
  }

  public static Set sdiff(String sourcekey, Collection<String> destKeys) {
    return redisTemplate.boundSetOps(sourcekey).diff(destKeys);
  }

  public static void sdiffAndStore(String sourcekey, String storeDestKey,
      Collection<String> destKeys) {
    redisTemplate.boundSetOps(sourcekey).diffAndStore(destKeys, storeDestKey);
  }

  public static void sdiffAndStore(String sourcekey, String storeDestKey, String destKey) {
    redisTemplate.boundSetOps(sourcekey).diffAndStore(destKey, storeDestKey);
  }

  public static Set sintersect(String sourcekey, String destKey) {
    return redisTemplate.boundSetOps(sourcekey).intersect(destKey);
  }

  public static Set sintersect(String sourcekey, Collection<String> destKeys) {
    return redisTemplate.boundSetOps(sourcekey).intersect(destKeys);
  }

  public static void sintersectAndStore(String sourcekey, String storeDestKey, String destKey) {
    redisTemplate.boundSetOps(sourcekey).intersectAndStore(destKey, storeDestKey);
  }

  public static void sintersectAndStore(String sourcekey, String storeDestKey,
      Collection<String> destKeys) {
    redisTemplate.boundSetOps(sourcekey).intersectAndStore(destKeys, storeDestKey);
  }

  public static Set sunion(String sourcekey, String destKey) {
    return redisTemplate.boundSetOps(sourcekey).union(destKey);
  }

  public static Set sunion(String sourcekey, Collection<String> destKeys) {
    return redisTemplate.boundSetOps(sourcekey).union(destKeys);
  }

  public static void sunionAndStore(String sourcekey, String storeDestKey, String destKey) {
    redisTemplate.boundSetOps(sourcekey).unionAndStore(destKey, storeDestKey);
  }

  public static void sunionAndStore(String sourcekey, String storeDestKey,
      Collection<String> destKeys) {
    redisTemplate.boundSetOps(sourcekey).unionAndStore(destKeys, storeDestKey);
  }

  public static Cursor sscan(String key, ScanOptions scanOptions) {
    return redisTemplate.boundSetOps(key).scan(scanOptions);
  }

  public static void lSet(String key, long index, Object obj) {
    redisTemplate.boundListOps(key).set(index, obj);
  }

  public static Object lGet(String key, long index) {
    return redisTemplate.boundListOps(key).index(index);
  }

  public static Object lIndex(String key, long index) {
    return lGet(key, index);
  }

  public static Object lLeftPop(String key) {
    return redisTemplate.boundListOps(key).leftPop();
  }

  public static Object lLeftPop(String key, long timeout, TimeUnit timeUnit) {
    return redisTemplate.boundListOps(key).leftPop(timeout, timeUnit);
  }

  public static Object lRightPop(String key) {
    return redisTemplate.boundListOps(key).rightPop();
  }

  public static Object lRightPop(String key, long timeout, TimeUnit timeUnit) {
    return redisTemplate.boundListOps(key).rightPop(timeout, timeUnit);
  }

  public static Long lLeftPush(String key, Object value) {
    return redisTemplate.boundListOps(key).leftPush(value);
  }

  public static Long lLeftPushAll(String key, Object... value) {
    return redisTemplate.boundListOps(key).leftPushAll(value);
  }

  public static Long lLeftPush(String key, Object existObj, Object afterObj) {
    return redisTemplate.boundListOps(key).leftPush(existObj, afterObj);
  }

  public static Long lRightPush(String key, Object existObj, Object beforeObj) {
    return redisTemplate.boundListOps(key).rightPush(existObj, beforeObj);
  }

  public static Long lLeftPushIfPresent(String key, Object value) {
    return redisTemplate.boundListOps(key).leftPushIfPresent(value);
  }

  public static Long lRightPushIfPresent(String key, Object value) {
    return redisTemplate.boundListOps(key).rightPushIfPresent(value);
  }

  public static void lTrim(String key, long start, long end) {
    redisTemplate.boundListOps(key).trim(start, end);
  }

  public static List lRange(String key, long start, long end) {
    return redisTemplate.boundListOps(key).range(start, end);
  }

  public static long lRemove(String key, long count, Object value) {
    return redisTemplate.boundListOps(key).remove(count, value);
  }

  public static long lSize(String key) {
    return redisTemplate.boundListOps(key).size();
  }

  public static Boolean zAdd(String key, Object value, double score) {
    return redisTemplate.boundZSetOps(key).add(value, score);
  }

//    public static Long zAdd(String key, Set<DefaultTypedTuple> defaultTypedTupleSet) {
//        return redisTemplate.boundZSetOps(key).add(defaultTypedTupleSet);
//    }

  public static Long zSize(String key) {
    return redisTemplate.boundZSetOps(key).zCard();
  }

  public static Long zCount(String key, double min, double max) {
    return redisTemplate.boundZSetOps(key).count(min, max);
  }

  public static Double zIncrementScore(String key, Object value, double increment) {
    return redisTemplate.boundZSetOps(key).incrementScore(value, increment);
  }

  public static void zIntersectAndStore(String sourcekey, String storeDestKey, String destKey) {
    redisTemplate.boundZSetOps(sourcekey).intersectAndStore(destKey, storeDestKey);
  }

  public static void zIntersectAndStore(String sourcekey, String storeDestKey,
      Collection<String> destKeys) {
    redisTemplate.boundZSetOps(sourcekey).intersectAndStore(destKeys, storeDestKey);
  }

  public static void zUnionAndStore(String sourcekey, String storeDestKey, String destKey) {
    redisTemplate.boundZSetOps(sourcekey).unionAndStore(destKey, storeDestKey);
  }

  public static void zUnionAndStore(String sourcekey, String storeDestKey,
      Collection<String> destKeys) {
    redisTemplate.boundZSetOps(sourcekey).unionAndStore(destKeys, storeDestKey);
  }

  public static Set zRange(String key, long start, long end) {
    return redisTemplate.boundZSetOps(key).range(start, end);
  }

  public static Set zRangeByLex(String key, RedisZSetCommands.Range range,
      RedisZSetCommands.Limit limit) {
    return redisTemplate.boundZSetOps(key).rangeByLex(range, limit);
  }

  public static Set zRangeByScore(String key, double gteMin, double ltMax) {
    return redisTemplate.boundZSetOps(key).rangeByScore(gteMin, ltMax);
  }

  public static Long zRank(String key, Object value) {
    return redisTemplate.boundZSetOps(key).rank(value);
  }

  public static Long zRemove(String key, Object... values) {
    return redisTemplate.boundZSetOps(key).remove(values);
  }

  public static void zRemoveRange(String key, long start, long end) {
    redisTemplate.boundZSetOps(key).removeRange(start, end);
  }

  public static void zRemoveRangeByScore(String key, double gteMin, double ltMax) {
    redisTemplate.boundZSetOps(key).removeRangeByScore(gteMin, ltMax);
  }

  public static Set zReverseRange(String key, long start, long end) {
    return redisTemplate.boundZSetOps(key).reverseRange(start, end);
  }

  public static Set zReverseRangeByScore(String key, double gteMin, double ltMax) {
    return redisTemplate.boundZSetOps(key).reverseRangeByScore(gteMin, ltMax);
  }

  public static Long zReverseRank(String key, Object value) {
    return redisTemplate.boundZSetOps(key).reverseRank(value);
  }

  public static Double sScore(String key, Object value) {
    return redisTemplate.boundZSetOps(key).score(value);
  }

  public static Cursor zScan(String key, ScanOptions scanOptions) {
    return redisTemplate.boundZSetOps(key).scan(scanOptions);
  }

  public static Long pfAdd(String key, Object... values) {
    return redisTemplate.opsForHyperLogLog().add(key, values);
  }

//    public static Long pfSize(String keys) {
//        return redisTemplate.opsForHyperLogLog().size(new Object[]{keys});
//    }

  public static Long pfMergeAndCount(String storeDestKey, String... sourceKeys) {
    return redisTemplate.opsForHyperLogLog().union(storeDestKey, sourceKeys);
  }

  public static <M> Long geoAdd(String key, Point point, M member) {
    return redisTemplate.opsForGeo().add(key, point, member);
  }

  public static <M> Long geoAdd(String key, GeoLocation<M> geoLocation) {
    return geoAdd(key, geoLocation.getPoint(), geoLocation.getName());
  }

//    public static <M> Long geoAdd(String key, Map<M, Point> memberCoordinateMap) {
//        return redisTemplate.opsForGeo().add(key, memberCoordinateMap);
//    }
//
//    public static <M> Long geoAdd(String key, Iterable<GeoLocation<M>> locations) {
//        return redisTemplate.opsForGeo().add(key, locations);
//    }

  public static <M> Distance geoDistance(String key, M startMember, M endMember) {
    return redisTemplate.opsForGeo().distance(key, startMember, endMember);
  }

  public static <M> Distance geoDistance(String key, M startMember, M endMember, Metric metric) {
    return redisTemplate.opsForGeo().distance(key, startMember, endMember, metric);
  }

  public static <M> List<String> geoHash(String key, M... members) {
    return redisTemplate.opsForGeo().hash(key, members);
  }

  public static <M> List<Point> geoPosition(String key, M... members) {
    return redisTemplate.opsForGeo().position(key, members);
  }

  public static <M> GeoResults<GeoLocation<Object>> radius(String key, Circle within) {
    return redisTemplate.opsForGeo().radius(key, within);
  }

  public static <M> GeoResults<GeoLocation<Object>> radius(String key, Circle within,
      GeoRadiusCommandArgs args) {
    return redisTemplate.opsForGeo().radius(key, within, args);
  }

  public static <M> GeoResults<GeoLocation<Object>> radiusByMember(String key, M member,
      double radius) {
    return redisTemplate.opsForGeo().radius(key, member, radius);
  }

  public static <M> GeoResults<GeoLocation<Object>> radiusByMember(String key, M member,
      Distance distance) {
    return redisTemplate.opsForGeo().radius(key, member, distance);
  }

  public static <M> GeoResults<GeoLocation<Object>> radiusByMember(String key, M member,
      Distance distance, GeoRadiusCommandArgs args) {
    return redisTemplate.opsForGeo().radius(key, member, distance, args);
  }

  public static void publishMsg(String channel, Object message) {
    redisTemplate.convertAndSend(channel, message);
  }

  public static void publishMsg(byte[] rawChannel, byte[] rawMessage) {
    redisTemplate.execute((connection) -> {
      connection.publish(rawChannel, rawMessage);
      return null;
    }, true);
  }

  public static void subscribe(MessageListener listener, String... channels) {
    if (channels != null && channels.length > 0) {
      byte[][] rawChannels = rawTypes(redisTemplate.getKeySerializer(), channels);
      subscribe(listener, rawChannels);
    }

  }

  public static void subscribe(MessageListener listener, byte[]... rawChannels) {
    redisTemplate.execute((connection) -> {
      connection.subscribe(listener, rawChannels);
      return null;
    }, true);
  }

  public static void subscribeWithPattern(MessageListener listener, String... patterns) {
    if (patterns != null && patterns.length > 0) {
      byte[][] rawChannels = rawTypes(redisTemplate.getKeySerializer(), patterns);
      subscribeWithPattern(listener, rawChannels);
    }

  }

  public static void subscribeWithPattern(MessageListener listener, byte[]... rawPatterns) {
    redisTemplate.execute((connection) -> {
      connection.pSubscribe(listener, rawPatterns);
      return null;
    }, true);
  }

  /**
   * @deprecated
   */
  @Deprecated
  public static Set<String> keys(String pattern) {
    return redisTemplate.keys(pattern);
  }

  public static <T> byte[][] rawTypes(RedisSerializer<T> serializer, String[] types) {
    byte[][] rawTypes = new byte[types.length][];
    int i = 0;
    Object[] var4 = types;
    int var5 = types.length;

    for (int var6 = 0; var6 < var5; ++var6) {
      T type = (T) var4[var6];
      rawTypes[i++] = serializer.serialize(type);
    }

    return rawTypes;
  }

  public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
    redisTemplate(redisTemplate);
  }

  public static void redisTemplate(RedisTemplate<String, Object> redisTemplate) {
    RedisTemplateUtil.redisTemplate = redisTemplate;
    stringRedisTemplate(redisTemplate.getConnectionFactory());
  }

  private static void stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
    stringRedisTemplate = new StringRedisTemplate(redisConnectionFactory);
  }

  public static RedisTemplate<String, Object> getRedisTemplate() {
    return redisTemplate;
  }

  public static StringRedisTemplate getRedisTemplateForString() {
    return stringRedisTemplate;
  }

  public static Object execute(RedisScript script, List keys, Object... args) {
    return redisTemplate.execute(script, keys, args);
  }

  public static <T, K> T execute(RedisScript<T> script, RedisSerializer<?> argsSerializer,
      RedisSerializer<T> resultSerializer, List<K> keys, Object... args) {
    return redisTemplate
        .execute(script, (List<String>) argsSerializer, resultSerializer, keys, args);
  }
}
