package com.mario.common.exception.code;

import com.mario.common.error.CommonError;
import com.mario.common.util.EnumUtil;
import java.text.MessageFormat;
import lombok.Getter;

public enum ErrorCodeEnum implements CommonError {
    /**
     * 成功统一返回
     */
    SUCCEED_CODE(0, "成功"),

    SESSION_TIMEOUT(1, "会话超时，请重新登录"),

    NOT_REGISTER(2, "用户未注册"),

    /**
     * 用户的RefreshToken过期了
     */
    REFRESH_TOKEN_EXPIRED(3, "主Token已失效"),

    INVALID_ACCESS_TOKEN(4, "accessToken无法解析"),


    /**
     * 服务级别错误码10XX
     */
    BEAN_SERVICE_ERROR(1100, "服务异常"),
    /**
     * 业务级别错误码10XX
     */
    BEAN_BIZ_ERROR(1099, "业务异常"),

    TIMEOUT_EXCEPTION(1098, "服务超时"),


    ERR_CHECK_PARAM(1010, "参数校验失败"),

    ERR_INVALID_PARAM(1011, "请求参数无效"),

    VALIDATOR_EXCEPTION(1012, "参数校验异常"),

    ERR_CHECK_NORMAL_PARAM(1013, "请求参数中存在非法字符"),

    ERR_LOCK_TIME(1014, "操作超时，请重试"),

    REPEAT_SUBMIT(1015, "请求重复请稍候再试"),

    DATA_NOT_EXIST(1016, "数据不存在"),

    DATA_EXIST(1017, "数据重复存在"),

    USER_INFO_UN_KNOWN_ERROR(1016, "账号不存在"),

    USER_LOGIN_UN_KNOWN_ERROR(1017, "账号登录信息不存在"),


    ERR_SMS_ERROR(1030, "发送短信失败[{0}]"),


    /**
     * 访问权限级别错误码14XX
     */
    ERR_ACCESS_DENIED(1400, "对不起，您没有访问该资源的权限"),

    ERR_403_ERROR(1403, "禁止访问(403)"),

    ERR_404_ERROR(1404, "对不起，您访问资源不存在(404)"),

    ERR_500_ERROR(1405, "系统出错(500)"),


    /**
     * 系统级别错误码19XX
     */
    ERR_UNKNOW_ERROR(1999, "系统错误"),

    ERR_DBINDEX_NOT_FOUND_ERROR(1998, "用户数据配置出错"),

    STREAM_TO_OBJECT_ERROR(1997, "从文件流读取对象出错"),

    OBJECT_TO_STREAM_ERROR(1996, "对象转换成文件流出错"),

    DATASOURCE_OPERTE_ERROR(1995, "数据库操作失败"),

    DATASOURCE_ERROR(1994, "数据库异常"),

    ERR_REMOTE_ACCESS_ERROR(1994, "远程访问出错"),

    ERR_REMOTE_ACCESS_TIMEOUT_ERROR(1993, "远程访问超时"),

    ERR_UNKNOWN_CLASS_ERROR(1992, "系统出错(1992)"),

    ERR_LOAD_CONFIG_ERROR(1991, "读取配置出错(1991)"),

    ERR_INSTANTIATION_ERROR(1990, "系统出错(1990)"),

    BEAN_CONVERT_ERROR(1980, "对象转换失败"),

    OBJECT_TO_JSON_ERROR(1981, "对象转JSON失败"),

    CREATOBJ_ERROR(1982, "对象创建失败"),

    BEAN_VLUE_ERROR(1983, "获取属性值失败"),

    ENUM_CONVERT_ERROR(1984, "枚举类型转换失败"),

    ERR_EXPORT_ERROR(1983, "导出文件异常"),


    RPC_EXCEPTION(1970, "远程服务器异常，请稍后重试"),

    RPC_TIMEOUT_EXCEPTION(1971, "远程服务器超时，请稍后重试"),

    /**
     * 远程调用响应为空
     */
    RPC_RESPONSE_NULL_EXCEPTION(1972, "远程服务器异常，请确认请求是否成功再尝试发起"),

    /**
     * 远程RPC接口废弃（不支持使用）
     */
    RPC_SERVICE_DEPRECATED(1973, "远程服务接口已废弃，请升级"),


    /**
     * 限流
     */
    FLOW_BLOCK_EXCEPTION(6000, "亲，小主们太热情了，请稍后再试！"),

    /**
     * 降级
     */
    DEGRADE_BLOCK_EXCEPTION(6001, "亲，小主们太热情了，请稍后重试！"),

    ORDER_LIST_EXCEPTION(8001, "跳转都订单列表"),
    NOT_USER_REGISTER_TIME(1501, "无该用户注册时间信息"),
    NOT_RIGHT_REGISTER_TIME(1502, "注册时间不在活动时间范围内"),
    NOT_SHOP_REGISTER_TIME(1503, "无该店主注册时间信息"),;

    @Getter
    private int errorCode;
    @Getter
    private String errorDesc;

    ErrorCodeEnum(int errorCode, String errorDesc) {
        this.errorCode = errorCode;
        this.errorDesc = errorDesc;
    }

    public static String getDesc(int errorCode) {
        for (ErrorCodeEnum bussErrorCode : ErrorCodeEnum.values()) {
            if (bussErrorCode.getErrorCode() == errorCode) {
                return bussErrorCode.errorDesc;
            }
        }
        return errorCode + "";
    }

    public String formatErrorDesc(Object... values) {
        return MessageFormat.format(getErrorDesc(), values);
    }

    public static ErrorCodeEnum resolve(int value) {
        return EnumUtil.fromEnumValue(ErrorCodeEnum.class, "errorCode", value);
    }
}