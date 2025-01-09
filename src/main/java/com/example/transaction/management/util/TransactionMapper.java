package com.example.transaction.management.util;

import java.math.BigDecimal;

import com.example.transaction.management.controller.param.TransactionCreateParam;
import com.example.transaction.management.controller.param.TransactionUpdateParam;
import com.example.transaction.management.controller.vo.TransactionVO;
import com.example.transaction.management.model.Transaction;
import com.example.transaction.management.model.Transaction.TransactionScene;
import com.example.transaction.management.model.Transaction.TransactionStatus;
import com.example.transaction.management.model.Transaction.TransactionType;
import com.example.transaction.management.model.TransactionDTO;

/**
 * @author jiasifan
 * Created on 2025-01-09
 */
public class TransactionMapper {


    public static Transaction toEntity(TransactionDTO transactionDTO) {
        if (transactionDTO == null) {
            return null;
        }
        Transaction transaction = new Transaction();
        if (transactionDTO.getId() > 0) {
            transaction.setId(transactionDTO.getId());
        }
        transaction.setFromAccountId(transactionDTO.getFromAccountId());
        transaction.setToAccountId(transactionDTO.getToAccountId());
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setCurrency(transactionDTO.getCurrency());
        transaction.setDescription(transactionDTO.getDescription());
        transaction.setType(transactionDTO.getType().getCode());
        transaction.setStatus(transactionDTO.getStatus().getCode());
        transaction.setScene(transactionDTO.getScene().getCode());
        transaction.setCreatedTime(transactionDTO.getCreatedTime());
        transaction.setInitiatedTime(transactionDTO.getInitiatedTime());
        transaction.setProcessedTime(transactionDTO.getProcessedTime());
        transaction.setCompletedTime(transactionDTO.getCompletedTime());
        transaction.setFailedTime(transactionDTO.getFailedTime());
        transaction.setExtParams(transactionDTO.getExtParams());
        return transaction;
    }

    public static TransactionDTO toDTO(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setId(transaction.getId());
        transactionDTO.setFromAccountId(transaction.getFromAccountId());
        transactionDTO.setToAccountId(transaction.getToAccountId());
        transactionDTO.setAmount(transaction.getAmount());
        transactionDTO.setCurrency(transaction.getCurrency());
        transactionDTO.setDescription(transaction.getDescription());
        transactionDTO.setType(TransactionType.fromCode(transaction.getType()));
        transactionDTO.setStatus(TransactionStatus.fromCode(transaction.getStatus()));
        transactionDTO.setScene(TransactionScene.fromCode(transaction.getScene()));
        transactionDTO.setCreatedTime(transaction.getCreatedTime());
        transactionDTO.setInitiatedTime(transaction.getInitiatedTime());
        transactionDTO.setProcessedTime(transaction.getProcessedTime());
        transactionDTO.setCompletedTime(transaction.getCompletedTime());
        transactionDTO.setFailedTime(transaction.getFailedTime());
        transactionDTO.setExtParams(transaction.getExtParams());
        return transactionDTO;
    }

    public static TransactionDTO toDTO(TransactionCreateParam createParam) {
        if (createParam == null) {
            return null;
        }
        TransactionDTO transactionDTO = new TransactionDTO();
        if (createParam.getId() > 0) { // 实际业务上也可能会有号码段的校验
            transactionDTO.setId(createParam.getId());
        }

        transactionDTO.setFromAccountId(createParam.getFromAccountId());
        transactionDTO.setToAccountId(createParam.getToAccountId());
        transactionDTO.setAmount(BigDecimal.valueOf(createParam.getAmount()));
        transactionDTO.setCurrency(createParam.getCurrency());
        transactionDTO.setDescription(createParam.getDescription());
        transactionDTO.setType(TransactionType.valueOf(createParam.getType()));
        transactionDTO.setStatus(TransactionStatus.valueOf(createParam.getStatus()));
        transactionDTO.setScene(TransactionScene.valueOf(createParam.getScene()));
        transactionDTO.setCreatedTime(createParam.getCreatedTime());
        transactionDTO.setInitiatedTime(createParam.getInitiatedTime());
        transactionDTO.setProcessedTime(createParam.getProcessedTime());
        transactionDTO.setCompletedTime(createParam.getCompletedTime());
        transactionDTO.setFailedTime(createParam.getFailedTime());
        transactionDTO.setExtParams(createParam.getExtParams());
        return transactionDTO;
    }

    public static TransactionDTO toDTO(TransactionUpdateParam updateParam) {
        if (updateParam == null || updateParam.getId() <= 0) {
            return null;
        }
        TransactionDTO transactionDTO = new TransactionDTO();
        if (updateParam.getId() > 0) { // 实际业务上也可能会有号码段的校验
            transactionDTO.setId(updateParam.getId());
        }

        transactionDTO.setFromAccountId(updateParam.getFromAccountId());
        transactionDTO.setToAccountId(updateParam.getToAccountId());
        transactionDTO.setAmount(BigDecimal.valueOf(updateParam.getAmount()));
        transactionDTO.setCurrency(updateParam.getCurrency());
        transactionDTO.setDescription(updateParam.getDescription());
        transactionDTO.setType(TransactionType.valueOf(updateParam.getType()));
        transactionDTO.setStatus(TransactionStatus.valueOf(updateParam.getStatus()));
        transactionDTO.setScene(TransactionScene.valueOf(updateParam.getScene()));
        transactionDTO.setCreatedTime(updateParam.getCreatedTime());
        transactionDTO.setInitiatedTime(updateParam.getInitiatedTime());
        transactionDTO.setProcessedTime(updateParam.getProcessedTime());
        transactionDTO.setCompletedTime(updateParam.getCompletedTime());
        transactionDTO.setFailedTime(updateParam.getFailedTime());
        transactionDTO.setExtParams(updateParam.getExtParams());
        return transactionDTO;
    }

    public static TransactionVO toVO(TransactionDTO transactionDTO) {
        if (transactionDTO == null) {
            return null;
        }
        TransactionVO transactionVO = new TransactionVO();
        transactionVO.setId(transactionDTO.getId());
        transactionVO.setFromAccountId(transactionDTO.getFromAccountId());
        transactionVO.setToAccountId(transactionDTO.getToAccountId());
        transactionVO.setAmount(String.valueOf(transactionDTO.getAmount()));
        transactionVO.setCurrency(transactionDTO.getCurrency());
        transactionVO.setDescription(transactionDTO.getDescription());
        transactionVO.setType(transactionDTO.getType().name());
        transactionVO.setStatus(transactionDTO.getStatus().name());
        transactionVO.setScene(transactionDTO.getScene().name());
        transactionVO.setCreatedTime(transactionDTO.getCreatedTime());
        transactionVO.setInitiatedTime(transactionDTO.getInitiatedTime());
        transactionVO.setProcessedTime(transactionDTO.getProcessedTime());
        transactionVO.setCompletedTime(transactionDTO.getCompletedTime());
        transactionVO.setFailedTime(transactionDTO.getFailedTime());
        transactionVO.setExtParams(transactionDTO.getExtParams());
        return transactionVO;
    }
}