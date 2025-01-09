package com.example.transaction.management.model;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


/**
 * @author jiasifan
 * Created on 2025-01-09
 */
@Data
@AllArgsConstructor
@Builder
public class Transaction {

    // 发号器用于生成唯一的交易ID
    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);

    // 交易的唯一标识符
    private long id;

    // 转账发起账户ID
    private String fromAccountId;

    // 转账接收账户ID
    private String toAccountId;

    // 交易金额
    private BigDecimal amount;

    // 交易货币
    private String currency;

    // 交易描述（可选）
    private String description;

    // 交易类型，例如：借记、贷记
    private int type;

    // 交易状态，例如：待处理、已完成、失败
    private int status;

    // 交易场景，例如：ATM、POS、网上银行、移动支付
    private int scene;

    // Unix时间戳（毫秒），记录交易不同阶段的时间点
    private long createdTime; // 创建交易记录的时间
    private long initiatedTime; // 用户或系统发起交易的时间
    private long processedTime; // 交易被处理的时间（如审核后）
    private long completedTime; // 交易成功完成的时间
    private long failedTime; // 如果适用，交易失败的时间

    // 附加的元数据信息（可选）
    private String extParams; // 用于存储额外的交易信息

    // 构造函数，初始化时生成唯一的交易ID
    public Transaction() {
        this.id = ID_GENERATOR.incrementAndGet();
    }

    // Getters and setters are omitted for brevity

    // 交易类型的整数表示
    public enum TransactionType {
        UNKNOWN_TYPE(0),
        DEBIT(1),
        CREDIT(2);

        private final int code;

        TransactionType(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static TransactionType fromCode(int code) {
            for (TransactionType type : TransactionType.values()) {
                if (type.getCode() == code) {
                    return type;
                }
            }
            return UNKNOWN_TYPE;
        }
    }

    // 交易状态的整数表示
    public enum TransactionStatus {
        INVALID(0), //非法状态
        PENDING(1),
        COMPLETED(2),
        FAILED(3),
        REVERSED(4);

        private final int code;

        TransactionStatus(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static TransactionStatus fromCode(int code) {
            for (TransactionStatus status : TransactionStatus.values()) {
                if (status.getCode() == code) {
                    return status;
                }
            }
            return INVALID;
        }
    }

    // 交易场景的整数表示
    public enum TransactionScene {
        UNKNOWN_SCENE(0),
        ATM(1),
        POS(2),
        ONLINE_BANKING(3),
        MOBILE_PAYMENT(4),
        OTHER(5);

        private final int code;

        TransactionScene(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static TransactionScene fromCode(int code) {
            for (TransactionScene scene : TransactionScene.values()) {
                if (scene.getCode() == code) {
                    return scene;
                }
            }
            return UNKNOWN_SCENE;
        }
    }
}