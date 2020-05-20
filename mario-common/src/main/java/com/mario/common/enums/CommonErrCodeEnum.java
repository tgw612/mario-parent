package com.mario.common.enums;

import com.mario.common.error.CommonError;
import com.mario.common.util.EnumUtil;
import java.text.MessageFormat;

public enum CommonErrCodeEnum implements CommonError {
  SUCCEED_CODE(0, "成功"),
  SESSION_TIMEOUT(1, "会话超时，请重新登录"),
  ERR_CHECK_PARAM(1010, "参数校验失败"),
  BEAN_BIZ_ERROR(1099, "业务异常"),
  ERR_UNKNOW_ERROR(1999, "系统错误"),
  STREAM_TO_OBJECT_ERROR(1998, "从文件流读取对象出错"),
  OBJECT_TO_STREAM_ERROR(1997, "对象转换成文件流出错"),
  DATASOURCE_OPERTE_ERROR(1996, "数据库操作失败"),
  DATASOURCE_ERROR(1995, "数据库异常"),
  ERR_REMOTE_ACCESS_ERROR(1994, "远程访问出错"),
  ERR_REMOTE_ACCESS_TIMEOUT_ERROR(1993, "远程访问超时"),
  ERR_UNKNOWN_CLASS_ERROR(1992, "系统出错(1992)"),
  ERR_LOAD_CONFIG_ERROR(1991, "读取配置出错(1991)"),
  ERR_INSTANTIATION_ERROR(1990, "系统出错(1990)"),
  ERR_DBINDEX_NOT_FOUND_ERROR(1989, "用户数据配置出错"),
  ERR_ALI_SEARCH_ERROR(1988, "搜索异常"),
  BEAN_CONVERT_ERROR(1987, "对象转换失败"),
  ENUM_CONVERT_ERROR(1986, "枚举类型转换失败"),
  BEAN_VLUE_ERROR(1985, "获取属性值失败"),
  CREATOBJ_ERROR(1984, "对象创建失败"),
  OBJECT_TO_JSON_ERROR(1983, "对象转JSON失败"),
  JSON_TO_OBJECT_ERROR(1982, "JSON转对象失败"),
  OBJECT_SERIALIZATION_ERROR(1981, "对象序列化失败"),
  OBJECT_DESERIALIZATION_ERROR(1980, "对象反序列化失败"),
  REGISTRY_CENTER_ERROR(1979, "注册中心异常");

  private int errorCode;
  private String errorDesc;

  private CommonErrCodeEnum(int errorCode, String errorDesc) {
    this.errorCode = errorCode;
    this.errorDesc = errorDesc;
  }

  public static String getDesc(int errorCode) {
    CommonErrCodeEnum[] var1 = values();
    int var2 = var1.length;

    for (int var3 = 0; var3 < var2; ++var3) {
      CommonErrCodeEnum bussErrorCode = var1[var3];
      if (bussErrorCode.getErrorCode() == errorCode) {
        return bussErrorCode.errorDesc;
      }
    }

    return errorCode + "";
  }

  public String formatErrorDesc(Object... values) {
    return MessageFormat.format(this.getErrorDesc(), values);
  }

  public static CommonErrCodeEnum resolve(int value) {
    return (CommonErrCodeEnum) EnumUtil.fromEnumValue(CommonErrCodeEnum.class, "errorCode", value);
  }

  @Override
  public int getErrorCode() {
    return this.errorCode;
  }

  @Override
  public String getErrorDesc() {
    return this.errorDesc;
  }
}
