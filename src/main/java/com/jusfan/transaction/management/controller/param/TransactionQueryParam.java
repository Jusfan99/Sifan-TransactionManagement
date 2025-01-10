package com.jusfan.transaction.management.controller.param;

import java.math.BigInteger;
import java.security.MessageDigest;

import lombok.Data;

/**
 * @author jiasifan
 * Created on 2025-01-10
 * 用于接收前端传来的查询参数
 */
@Data
public class TransactionQueryParam {
    private int page = 1; // 默认第一页
    private int size = 10; // 默认每页10条记录
    private String sortField; // 排序字段
    private String sortOrder; // 排序方式，如"asc"或"desc"

    private String fromAccountId;
    private String toAccountId;
    private String currency;
    private String type;
    private String status;
    private String scene;

    private Long createdTimeStart;
    private Long createdTimeEnd;
    private Long initiatedTimeStart;
    private Long initiatedTimeEnd;
    private Long processedTimeStart;
    private Long processedTimeEnd;
    private Long completedTimeStart;
    private Long completedTimeEnd;
    private Long failedTimeStart;
    private Long failedTimeEnd;

    /*
     * 下面两个方法用于根据参数生成唯一的缓存key，保证key长度不会太大
     */
    public String generateCacheKey() {
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(page).append(":");
        keyBuilder.append(size).append(":");
        keyBuilder.append(fromAccountId).append(":");
        keyBuilder.append(toAccountId).append(":");
        keyBuilder.append(currency).append(":");
        keyBuilder.append(type).append(":");
        keyBuilder.append(status).append(":");
        keyBuilder.append(scene).append(":");
        keyBuilder.append(createdTimeStart).append(":");
        keyBuilder.append(createdTimeEnd).append(":");
        keyBuilder.append(initiatedTimeStart).append(":");
        keyBuilder.append(initiatedTimeEnd).append(":");
        keyBuilder.append(processedTimeStart).append(":");
        keyBuilder.append(processedTimeEnd).append(":");
        keyBuilder.append(completedTimeStart).append(":");
        keyBuilder.append(completedTimeEnd).append(":");
        keyBuilder.append(failedTimeStart).append(":");
        keyBuilder.append(failedTimeEnd).append(":");
        keyBuilder.append(sortField).append(":");
        keyBuilder.append(sortOrder);

        return hashString(keyBuilder.toString());
    }

    private String hashString(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}