package com.mario.common.id;

import com.mario.common.enums.BaseEnum;
import com.mario.common.util.EnumUtil;

public enum IdCodeTypeEnum implements BaseEnum<String> {
  ORDER_CODE("1", "订单号"),
  TRANSFER_ACCOUNT_CODE("2", "转账号"),
  WITHDRAW_CODE("3", "提现号"),
  LOG_ID("9", "日志ID"),
  ACTIVITY_CODE("11", "活动流水号"),
  FINANCE_MARKET_CODE("12", "财务模块集市币发放流水号"),
  FINANCE_COMMISSON_CODE("13", "财务模块佣金发放流水号"),
  PROMOTION_ACTIVITY_CODE("15", "促销活动编号"),
  PROMOTION_PRODUCT_CODE("16", "促销商品编号"),
  SERIAL_CODE("17", "流水号");

  private String code;
  private String desc;

  private IdCodeTypeEnum(String code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public static String getDesc(Integer code) {
    if (code == null) {
      return null;
    } else {
      IdCodeTypeEnum[] var1 = values();
      int var2 = var1.length;

      for (int var3 = 0; var3 < var2; ++var3) {
        IdCodeTypeEnum item = var1[var3];
        if (item.code.equals(code)) {
          return item.desc;
        }
      }

      return code + "";
    }
  }

  public static IdCodeTypeEnum resolve(Integer value) {
    return (IdCodeTypeEnum) EnumUtil.fromEnumValue(IdCodeTypeEnum.class, "code", value);
  }

  @Override
  public String getCode() {
    return this.code;
  }

  @Override
  public String getDesc() {
    return this.desc;
  }
}