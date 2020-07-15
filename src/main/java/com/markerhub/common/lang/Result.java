package com.markerhub.common.lang;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> implements Serializable {

    private boolean status;
    private String code;
    private String msg;
    private T data;


    public static <T> Result success() {
        return new Result(true, ResponseConstant.RET_CODE_SUCCESS, ResponseConstant.RET_INFO_SUCCESS, null);

    }


    public static <T> Result success(Object data) {
        return new Result(true, ResponseConstant.RET_CODE_SUCCESS, ResponseConstant.RET_INFO_SUCCESS, data);

    }


    public static <T> Result failed() {
        return new Result(false, ResponseConstant.RET_CODE_FAILED, ResponseConstant.RET_INFO_FAILED, null);

    }


    public static <T> Result failed(String msg) {
        return new Result(false, ResponseConstant.RET_CODE_FAILED, msg, null);

    }


    public static <T> Result serverException() {
        return new Result(false, ResponseConstant.RET_CODE_SERVER_EXCEPTION, ResponseConstant.RET_INFO_SERVER_EXCEPTION, null);

    }


    public static <T> Result serverException(String msg) {
        return new Result(false, ResponseConstant.RET_CODE_SERVER_EXCEPTION, msg, null);

    }


    public static <T> Result paramsException() {
        return new Result(false, ResponseConstant.RET_CODE_PARAM_EXCEPTION, ResponseConstant.RET_INFO_PARAM_EXCEPTION, null);

    }


    public static <T> Result paramsException(String msg) {
        return new Result(false, ResponseConstant.RET_CODE_PARAM_EXCEPTION, msg, null);

    }


    public static <T> Result customException(String code, String msg) {
        return new Result(false, code, msg, null);

    }


    public static <T> Result customSuccess(String code, String msg,Object data) {
        return new Result(false, code, msg, data);

    }
}
