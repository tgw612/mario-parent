package com.mario.redis.config;

/**
 * Redis常量
 * redis key统一前缀：   product:[admin/center]:[业务含义比如shop]:等等
 * 列子： 商品按id维度详情缓存： product:center:product_detail:12
 * 店铺按id维度详情缓存             product:center:shop_detail:123
 */
public final class RedisConstant {

  /**
   * 统一 empty 过期时间 防止 雪崩效应 单位：秒
   */
  public static final Integer EMPTY_EXPIRE_TIME = 120;


  /**
   * 统一 懒加载 分布式锁 过期时间
   */
  public static final Integer LOCK_EXPIRE_TIME = 120;


  /**
   * jetcache 区域配置
   */
  public static final class Area {

    /**
     * 默认
     */
    public static final String DEFAULT = "default";
    /**
     * 商品
     */
    public static final String PRODUCT = "product";

  }

  /**
   * 定义Redis 中Key
   */
  public static final class Key {

    /**
     * 字典前缀
     */
    public static final String DICTIONARY_PRE = "dictionary:";

    /**
     * 锁后缀
     */
    public static final String LOCK_SUF = ":lock";

    /**
     * 缓存前缀
     */
    public static final String ACT_PRE = "mall:product:cache:";

    /**
     * 缓存前缀
     */
    public static final String SHOP_PRE = "shop:cache:";

  }

  public static final class CacheName {

    /**
     * 1分钟
     */
    public static final String DEFAULT = "DEFAULT";

    /**
     * 10分钟
     */
    public static final String NORMAL = "NORMAL";

    /**
     * 1小时
     */
    public static final String LONG = "LONG";

    /**
     * 10秒
     */
    public static final String SHORT = "SHORT";
    /**
     * 3秒
     */
    public static final String SHORTER = "SHORTER";

    /**
     * 30秒
     */
    public static final String THIRTY_SECONDS = "THIRTY";

    /**
     * 15秒
     */
    public static final String FIFTEEN_SECONDS = "FIFTEEN";
  }
}
