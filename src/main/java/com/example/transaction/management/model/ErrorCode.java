package com.example.transaction.management.model;

/**
 * @author jiasifan
 * Created on 2025-01-11
 *
 * 用于定义 API 接口的错误码和错误信息，业务错误码，自定义号码段
 */
public enum ErrorCode {
    // 成功
    SUCCESS(0, "操作成功"),

    ERROR(9999, "操作失败, 请及时联系管理员"),

    // 创建相关的错误码
    DUPLICATE_RESOURCE_CREATE(1001, "资源已存在，无法重复创建"),

    // 更新相关的错误码
    RESOURCE_NOT_FOUND_UPDATE(1002, "尝试更新的资源不存在"),
    UPDATE_CONFLICT(1003, "更新操作导致的数据冲突"),

    // 删除相关的错误码
    RESOURCE_NOT_FOUND_DELETE(1004, "尝试删除的资源不存在"),

    // 获取单个资源相关的错误码
    RESOURCE_NOT_FOUND_GET(1005, "请求的资源不存在"),

    // 查询相关的错误码
    INVALID_PARAMETERS(1006, "参数无效或格式不正确"),

    // 其他自定义业务错误...

    ;

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}