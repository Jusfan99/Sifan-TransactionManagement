package com.example.transaction.management.model;

import java.math.BigDecimal;

import com.example.transaction.management.model.Transaction.TransactionScene;
import com.example.transaction.management.model.Transaction.TransactionStatus;
import com.example.transaction.management.model.Transaction.TransactionType;

import lombok.Data;

/**
 * @author jiasifan
 * Created on 2025-01-10
 * DTO
 */
@Data
public class TransactionDTO {
    private long id;
    private String fromAccountId;
    private String toAccountId;
    private BigDecimal amount;
    private String currency;
    private String description;
    private TransactionType type;
    private TransactionStatus status;
    private TransactionScene scene;
    private long createdTime;
    private long initiatedTime;
    private long processedTime;
    private long completedTime;
    private long failedTime;
    private String extParams;
}