package com.mario.common.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: Author: 陈二伟 Date:2018/10/31
 */
public abstract class LuaUtils {

  /**
   * 存放脚本 map
   */
  public static final Map<String, String> LUA_MAP = new ConcurrentHashMap<>();

  /**
   * lua脚本初始化开关
   */
  public static final String LUA_SWITCH_INIT = "lua.switch.init";

  /**
   * lua 脚本刷新开关
   */
  public static final String LUA_SWITCH_REFRESH = "lua.switch.refresh";

  /**
   * lua 脚本路径
   */
  public static final String LUA_PATH = "lua.path";


  /**
   * 货品减库存
   */
  public static final String GOODS_SUB_STOCK = "GoodsSubStock.lua";

  /**
   * 货品加库存
   */
  public static final String GOODS_PLUS_STOCK = "GoodsPlusStock.lua";

  /**
   * 拼团减库存
   */
  public static final String ACT_COLLAGE_SUB_STOCK = "ActCollageSubStock.lua";

  /**
   * 拼团加库存
   */
  public static final String ACT_COLLAGE_PLUS_STOCK = "ActCollagePlusStock.lua";

  /**
   * 秒杀减库存
   */
  public static final String FLASH_SALE_SUB_STOCK = "FlashSaleSubStock.lua";

  /**
   * 秒杀加库存
   */
  public static final String FLASH_SALE_PLUS_STOCK = "FlashSalePlusStock.lua";

}
