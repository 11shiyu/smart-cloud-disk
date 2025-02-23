package com.yupi.yupicturebackend.common;

import com.yupi.yupicturebackend.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;

//全局响应封装类 自定义接口调用返回信息, 支持序列化
@Data
public class BaseResponse<T> implements Serializable {


    private int code;
    private String message;
    private T data; //用泛型 接受各类对象

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.message = message;
        this.data = data;
    }


    public BaseResponse(int code, T data) {
        this(code, data, "");
    }


    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}
