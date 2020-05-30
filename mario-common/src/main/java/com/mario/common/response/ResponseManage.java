package com.mario.common.response;

import com.mario.common.exception.SystemException;
import com.mario.common.exception.code.ErrorCodeEnum;
import com.mario.common.model.response.CommonResponse;
import com.mario.common.util.StringUtil;
import org.springframework.validation.BindingResult;

public class ResponseManage {


    /**
     * 成功响应
     */
    public static <T> CommonResponse<T> success(T result) {
        /*if (result == null) {
            throw new ValidationException(ErrorCodeEnum.ERR_CHECK_PARAM.getErrorDesc());
        }
        CommonResponse response = new CommonResponse();
        response.setResult(result);
        response.setErrorCode(ErrorCodeEnum.SUCCEED_CODE.getErrorCode());
        response.setErrorMsg(ErrorCodeEnum.SUCCEED_CODE.getErrorDesc());*/
        return successIfNotNull(result);
    }

    /**
     * 根据结果判断返回成功或失败, 当不为空时，返回成功，否则失败
     *
     * @param result 结果, 当不为空时，返回成功，否则失败
     */
    public static <T> CommonResponse<T> successIfNotNull(T result) {
        CommonResponse<T> response = new CommonResponse<T>();
        response.setResult(result);
        if (response.isSuccess()) {
            response.setError(ErrorCodeEnum.SUCCEED_CODE);
        } else {
            response.setError(ErrorCodeEnum.BEAN_BIZ_ERROR);
        }
        return response;
    }

    /**
     * 根据结果判断返回成功或失败, 当不为空时，返回成功，否则失败
     *
     * @param result  结果, 当不为空时，返回成功，否则失败
     * @param nullMsg 对象为空提示信息
     */
    public static <T> CommonResponse<T> successIfNotNull(T result, String nullMsg) {
        CommonResponse<T> response = new CommonResponse<T>();
        response.setResult(result);
        if (response.isSuccess()) {
            response.setError(ErrorCodeEnum.SUCCEED_CODE);
        } else {
            if (StringUtil.isEmpty(nullMsg)) {
                response.setErrorMsg(ErrorCodeEnum.BEAN_BIZ_ERROR.getErrorDesc());
            } else {
                response.setErrorMsg(nullMsg);
            }
            response.setErrorCode(ErrorCodeEnum.BEAN_BIZ_ERROR.getErrorCode());
        }
        return response;
    }

    /**
     * 失败响应
     *
     * @param message 失败提示信息
     */
    public static <T> CommonResponse<T> fail(String message) {
        CommonResponse<T> response = new CommonResponse<T>();
        response.setErrorCode(ErrorCodeEnum.BEAN_BIZ_ERROR.getErrorCode());
        if (StringUtil.isEmpty(message)) {
            response.setErrorMsg(ErrorCodeEnum.BEAN_BIZ_ERROR.getErrorDesc());
        } else {
            //唯一约束异常
            response.setErrorMsg(message);
        }
        return response;
    }

    /**
     * 失败响应
     *
     * @param error 错误码以及错误信息
     */
    public static <T> CommonResponse<T> fail(ErrorCodeEnum error) {
        CommonResponse<T> response = new CommonResponse<T>();
        response.setError(error);
        return response;
    }

    /**
     * 失败响应
     *
     * @param error 错误码以及错误信息
     */
    public static <T> CommonResponse<T> failIfNotNull(T result, ErrorCodeEnum error) {
        CommonResponse<T> response = new CommonResponse<T>();
        response.setResult(result);
        response.setError(error);
        return response;
    }

    /**
     * 失败响应
     *
     * @param errorCode 错误码
     * @param message   错误信息
     */
    public static <T> CommonResponse<T> fail(int errorCode, String message) {
        CommonResponse<T> response = new CommonResponse<T>();
        response.setErrorCode(errorCode);
        response.setErrorMsg(message);
        return response;
    }

