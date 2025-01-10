package com.jusfan.transaction.management.controller.vo;

import lombok.Data;

/**
 * @author jiasifan
 * Created on 2025-01-10
 * for 前端展示
 */
@Data
public class TransactionVO {
    private long id;
    private String fromAccountId;
    private String toAccountId;
    private String amount; // 转换成字符串类型，避免精度丢失
    private String currency;
    private String description;
    private String type; // 转换成字符串类型，方便展示
    private String status;
    private String scene;
    private long createdTime;
    private long initiatedTime;
    private long processedTime;
    private long completedTime;
    private long failedTime;
    private String extParams;
}