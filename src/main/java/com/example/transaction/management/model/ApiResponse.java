package com.example.transaction.management.model;

import lombok.Data;

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