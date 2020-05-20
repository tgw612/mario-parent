package com.mario.common.enums;

import com.mario.common.util.EnumUtil;

public enum AppName implements AppNameBase {
  DOUBO_SC("DOUBO_SC", "服务中心", "s"),
  DOUBO_BUYER_API("DOUBO_BUYER_API", "买家API", "b"),
  DOUBO_LIVE_API("DOUBO_LIVE_API", "卖家API", "s"),
  DOUBO_ADMIN_WEB("DOUBO_ADMIN_WEB", "后台管理", "w"),
  DOUBO_MQ("DOUBO_MQ", "MQ", "m"),
  DOUBO_CMQ("DOUBO_CMQ", "CMQ", "q");

  private String code;
  private String desc;
  private String codeNumber;

  private AppName(String code, String desc, String codeNumber) {
    this.code = code;
    this.desc = desc;
    this.codeNumber = codeNumber;
  }

  public static AppName resolveCodeNumber(String codeNumber) {
    return (AppName) EnumUtil.fromEnumValue(AppName.class, "codeNumber", codeNumber);
  }

  public static AppName resolve(String code) {
    return (AppName) EnumUtil.fromEnumValue(AppName.class, "code", code);
  }

  @Override
  public String toString() {
    return "AppName(code=" + this.getCode() + ", desc=" + this.getDesc() + ", codeNumber=" + this
        .getCodeNumber() + ")";
  }

  @Override
  public String getCode() {
    return this.code;
  }

  @Override
  public String getDesc() {
    return this.desc;
  }

  @Override
  public String getCodeNumber() {
    return this.codeNumber;
  }
}