    /**
     * 失败响应
     *
     * @param ex 系统异常
     */
    public static <T> CommonResponse<T> fail(SystemException ex) {
        CommonResponse<T> response = new CommonResponse<T>();
        response.setErrorCode(ex.getErrCode());
        response.setErrorMsg(ex.getErrReason());
        return response;
    }

    /**
     * 失败响应-- 无效的参数
     */
    public static <T> CommonResponse<T> fail(BindingResult ex) {
        CommonResponse<T> response = new CommonResponse<T>();
        if (!ex.getFieldError().isBindingFailure()) {
            response.setErrorCode(ErrorCodeEnum.ERR_CHECK_PARAM.getErrorCode());
            response.setErrorMsg(ex.getFieldError().getDefaultMessage());
        } else {
            response.setErrorCode(ErrorCodeEnum.ERR_INVALID_PARAM.getErrorCode());
            response.setErrorMsg(ErrorCodeEnum.ERR_INVALID_PARAM.getErrorDesc());
        }
        return response;
    }

    /**
     * 失败响应--提示系统出错
     */
    public static <T> CommonResponse<T> failSysErr() {
        CommonResponse<T> response = new CommonResponse<T>();
        response.setError(ErrorCodeEnum.ERR_UNKNOW_ERROR);
        return response;
    }

    /**
     * 失败响应 -- 参数验证
     *
     * @param message 错误信息
     */
    public static <T> CommonResponse<T> failValidator(String message) {
        CommonResponse<T> response = new CommonResponse<T>();
        response.setErrorCode(ErrorCodeEnum.VALIDATOR_EXCEPTION.getErrorCode());
        if (StringUtil.isEmpty(message)) {
            response.setErrorMsg("未知的参数校验异常");
        } else {
            response.setErrorMsg(message);
        }
        return response;
    }

    /**
     * 失败响应-- 数据不存存在
     */
    public static <T> CommonResponse<T> failDataNotExist(String message) {
        CommonResponse<T> response = new CommonResponse<T>();
        response.setErrorCode(ErrorCodeEnum.DATA_NOT_EXIST.getErrorCode());
        if (StringUtil.isEmpty(message)) {
            response.setErrorMsg(ErrorCodeEnum.DATA_NOT_EXIST.getErrorDesc());
        } else {
            response.setErrorMsg(message);
        }
        return response;
    }

    /**
     * 失败响应 -- 数据重复存在响应
     */
    public static <T> CommonResponse<T> failDataExist(String message) {
        CommonResponse<T> response = new CommonResponse<T>();
        response.setErrorCode(ErrorCodeEnum.DATA_EXIST.getErrorCode());
        if (StringUtil.isEmpty(message)) {
            response.setErrorMsg(ErrorCodeEnum.DATA_EXIST.getErrorDesc());
        } else {
            response.setErrorMsg(message);
        }
        return response;
    }

    /**
     * 失败响应-- 无效的参数
     */
    public static <T> CommonResponse<T> failIllegalArgument(String message) {
        CommonResponse<T> response = new CommonResponse<T>();
        response.setErrorCode(ErrorCodeEnum.ERR_INVALID_PARAM.getErrorCode());
        if (StringUtil.isEmpty(message)) {
            response.setErrorMsg("无效的请求参数");
        } else {
            response.setErrorMsg(message);
        }
        return response;
    }

    /**
     * 失败响应 -- 数据库操作
     */
    public static <T> CommonResponse<T> failDbOperation(String message) {
        CommonResponse<T> response = new CommonResponse<T>();
        response.setErrorCode(ErrorCodeEnum.DATASOURCE_OPERTE_ERROR.getErrorCode());
        //唯一约束异常
        response.setErrorMsg(message);
        return response;
    }

