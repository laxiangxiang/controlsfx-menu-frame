package com.shdq.menu_frame.http;

import com.alibaba.fastjson.JSON;

/**
 * 统一API响应结果封装
 */
public class Result<T> {
    private int code;
    private String message;
    private T data;

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Result setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
