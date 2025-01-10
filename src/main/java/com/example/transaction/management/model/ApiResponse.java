package com.example.transaction.management.model;

import lombok.Data;

/**
 * @author jiasifan
 * Created on 2025-01-10
 *   ApiResponse 用于统一封装 API 接口的响应数据
 */
@Data
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;

    public ApiResponse(T data) {
        this.code = 1;
        this.message = "操作成功";
        this.data = data;
    }

    // 错误情况下的构造方法
    public ApiResponse(int code, String message) {
        this.code = code;
        this.message = message;
        this.data = null;
    }
}