    /**
     * 失败响应 -- 业务异常
     */
    public static <T> CommonResponse<T> failBizException(String message) {
        CommonResponse<T> response = new CommonResponse<T>();
        response.setErrorCode(ErrorCodeEnum.BEAN_BIZ_ERROR.getErrorCode());
        if (StringUtil.isEmpty(message)) {
            response.setErrorMsg(ErrorCodeEnum.BEAN_BIZ_ERROR.getErrorDesc());
        } else {
            response.setErrorMsg(message);
        }
        response.setResult(null);
        return response;
    }

    /**
     * 失败响应 -- 服务异常(微服务等)
     */
    public static <T> CommonResponse<T> failServiceException(String message) {
        CommonResponse<T> response = new CommonResponse<T>();
        response.setErrorCode(ErrorCodeEnum.BEAN_SERVICE_ERROR.getErrorCode());
        if (StringUtil.isEmpty(message)) {
            response.setErrorMsg(ErrorCodeEnum.BEAN_SERVICE_ERROR.getErrorDesc());
        } else {
            response.setErrorMsg(message);
        }
        response.setResult(null);
        return response;
    }

    /**
     * 调用远程服务异常
     */
    public static <T> CommonResponse<T> rpcException(String message) {
        CommonResponse<T> response = new CommonResponse<T>();
        response.setErrorCode(ErrorCodeEnum.RPC_EXCEPTION.getErrorCode());
        if (StringUtil.isEmpty(message)) {
            response.setErrorMsg(ErrorCodeEnum.RPC_EXCEPTION.getErrorDesc());
        } else {
            response.setErrorMsg(message);
        }
        response.setResult(null);
        return response;
    }

    /**
     * 调用远程服务超时
     */
    public static <T> CommonResponse<T> rpcTimeoutException(String message) {
        CommonResponse<T> response = new CommonResponse<T>();
        response.setErrorCode(ErrorCodeEnum.RPC_TIMEOUT_EXCEPTION.getErrorCode());
        if (StringUtil.isEmpty(message)) {
            response.setErrorMsg(ErrorCodeEnum.RPC_TIMEOUT_EXCEPTION.getErrorDesc());
        } else {
            response.setErrorMsg(message);
        }
        response.setResult(null);
        return response;
    }

    /**
     * 调用远程服务结果为null
     */
    public static <T> CommonResponse<T> rpcResNullException(String message) {
        CommonResponse<T> response = new CommonResponse<T>();
        response.setErrorCode(ErrorCodeEnum.RPC_RESPONSE_NULL_EXCEPTION.getErrorCode());
        if (StringUtil.isEmpty(message)) {
            response.setErrorMsg(ErrorCodeEnum.RPC_RESPONSE_NULL_EXCEPTION.getErrorDesc());
        } else {
            response.setErrorMsg(message);
        }
        response.setResult(null);
        return response;
    }

    /**
     * 限流
     */
    public static <T> CommonResponse<T> flowBlockException(String message) {
        CommonResponse<T> response = new CommonResponse<T>();
        response.setErrorCode(ErrorCodeEnum.FLOW_BLOCK_EXCEPTION.getErrorCode());
        if (StringUtil.isEmpty(message)) {
            response.setErrorMsg(ErrorCodeEnum.FLOW_BLOCK_EXCEPTION.getErrorDesc());
        } else {
            response.setErrorMsg(message);
        }
        response.setResult(null);
        return response;
    }

    /**
     * 降级
     */
    public static <T> CommonResponse<T> degradeBlockException(String message) {
        CommonResponse<T> response = new CommonResponse<T>();
        response.setErrorCode(ErrorCodeEnum.DEGRADE_BLOCK_EXCEPTION.getErrorCode());
        if (StringUtil.isEmpty(message)) {
            response.setErrorMsg(ErrorCodeEnum.DEGRADE_BLOCK_EXCEPTION.getErrorDesc());
        } else {
            response.setErrorMsg(message);
        }
        response.setResult(null);
        return response;
    }
}
