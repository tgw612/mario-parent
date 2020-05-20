package com.mario.common.exception.code;

import lombok.Getter;

/**
 * Description: Author: 陈二伟 Date:2018/10/31
 */
public enum LuaStockErrorEnum {

  NO_EXIST_KEYS_CODE("NO_EXIST_KEYS", "KEYS为空"),
  NO_EXIST_ARGV_CODE("NO_EXIST_ARGV", "ARGV为空"),
  MISS_PARAM_CODE("MISS_PARAM", "缺失参数 "),
  REDIS_MISS_KEY_CODE("REDIS_MISS_KEY", "redis 指定key 不存在"),
  UNDER_STOCK_CODE("UNDER_STOCK", "库存不足"),
  ERROR_CODE("ERROR", "异常"),
  SUCCESS_CODE("SUCCESS", "成功"),
  ;


  @Getter
  private String code;
  @Getter
  private String desc;

  LuaStockErrorEnum(String code, String desc) {
    this.code = code;
    this.desc = desc;
  }
}
