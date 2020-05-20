package com.mario.common.constants;

/**
 * Description: Author: 陈二伟 Date:2018/10/29
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
     * 首页的区域
     */
    public static final String DISCOVER = "discover";


  }

  /**
   * 定义Redis 中Key
   */
  public static final class Key {


    /**
     * 锁后缀
     */
    public static final String LOCK_SUF = ":lock";

    /**
     * 缓存前缀
     */
    public static final String LOCK_PRE = "mall:discover:lock:";

    /**
     * 发现详情缓存前缀
     */
    public static final String INDEX_COMMEND_DISCOVER_DETAIL_PRE = "mall:api:detail:commendDiscover:";

    /**
     * 发现商品缓存前缀
     */
    public static final String INDEX_DISCOVER_PRODUCT_PRE = "mall:api:index:discover:product:";

    // 新版发现页新增缓存

    /**
     * 话题列表缓存
     */
    public static final String SUBJECT_QUERY_PAGE = "discover:api:subject:queryPage:";

    /**
     * 话题详情缓存
     */
    public static final String SUBJECT_QUERY_INFO = "discover:api:subject:queryInfo:";

    /**
     * 话题内文章缓存
     */
    public static final String SUBJECT_QUERY_ARTICLE_PAGE = "discover:api:subject:queryArticlePage:";

    /**
     * 话题内文章缓存,兼容老版本
     */
    public static final String SUBJECT_QUERY_ARTICLE_PAGE_OLD = "discover:api:subject:queryArticlePageOld:";

    /**
     * 商品阅读次数缓存
     */
    public static final String PRODUCT_READ_COUNT = "discover:api:product:readCount:";

    /**
     * 商品分享次数缓存
     */
    public static final String PRODUCT_SHARE_COUNT = "discover:api:product:shareCount:";

    /**
     * 爆款商品列表缓存
     */
    public static final String HOT_PRODUCT_QUERY_PAGE = "discover:api:product:queryHotPage:";

    /**
     * 高佣金商品列表缓存
     */
    public static final String HIGH_PRODUCT_QUERY_PAGE = "discover:api:product:queryHighPage:";

    /**
     * 订单支付成功返回商品列表缓存
     */
    public static final String ORDER_PAYED_PRODUCT_QUERY_PAGE = "discover:api:product:order:payed:queryPage:";

    /**
     * 商品详情缓存
     */
    public static final String PRODUCT_QUERY_INFO = "discover:api:product:queryInfo:";

    /**
     * 商品内文章缓存
     */
    public static final String PRODUCT_QUERY_ARTICLE_PAGE = "discover:api:product:queryArticlePage:";

    /**
     * 文章列表缓存
     */
    public static final String ARTICLE_QUERY_PAGE = "discover:api:article:queryPage:";

    /**
     * 文章详情缓存
     */
    public static final String ARTICLE_QUERY_INFO = "discover:api:article:queryInfo:";

    /**
     * 文章列表缓存，兼容老版本
     */
    public static final String ARTICLE_QUERY_PAGE_OLD = "discover:api:article:queryPageOld:";

    /**
     * 文章详情缓存，兼容老版本
     */
    public static final String ARTICLE_QUERY_INFO_OLD = "discover:api:article:queryInfoOld:";

    /**
     * 发现分享材料次数缓存前缀
     */
    public static final String INDEX_DISCOVER__SHARE_PRODUCT_PRE = "mall:api:index:shareProduct:count:";

    /**
     * 文章点赞用户列表缓存
     */
    public static final String LIKE_USER_ARTICLE_PAGE = "discover:api:like:article:queryLikePage:";

    /**
     * 用户点赞列表统计缓存
     */
    public static final String LIKE_USER_PAGE = "discover:api:like:user:queryLikePage:";

    /**
     * 商品编号和商品id对应关系缓存
     */
    public static final String PRODUCT_NO_ID_RELATION = "discover:api:product:noId:relation:";

    /**
     * 发现上传短视频id前缀
     */
    public static final String DISCOVER_ARTICLE_VOD_URL = "discover:article:vod:vodInfo";

    /**
     * 发现上传短视频fileId-vodLook
     */
    public static final String DISCOVER_UPLOAD_VOD_EVENT = "discover:vod:upload:vodInfo";

    /**
     * 文章关联话题历史
     */
    public static final String DISCOVER_ARTICLE_HISTORY_SUBJECT = "discover:article:history:subject";

  }
}
