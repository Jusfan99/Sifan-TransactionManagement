package com.example.transaction.management.service;

import com.example.transaction.management.controller.param.TransactionCreateParam;
import com.example.transaction.management.controller.param.TransactionUpdateParam;
import com.example.transaction.management.controller.vo.TransactionVO;
import com.example.transaction.management.model.Transaction;
import com.example.transaction.management.model.TransactionDTO;
import com.example.transaction.management.util.TransactionMapper;

/**
 * @author jiasifan
 * Created on 2025-01-09
 */
public class TransactionMapperProxy {
    public Transaction toEntity(TransactionDTO transactionDTO) {
        return TransactionMapper.toEntity(transactionDTO);
    }

    public TransactionDTO toDTO(Transaction transaction) {
        return TransactionMapper.toDTO(transaction);
    }

    public TransactionDTO toDTO(TransactionCreateParam createParam) {
        return TransactionMapper.toDTO(createParam);
    }

    public TransactionDTO toDTO(TransactionUpdateParam updateParam) {
        return TransactionMapper.toDTO(updateParam);
    }

    public TransactionVO toVO(TransactionDTO transactionDTO) {
        return TransactionMapper.toVO(transactionDTO);
    }
}